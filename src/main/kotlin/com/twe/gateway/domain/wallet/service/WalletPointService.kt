package com.tw2.prepaid.domain.wallet.service

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.MAX_LOCAL_DATE
import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.properties.SecretKey
import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.common.support.annotation.PrimaryDb
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositTargetRequest
import com.tw2.prepaid.domain.bank.service.OpenbankService
import com.tw2.prepaid.domain.wallet.dto.request.WalletPointCreateRequest
import com.tw2.prepaid.domain.wallet.dto.request.WalletPointRefundRequest
import com.tw2.prepaid.domain.wallet.dto.request.WalletPointUseRequest
import com.tw2.prepaid.domain.wallet.dto.response.WalletValidPointResponse
import com.tw2.prepaid.domain.wallet.model.POINT_TYPE_PREFIX
import com.tw2.prepaid.domain.wallet.model.PointTransactionType
import com.tw2.prepaid.domain.wallet.model.entity.WalletHistoryObDetail
import com.tw2.prepaid.domain.wallet.model.entity.WalletPoint
import com.tw2.prepaid.domain.wallet.model.entity.WalletPointHistory
import com.tw2.prepaid.domain.wallet.model.repository.WalletHistoryObDetailRepository
import com.tw2.prepaid.domain.wallet.model.repository.WalletPointHistoryRepository
import com.tw2.prepaid.domain.wallet.model.repository.WalletPointRepository
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*

