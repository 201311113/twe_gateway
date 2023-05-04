package com.tw2.prepaid.domain.wallet.service

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.properties.SecretKey
import com.tw2.prepaid.common.support.annotation.PrimaryDb
import com.tw2.prepaid.common.utils.convertPositive
import com.tw2.prepaid.common.utils.isSame
import com.tw2.prepaid.common.utils.round
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositTargetRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankWithdrawRequest
import com.tw2.prepaid.domain.bank.service.OpenbankService
import com.tw2.prepaid.domain.currency.model.CurrencyType
import com.tw2.prepaid.domain.wallet.dto.request.*
import com.tw2.prepaid.domain.wallet.dto.response.WalletAtmBalanceInquiryResponse
import com.tw2.prepaid.domain.wallet.dto.response.WalletCardAdjustResponse
import com.tw2.prepaid.domain.wallet.dto.response.WalletCardAuthorizationResponse
import com.tw2.prepaid.domain.wallet.dto.response.WalletCardCancelResponse
import com.tw2.prepaid.domain.wallet.model.*
import com.tw2.prepaid.domain.wallet.model.entity.WalletHistory
import com.tw2.prepaid.domain.wallet.model.entity.WalletHistoryObDetail
import com.tw2.prepaid.domain.wallet.model.entity.WalletPocket
import com.tw2.prepaid.domain.wallet.model.repository.WalletHistoryObDetailRepository
import com.tw2.prepaid.domain.wallet.model.repository.WalletHistoryRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
@Service
class WalletCardService(
    private val wcs: WalletCommonService,
    private val walletHistoryRepository: WalletHistoryRepository,
    private val walletHistoryObDetailRepository: WalletHistoryObDetailRepository,
    private val openbankService: OpenbankService,
    private val historyService: WalletHistoryService,
    private val pp: PrepaidProperties,
) {
    @Transactional
    fun authorizeCard(
        walletId: Long, currency: CurrencyType,
        req: WalletCardAuthorizationRequest
    ): WalletCardAuthorizationResponse {
        val wallet = wcs.getWalletWithLock(walletId)
        val pocket = wcs.getOrCreatePocket(wallet = wallet, currency = currency.name)
        checkDuplicateRequest(req.accountTxId)

        val (cashUsage, pointUsage) = try {
            pocket.spendAmount(amount = req.amount, isForce = isAvailableMinusWallet(req, currentBalance = pocket.totalAmount()))
        } catch (ex: DefaultException) {
            when(ex.errorCode) {
                ErrorCode.잔액부족 -> {
                    throw DefaultException(
                        errorCode = ErrorCode.잔액부족,
                        data = WalletCardAuthorizationResponse(
                            walletHistoryId = -1,
                            balanceAmount = pocket.totalAmount()
                        )
                    )
                }
                else -> throw ex
            }
        }
        val historyId = historyService.saveWalletCardHistory(
            wallet = wallet, pocket = pocket, accountTxId = req.accountTxId,
            actionType = req.processType.getWalletActionType(AUTHORIZATION_PREFIX),
            cashAmount = -cashUsage, pointAmount = -pointUsage,
            additionalInfos = convertCommonHistoryField(req.additionalInfos)
        )
        return WalletCardAuthorizationResponse(
            walletHistoryId = historyId,
            balanceAmount = pocket.totalAmount()
        )
    }
    private fun isAvailableMinusWallet(req: WalletCardAuthorizationRequest, currentBalance: BigDecimal) =
        req.isForced || (req.amount.isSame(BigDecimal.ONE) && currentBalance - req.amount >= -BigDecimal.ONE)
    @Transactional
    fun cancelCard(
        walletId: Long, currency: CurrencyType,
        req: WalletCardCancelRequest
    ): WalletCardCancelResponse {
        val wallet = wcs.getWalletWithLock(walletId)
        val pocket = wcs.getOrCreatePocket(wallet = wallet, currency = currency.name)
        checkDuplicateRequest(req.accountTxId)

        val (cashAmount, pointAmount) = walletDeposit(
            originalAccountTxIds = req.originalAccountTxIds,
            amount = req.amount, pocket = pocket
        )
        val historyId = historyService.saveWalletCardHistory(
            wallet = wallet, pocket = pocket, accountTxId = req.accountTxId,
            actionType = req.processType.getWalletActionType(CANCEL_PREFIX),
            cashAmount = cashAmount, pointAmount = pointAmount,
            additionalInfos = convertCommonHistoryField(req.additionalInfos)
        )
        return WalletCardCancelResponse(
            walletHistoryId = historyId,
            balanceAmount = pocket.totalAmount()
        )
    }
    private fun walletDeposit(
        originalAccountTxIds: List<String>,
        amount: BigDecimal,
        pocket: WalletPocket
    ): Pair<BigDecimal, BigDecimal> {
        val walletHis = originalNormalAuthorizationWalletHistory(
            originalAccountTxIds = originalAccountTxIds,
            amount = amount
        )
        return when (walletHis) {
            is WalletHistory -> {
                // walletHis 에 적혀있는 원거래 숫자가 음수(승인)이기 때문에 -를 붙여서 계산
                pocket.plusBalance(cashAmount = -walletHis.cashAmount, pointAmount = -walletHis.pointAmount)
                -walletHis.cashAmount to -walletHis.pointAmount
            }
            else -> {
                pocket.plusBalance(cashAmount = amount)
                amount to BigDecimal.ZERO
            }
        }
    }
    private fun originalNormalAuthorizationWalletHistory(
        originalAccountTxIds: List<String>,
        amount: BigDecimal,
    ): WalletHistory? {
        if (originalAccountTxIds.isEmpty())
            throw DefaultException(errorCode = ErrorCode.원거래없음)
        else if (originalAccountTxIds.size > 1)
            return null

        val originalAccountTxId = originalAccountTxIds[0]
        val originAuthHis = walletHistoryObDetailRepository.findByAccountTxIdAndCreatedAtBetween(
            originalAccountTxId, LocalDateTime.now().minusMonths(2), LocalDateTime.now().plusDays(1)
        ) ?: throw DefaultException(errorCode = ErrorCode.원거래없음)

        val originWalletHis = walletHistoryRepository.findByIdAndCreatedAtBetween(
            originAuthHis.walletHistoryId, LocalDateTime.now().minusMonths(2), LocalDateTime.now().plusDays(1)
        ) ?: throw DefaultException(errorCode = ErrorCode.원거래없음)

        if (((originWalletHis.cashAmount + originWalletHis.pointAmount).abs() - amount.abs()).abs() > BigDecimal.ONE)
            return null

        return originWalletHis
    }
    private fun checkDuplicateRequest(accountTxId: String) = walletHistoryObDetailRepository.findByAccountTxIdAndCreatedAtBetween(
        accountTxId, LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(1)
    )?.let { throw DefaultException(httpStatus = HttpStatus.BAD_REQUEST, errorCode = ErrorCode.DUPLICATE_REQUEST) }
    @Transactional
    fun adjustCard(
        walletId: Long, currency: CurrencyType,
        req: WalletCardAdjustRequest
    ): WalletCardAdjustResponse {
        val wallet = wcs.getWalletWithLock(walletId)
        val pocket = wcs.getOrCreatePocket(wallet = wallet, currency = currency.name)
        checkDuplicateRequest(req.accountTxId)

        val historyId: Long
        if (req.amount > BigDecimal.ZERO) {
            val (cashUsage, pointUsage) = pocket.spendAmount(amount = req.amount, isForce = true)
            historyId = historyService.saveWalletCardHistory(
                wallet = wallet, pocket = pocket, actionType = WalletActionType.ADJUST,
                cashAmount = -cashUsage, pointAmount = -pointUsage, accountTxId = req.accountTxId,
                additionalInfos = convertCommonHistoryField(req.additionalInfos)
            )
        } else {
            val (cashAmount, pointAmount) = walletDeposit(
                originalAccountTxIds = req.originalAccountTxIds,
                amount = -req.amount, pocket = pocket
            )
            historyId = historyService.saveWalletCardHistory(
                wallet = wallet, pocket = pocket, actionType = WalletActionType.ADJUST,
                cashAmount = cashAmount, pointAmount = pointAmount, accountTxId = req.accountTxId,
                additionalInfos = convertCommonHistoryField(req.additionalInfos)
            )
        }
        return WalletCardAdjustResponse(walletHistoryId = historyId)
    }
    @Transactional
    fun atmBalanceInquiry(walletId: Long, currency: CurrencyType,
                          req: WalletAtmBalanceInquiryRequest
    ): WalletAtmBalanceInquiryResponse {
        val wallet = wcs.getWalletWithLock(walletId)
        val pocket = wcs.getOrCreatePocket(wallet = wallet, currency = currency.name)

        val (cashUsage, pointUsage) = pocket.spendAmount(amount = req.feeAmount, isForce = true)
        if (req.feeAmount > BigDecimal.ZERO)
            historyService.saveWalletCardHistory(
                wallet = wallet, pocket = pocket, actionType = WalletActionType.BALANCE_INQUIRY,
                cashAmount = -cashUsage, pointAmount = -pointUsage, accountTxId = req.accountTxId,
            )

        return WalletAtmBalanceInquiryResponse(
            ledgerBalance = pocket.totalAmount(),
            availableBalance = convertPositive(pocket.totalAmount()),
        )
    }
    @PrimaryDb
    fun accountWithdraw(walletId: Long, cardFeeType: CardFeeType, req: WalletBankWithdrawRequest) {
        val wallet = wcs.getWalletNoLocking(walletId)
        walletHistoryObDetailRepository.save(
            WalletHistoryObDetail(
                walletHistoryId = -1, walletId = wallet.id, accountTxId = req.accountTxId, actionType = cardFeeType.name
            )
        )
        // 고객의 계좌에서 인출 및 TW 계좌로 입금
        openbankService.withdraw(userId = wallet.user.id, walletId = walletId, businessUuid = req.accountTxId,
            originReq = OpenbankWithdrawRequest(
                dpsPrintContent = req.twPrintContent,
                fintechUseNum = req.accountId ?: wallet.getNotNullMainFintechNum(),
                reqClientFintechUseNum = req.accountId ?: wallet.getNotNullMainFintechNum(),
                tranAmt = req.amount.toString(),
                reqClientName = wallet.user.member.name ?: EMPTY_MESSAGE,
                reqClientNum = wallet.getUserSeqNo(),
                wdPrintContent = req.userPrintContent, // 사용자의 계좌에 어떻게 보여줄지에 대한 인자
            )
        )
    }
    @PrimaryDb
    fun accountDeposit(walletId: Long, cardFeeType: CardFeeType, req: WalletBankDepositRequest) {
        val wallet = wcs.getWalletNoLocking(walletId)
        walletHistoryObDetailRepository.save(
            WalletHistoryObDetail(
                walletHistoryId = -1, walletId = wallet.id, accountTxId = req.accountTxId, actionType = cardFeeType.name
            )
        )
        // 사용자 계좌 입금 처리중인 경우는 일단 성공으로 간주
        openbankService.deposit(walletId = walletId, userId = wallet.user.id, businessUuid = req.accountTxId,
            originReq = OpenbankDepositRequest(
                wdPassPhrase = pp.getSecretValue(SecretKey.OB_PASS_PHRASE), // 토큰으로서 상수로 갖고 있어야 함
                wdPrintContent = req.twPrintContent,
                reqList = listOf(
                    OpenbankDepositTargetRequest(
                        tranNo = "1",
                        fintechUseNum = req.accountId ?: wallet.getNotNullMainFintechNum(),
                        reqClientFintechUseNum = req.accountId ?: wallet.getNotNullMainFintechNum(),
                        printContent = req.userPrintContent, // 사용자의 계좌에 어떻게 보여줄지에 대한 인자
                        tranAmt = req.amount.toString(),
                        reqClientName = wallet.user.member.name ?: EMPTY_MESSAGE,
                        reqClientNum = wallet.getUserSeqNo(),
                    )
                )
            )
        )
    }
    private fun convertCommonHistoryField(map: Map<String, Any?>) = map.keys.associateWith {
        val value = map[it]
        if (value is Double) value.round() else value
    }
}