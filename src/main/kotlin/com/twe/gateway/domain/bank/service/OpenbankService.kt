package com.tw2.prepaid.domain.bank.service

import com.tw2.prepaid.common.*
import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.error.ErrorCodeHolder
import com.tw2.prepaid.common.jpa.oddments.OddmentsCategory
import com.tw2.prepaid.common.jpa.oddments.OddmentsHolderEntity
import com.tw2.prepaid.common.jpa.oddments.OddmentsHolderEntityRepository
import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.properties.SecretKey
import com.tw2.prepaid.common.support.redis.CHARGE_ERROR_KEY
import com.tw2.prepaid.common.support.redis.RedisRepository
import com.tw2.prepaid.common.support.redis.makeRedisKey
import com.tw2.prepaid.common.support.retry.RetryJobType
import com.tw2.prepaid.common.support.retry.SqsRetryTemplate
import com.tw2.prepaid.common.utils.isReadTimeoutException
import com.tw2.prepaid.domain.bank.feign.ObNonExchangeApiClient
import com.tw2.prepaid.domain.bank.common.convertErrorCodeFromOpenBanking
import com.tw2.prepaid.domain.bank.model.OpenbankAccountRegisterType
import com.tw2.prepaid.domain.bank.model.OpenbankAccountStateType
import com.tw2.prepaid.domain.bank.model.OpenbankTransferResultEnum
import com.tw2.prepaid.domain.bank.model.OpenbankTransferResultEnum.*
import com.tw2.prepaid.domain.bank.model.ParticipatingBankResponseCode
import com.tw2.prepaid.domain.bank.model.entity.OpenbankTransferHistory
import com.tw2.prepaid.domain.bank.model.repository.OpenbankTransferHistoryRepository
import com.tw2.prepaid.domain.bank.dto.request.openbank.*
import com.tw2.prepaid.domain.bank.dto.response.*
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankAccountResponse
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankRegisterAccountResponse
import com.tw2.prepaid.domain.bank.feign.ObExchangeApiClient
import com.tw2.prepaid.domain.bank.retry.OB_HISTORY_REQ
import com.tw2.prepaid.domain.member.model.PartnerPaymentType
import com.tw2.prepaid.domain.wallet.service.WalletCommonService
import feign.FeignException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}
val successCode = ErrorCodeHolder(errorCode = ErrorCode.SUCCESS, msg = EMPTY_MESSAGE)
const val CHARGE_ERROR_WAIT_TIMEOUT: Long = 60 * 60 * 1000