private val log = KotlinLogging.logger {}
@Service
class WalletPointService(
    private val walletPointRepository: WalletPointRepository,
    private val walletPointHistoryRepository: WalletPointHistoryRepository,
    private val walletHistoryObDetailRepository: WalletHistoryObDetailRepository,
    private val openbankService: OpenbankService,
    private val wcs: WalletCommonService,
    private val pp: PrepaidProperties,
) {
    fun getWalletPoint(walletId: Long) =
        totalValidPoint(getWalletPointEntities(walletId)).copy(todayPointRefundCnt = todayPointRefundCnt(walletId))
    @Transactional
    fun createWalletPoint(walletId: Long, req: WalletPointCreateRequest) {
        wcs.getWalletWithLock(walletId)
        // channel 별로 분리를 하지 않고 walletId, expiredDt, refundAvailable 별로 row 를 가지고 있어서
        // 나중에 삭제 이력 남길시 같은 만료일자의 다른 채널이력은 뭉게지긴함
        val walletPoint = walletPointRepository.findByWalletIdAndExpiredDt(walletId, req.expiredDt)
            .find { it.refundAvailable == req.refundAvailable } ?: walletPointRepository.save(req.toEntity(walletId))
        walletPoint.chargePoint(req.amount)
        walletPointHistoryRepository.save(req.toHistoryEntity(walletId = walletId, walletPointId = walletPoint.id))
    }
    @Transactional
    fun refundWalletPoints(walletId: Long, req: WalletPointRefundRequest): Response<WalletValidPointResponse> {
        val wallet = wcs.getWalletWithLock(walletId)
        val result = useWalletPoint(walletId = walletId, point = req.pointAmount, type = PointTransactionType.REFUND)
        val businessUuid = UUID.randomUUID().toString()

        walletHistoryObDetailRepository.save(
            WalletHistoryObDetail(
                walletHistoryId = -1,
                walletId = wallet.id,
                accountTxId = businessUuid,
                actionType = POINT_TYPE_PREFIX + PointTransactionType.REFUND.name,
            )
        )
        openbankService.deposit(
            walletId = walletId, userId = wallet.user.id,
            businessUuid = businessUuid,
            originReq = OpenbankDepositRequest(
                wdPassPhrase = pp.getSecretValue(SecretKey.OB_PASS_PHRASE), // 토큰으로서 상수로 갖고 있어야 함
                wdPrintContent = req.twPrintContent, // 트래블월렛 계좌에 어떻게 보여줄지에 대한 인자
                reqList = listOf(
                    OpenbankDepositTargetRequest(
                        tranNo = "1",
                        fintechUseNum = req.fintechUseNum ?: wallet.getNotNullMainFintechNum(),
                        reqClientFintechUseNum = wallet.getNotNullMainFintechNum(),
                        printContent = req.userPrintContent, // 사용자의 계좌에 어떻게 보여줄지에 대한 인자
                        tranAmt = req.pointAmount.toString(),
                        reqClientName = wallet.user.member.name ?: EMPTY_MESSAGE,
                        reqClientNum = wallet.getUserSeqNo(),
                    )
                )
            )
        )
        return Response(data = result)
    }
    // 지갑 충전 or 계좌 환불시 사용
    @Transactional
    fun useWalletPoint(
        walletId: Long,
        point: Int,
        channel: String = EMPTY_MESSAGE,
        memo: String? = null,
        displayDetail: String? = null,
        isForced: Boolean = false,
        isRefundableIfMinus: Boolean = true,
        type: PointTransactionType
    ): WalletValidPointResponse {
        if (point < 1)
            throw DefaultException(errorCode = ErrorCode.NOT_POSITIVE_AMOUNT)

        wcs.getWalletWithLock(walletId)
        val entities = getWalletPointEntities(walletId).toMutableList()
        validatePointUseBalance(points = entities, point = point, isForced = isForced, type = type)

        var usePoint = 0
        var oldestExpiredDt: LocalDate? = null

        run {
            entities
                .filter { selectTargetWalletPoint(it, type) }
                .forEach {
                    usePoint += it.usePoint(point - usePoint)
                    if (oldestExpiredDt == null && usePoint > 0) oldestExpiredDt = it.expiredDt
                    if (it.isEmpty()) walletPointRepository.delete(it)
                    if (usePoint >= point) return@run
                }
        }
        // usePoint 를 무조건 point 만큼 다 써야하는데 부족해서 마이너스로 남겨야 하는 상황
        if (isForced && usePoint < point) {
            var noExpiredPoint = entities.reversed().find { it.expiredDt == MAX_LOCAL_DATE && it.refundAvailable == isRefundableIfMinus }
            if (noExpiredPoint == null) {
                noExpiredPoint = walletPointRepository.save(WalletPoint(walletId = walletId, refundAvailable = isRefundableIfMinus, expiredDt = MAX_LOCAL_DATE))
                entities.add(noExpiredPoint)
            }
            noExpiredPoint.usePointForced(point - usePoint)
        }
        walletPointHistoryRepository.save(
            WalletPointCreateRequest(
                displayDetail = displayDetail,
                refundAvailable = isRefundableIfMinus,
                channel = channel,
                memo = memo,
                amount = point,
                transactionType = type,
                expiredDt = oldestExpiredDt ?: LocalDate.now(), // 포인트 쓴 만료일자 중 제일 최신 것으로 입력
            ).toHistoryEntity(walletId = walletId, walletPointId = -1)
        )
        return totalValidPoint(entities).copy(todayPointRefundCnt = todayPointRefundCnt(walletId))
    }
    @Transactional
    fun useWalletPointFromReq(walletId: Long, req: WalletPointUseRequest) = with(req) {
        useWalletPoint(
            walletId = walletId, point = amount, memo = memo, displayDetail = displayDetail,
            isForced = isForced, type = transactionType, isRefundableIfMinus = isRefundableIfMinus,
        )
    }
    private fun todayPointRefundCnt(walletId: Long) = walletPointHistoryRepository.countByWalletIdAndCreatedAtBetweenAndTransactionType(
        walletId = walletId,
        startDt = LocalDate.now().atStartOfDay(),
        endDt = LocalDate.now().plusDays(1).atStartOfDay(),
        tranType = PointTransactionType.REFUND,
    )
    private fun selectTargetWalletPoint(walletPoint: WalletPoint, type: PointTransactionType) =
        if (type == PointTransactionType.REFUND) walletPoint.refundAvailable else true
    private fun validatePointUseBalance(points: List<WalletPoint>, point: Int, isForced: Boolean, type: PointTransactionType) {
        if (isForced)
            return
        val validPoint = totalValidPoint(points)
        when (type) {
            PointTransactionType.REFUND ->
                if (validPoint.refundAvailableBalance < point)
                    throw DefaultException(errorCode = ErrorCode.포인트부족)
            else ->
                if (validPoint.totalAvailableBalance < point)
                    throw DefaultException(errorCode = ErrorCode.포인트부족)
        }
    }
    private fun totalValidPoint(entities: List<WalletPoint>): WalletValidPointResponse {
        val firstDayOfNextMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth())
        return entities.fold(WalletValidPointResponse()) { acc, walletPoint ->
            val (refundAvailableBalance, onlyChargeAvailableBalance) =
                if (walletPoint.refundAvailable) walletPoint.balance to 0
                else 0 to walletPoint.balance
            val (thisMonthRefundAvailableBalance, thisMonthOnlyChargeAvailableBalance) =
                if (firstDayOfNextMonth.isAfter(walletPoint.expiredDt)) refundAvailableBalance to onlyChargeAvailableBalance
                else 0 to 0
            WalletValidPointResponse(
                refundAvailableBalance = acc.refundAvailableBalance + refundAvailableBalance,
                onlyChargeAvailableBalance = acc.onlyChargeAvailableBalance + onlyChargeAvailableBalance,
                thisMonthExpiredRefundAvailableBalance = acc.thisMonthExpiredRefundAvailableBalance + thisMonthRefundAvailableBalance,
                thisMonthExpiredOnlyChargeAvailableBalance = acc.thisMonthExpiredOnlyChargeAvailableBalance + thisMonthOnlyChargeAvailableBalance,
                totalAvailableBalance = acc.totalAvailableBalance + walletPoint.balance,
                totalThisMonthAvailableBalance = acc.totalThisMonthAvailableBalance +
                        if (firstDayOfNextMonth.isAfter(walletPoint.expiredDt)) walletPoint.balance else 0,
            )
        }
    }
    private fun getWalletPointEntities(walletId: Long) =
        walletPointRepository.findAllByWalletIdAndExpiredDtAfter(
            walletId = walletId,
            expiredDt = LocalDate.now()
        ).sortedBy(WalletPoint::expiredDt)
    @PrimaryDb
    suspend fun batchDeleteExpiredPoints() {
        val expiredWalletPoints = walletPointRepository.findByExpiredDtBefore(LocalDate.now())
        val expiredWalletHistories = expiredWalletPoints.mapNotNull {
            if (it.balance == 0) null
            // walletPointId 를 가지는 만료되지 않은 history 를 뽑아와서 channel 각각 만료 이력으로 남길수는 있는데 넘 귀찮아서 그냥 channel 안남김
            // 사실 저 용도 아니면 walletPointId 지워도 된다.
            else WalletPointHistory(
                walletId = it.walletId,
                walletPointId = it.id,
                channel = EMPTY_MESSAGE,
                amount = it.balance,
                transactionType = PointTransactionType.EXPIRATION,
                refundAvailable = it.refundAvailable,
                expiredDt = it.expiredDt,
            )
        }
        expiredWalletPoints.chunked(300).forEach {
            walletPointRepository.deleteAll(it)
            delay(10L)
        }
        expiredWalletHistories.chunked(300).forEach {
            walletPointHistoryRepository.saveAll(it)
            delay(10L)
        }
    }
}