// 다른 서비스에서 openbank api 호출할때 직접 호출하지말고 여기 서비스를 이용하도록 한다.
@Service
class OpenbankService(
    private val obNonExchangeApiClient: ObNonExchangeApiClient,
    private val obExchangeApiClient: ObExchangeApiClient,
    private val openbankTransferHistoryRepository: OpenbankTransferHistoryRepository,
    private val retryTemplate: SqsRetryTemplate,
    private val oddmentsHolderEntityRepository: OddmentsHolderEntityRepository,
    private val wcs: WalletCommonService,
    private val redisRepository: RedisRepository,
    private val pp: PrepaidProperties,
) {

    // @Transactional 삭제. 어차피 현재 list 는 한개로만 쓰고 있고 save 안에서 transaction 이 걸리기 때문이다.
    // Transaction Scope 안에 외부 호출로 인한 대기 시간이 많으면 성능에 안좋긴함.
    fun deposit(originReq: OpenbankDepositRequest, userId: Long,
                walletId: Long, businessUuid: String,
                useMainAccount: Boolean = true,): BankDepositResponse {
        val partner = wcs.getWalletNoLocking(walletId).user.partner
        var request = originReq
        var tranResult = DONE
        with(partner) {
            if (paymentType == PartnerPaymentType.OB) {
                request = request.copy(
                    cntrAccountNum = (if (useMainAccount) mainAccountNum else subAccountNum) ?: TW_ACCOUNT_NUM
                )
            } else return BankDepositResponse()
        }

        val result = try {
            obExchangeApiClient.deposit(request)
        } catch (ex: FeignException) {
            if (isReadTimeoutException(ex.cause)) { // 성공으로 처리
                log.error("$OB_ADJUSTMENT_MARKER$OB_ADJUSTMENT_READ_TIMEOUT[DEPOSIT-SUCCESS]$request")
                tranResult = READ_TIMEOUT
                toObDepositResponse(request)
            }
            else throw ex
        }
        var errorCodeHolder = successCode

        result.resList.forEach {
            errorCodeHolder = convertErrorCodeFromOpenBanking(result.rspCode, it.bankRspCode, result.rspMessage + " " + it.bankRspMessage)
            val userSeqNum = request.reqList.find { req -> req.tranNo == it.tranNo }?.reqClientNum ?:
                throw DefaultException(errorCode = ErrorCode.오픈뱅킹에러)

            when (errorCodeHolder.errorCode) {
                ErrorCode.SUCCESS -> {}
                ErrorCode.PROCESSING_BANK_TRANSFER -> {
                    log.error("$OB_ADJUSTMENT_MARKER$OB_ADJUSTMENT_PROCESSING[DEPOSIT-SUCCESS]$request")
                    tranResult = PROCESSING
                    errorCodeHolder = successCode // 고객 입금 처리중은 성공으로 처리
                }
                else -> throw DefaultException(
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
                    errorCode = ErrorCode.오픈뱅킹에러,
                    message = errorCodeHolder.getMessage(),
                    isErrorLog = errorCodeHolder.isErrorLog,
                    data = ObErrorResponse(apiRetCode = result.rspCode, bankRetCode = it.bankRspCode)
                )
            }
            val history = openbankTransferHistoryRepository.save(it.toOpenbankTransferHistoryEntity(
                userSeqNum = userSeqNum, tranResult = tranResult, businessUuid = businessUuid,
                response = result, userId = userId, walletId = walletId))
            if (tranResult == PROCESSING || tranResult == READ_TIMEOUT) {
                retryTemplate.pushJob(
                    retryJobType = RetryJobType.OB_DEPOSIT,
                    data = mapOf(RetryJobType.OB_DEPOSIT.name to request, OB_HISTORY_REQ to history)
                )
            }
        }
        return of(result, errorCodeHolder)
    }
    fun withdraw(originReq: OpenbankWithdrawRequest, userId: Long,
                 useMainAccount: Boolean = true, businessUuid: String,
                 walletId: Long, additionalRetryInfo: Map<String, Any> = emptyMap()
    ): BankWithdrawResponse {
        // 출금 처리중인 에러가 있을시 같은 계좌의 출금은 막는다.
        if (redisRepository.hasKey(makeChargeErrorKey(walletId, originReq.fintechUseNum)))
            throw DefaultException(errorCode = ErrorCode.EXCHANGE_ERROR_PROCESSING)
        val partner = wcs.getWalletNoLocking(walletId).user.partner
        var request = originReq
        with(partner) {
            if (paymentType == PartnerPaymentType.OB) {
                request = request.copy(
                    cntrAccountNum = (if (useMainAccount) mainAccountNum else subAccountNum) ?: TW_ACCOUNT_NUM,
                )
            } else return BankWithdrawResponse()
        }

        val result = try {
            obExchangeApiClient.withdraw(request)
        } catch (ex: FeignException) {
            if (isReadTimeoutException(ex.cause)) {
                log.error("$OB_ADJUSTMENT_MARKER$OB_ADJUSTMENT_READ_TIMEOUT[WITHDRAW-FAIL]$request")
                val history = toObWithdrawResponse(request).toOpenbankTransferHistoryEntity(
                    userSeqNum = request.reqClientNum,
                    businessUuid = businessUuid,
                    userId = userId,
                    walletId = walletId,
                    tranResult = READ_TIMEOUT
                )
                redisRepository.save(key = makeChargeErrorKey(walletId, request.fintechUseNum), timeoutMillis = CHARGE_ERROR_WAIT_TIMEOUT)
                retryTemplate.pushJob(
                    retryJobType = RetryJobType.OB_WITHDRAW,
                    data = mapOf(RetryJobType.OB_WITHDRAW.name to request, OB_HISTORY_REQ to history) + additionalRetryInfo
                )
            }
            throw ex // 무조건 실패로 처리
        }
        var errorCodeHolder = convertErrorCodeFromOpenBanking(result.rspCode, result.bankRspCode, result.rspMessage + " " + result.bankRspMessage)
        when (errorCodeHolder.errorCode) {
            ErrorCode.SUCCESS ->
                openbankTransferHistoryRepository.save(
                    result.toOpenbankTransferHistoryEntity(
                        userSeqNum = request.reqClientNum,
                        businessUuid = businessUuid,
                        userId = userId,
                        walletId = walletId,
                        tranResult = DONE
                    )
                )
            ErrorCode.PROCESSING_BANK_TRANSFER -> {
                log.error("$OB_ADJUSTMENT_MARKER$OB_ADJUSTMENT_PROCESSING[WITHDRAW-FAIL]$request")
                val history = result.toOpenbankTransferHistoryEntity(
                    userSeqNum = request.reqClientNum,
                    businessUuid = businessUuid,
                    userId = userId,
                    walletId = walletId,
                    tranResult = PROCESSING
                )
                redisRepository.save(key = makeChargeErrorKey(walletId, request.fintechUseNum), timeoutMillis = CHARGE_ERROR_WAIT_TIMEOUT)
                retryTemplate.pushJob(
                    retryJobType = RetryJobType.OB_WITHDRAW,
                    data = mapOf(RetryJobType.OB_WITHDRAW.name to request, OB_HISTORY_REQ to history) + additionalRetryInfo
                )
                // 고객 계좌 출금 처리중은 에러처리
                throw DefaultException(
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
                    errorCode = ErrorCode.PROCESSING_BANK_TRANSFER,
                )
            }
            else -> throw DefaultException(
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
                errorCode = ErrorCode.오픈뱅킹에러,
                message = errorCodeHolder.getMessage(),
                isErrorLog = errorCodeHolder.isErrorLog,
                data = ObErrorResponse(apiRetCode = result.rspCode, bankRetCode = result.bankRspCode)
            )
        }
        return of(result, errorCodeHolder)
    }
    private fun makeChargeErrorKey(walletId: Long, fintechUseNum: String) = makeRedisKey(
        mainKey = CHARGE_ERROR_KEY,
        subKeys = mapOf("walletId" to walletId.toString(), "fintechNum" to fintechUseNum)
    )
    // retry 확인 내부용 (거래 확인)
    fun getTransferResult(req: OpenbankTransferRequest): BankTransferResponse {
        val result = obNonExchangeApiClient.getTransferResult(req)
        var errorCode = ErrorCodeHolder(errorCode = ErrorCode.SUCCESS, msg = EMPTY_MESSAGE)

        result.resList.forEach {
            errorCode = convertErrorCodeFromOpenBanking(result.rspCode, it.bankRspCode, result.rspMessage + " " + it.bankRspMessage)
            if (errorCode.errorCode != ErrorCode.SUCCESS)
                throw DefaultException(
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
                    errorCode = errorCode.errorCode,
                    message = errorCode.getMessage(),
                    data = ObErrorResponse(apiRetCode = result.rspCode, bankRetCode = it.bankRspCode)
                )
        }

        return of(response = result, errorCode = errorCode.errorCode)
    }
    fun getAccountBalance(fintechUseNum: String): BankBalanceResponse {
        val result = obNonExchangeApiClient.getAccountBalance(fintechUseNum = fintechUseNum)
        val resultCode = convertErrorCodeFromOpenBanking(result.rspCode, result.bankRspCode,
            result.rspMessage + " " + result.bankRspMessage)
        if (resultCode.errorCode != ErrorCode.SUCCESS)
            throw DefaultException(
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
                errorCode = ErrorCode.오픈뱅킹에러,
                message = resultCode.getMessage(),
                data = ObErrorResponse(apiRetCode = result.rspCode, bankRetCode = result.bankRspCode)
            )
        return create(result)
    }
    @Transactional
    fun changeHistoryTransferStatus(req: OpenbankTransferHistory, transferResultEnum: OpenbankTransferResultEnum? = null) {
        val history = openbankTransferHistoryRepository.findByBusinessUuidAndCreatedAtBetween(
            businessUuid = req.businessUuid,
            startDt = LocalDate.now().minusDays(1).atStartOfDay(),
            endDt = LocalDate.now().plusDays(1).atStartOfDay()
        ) ?: openbankTransferHistoryRepository.save(req)
        history.tranResult = transferResultEnum ?: history.tranResult
    }
    fun registerAccount(req: OpenbankRegisterAccountRequest): OpenbankRegisterAccountResponse {
        val result = obNonExchangeApiClient.registerAccount(req)
        val resultCode = convertErrorCodeFromOpenBanking(
            result.rspCode, result.bankRspCode, result.rspMessage + " " + result.bankRspMessage
        )
        if (resultCode.errorCode != ErrorCode.SUCCESS)
            throw DefaultException(
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
                errorCode = resultCode.errorCode,
                message = resultCode.getMessage(),
                data = ObErrorResponse(apiRetCode = result.rspCode, bankRetCode = result.bankRspCode)
            )
        return result
    }
    // 이 경우는 해지 케이스라 에러의 경우도 pass
    fun unregisterAccount(fintechUseNum: String) {
        OpenbankAccountRegisterType.values().forEach {
            obNonExchangeApiClient.cancelAccount(
                OpenbankCancelAccountRequest(
                    scope = it,
                    fintechUseNum = fintechUseNum
                )
            )
        }
    }
    // 출금 / 조회 동의중 하나만 되있어도 계좌 리스트에 보임, activeState 는 동의와 노상관
    fun getAccounts(userSeqNum: String? = null, walletId: Long): List<BankAccount> {
        val userSeqNo = userSeqNum ?: wcs.getWalletNoLocking(walletId).user.userSeqNum ?:
            throw DefaultException(errorCode = ErrorCode.NOT_EXIST_MAIN_ACCOUNT)
        val result = obNonExchangeApiClient.getAccounts(userSeqNo = userSeqNo)
        val resultCode = convertErrorCodeFromOpenBanking(result.rspCode, IGNORE, result.rspMessage)
        if (resultCode.errorCode != ErrorCode.SUCCESS)
            throw DefaultException(
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
                errorCode = ErrorCode.오픈뱅킹에러,
                message = result.rspMessage,
                data = ObErrorResponse(apiRetCode = result.rspCode, bankRetCode = ParticipatingBankResponseCode.`000`.name)
            )

        return result.resList?.map {
            BankAccount.create(it, isAvailableBank(result.rspCode, it))
        } ?: emptyList()
    }
    private fun isAvailableBank(apiRspCode: String, bankResponse: OpenbankAccountResponse) =
        convertErrorCodeFromOpenBanking(
            apiRspCode,
            bankResponse.bankCodeStd,
            IGNORE
        ).errorCode == ErrorCode.SUCCESS &&
        bankResponse.accountState == OpenbankAccountStateType.USE &&
        bankResponse.inquiryAgreeYn == YnType.Y &&
        bankResponse.transferAgreeYn == YnType.Y
    fun closeUser(userSeqNo: String): BankCloseUserResponse {
        val result = obNonExchangeApiClient.closeUser(OpenbankCloseUserRequest(
            clientUseCode = pp.getSecretValue(SecretKey.OB_TRANSACTION_KEY),
            userSeqNo = userSeqNo
        ))

        return BankCloseUserResponse(
            rspCode = result.rspCode,
            rspMessage = result.rspMessage
        )
    }
    @Transactional
    fun refreshAccessToken() {
        val now = LocalDateTime.now()
        val entity = oddmentsHolderEntityRepository.queryByCategory(OddmentsCategory.OB_ACCESS_TOKEN)
            ?: oddmentsHolderEntityRepository.save(
                OddmentsHolderEntity(
                    oddment = obNonExchangeApiClient.refreshAccessTokenUnLogging(req = OpenbankRefreshAccessTokenRequest()).accessToken,
                    category = OddmentsCategory.OB_ACCESS_TOKEN
                )
            )

        // 00:00 ~ 00:10
        if (now.hour == 0 && now.minute in (1..9) && entity.updatedAt.plusDays(83).isBefore(now)) {
            entity.oddment = obNonExchangeApiClient.refreshAccessTokenUnLogging(req = OpenbankRefreshAccessTokenRequest()).accessToken
            log.info("ob access token is refreshed!!")
        }
    }
}