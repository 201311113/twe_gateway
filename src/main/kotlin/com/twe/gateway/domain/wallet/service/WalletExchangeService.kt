package com.tw2.prepaid.domain.wallet.service

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.properties.SecretKey
import com.tw2.prepaid.common.utils.*
import com.tw2.prepaid.domain.bank.model.getBankNameDefaultEmpty
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositTargetRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankWithdrawRequest
import com.tw2.prepaid.domain.bank.dto.response.BankAccount
import com.tw2.prepaid.domain.bank.service.OpenbankService
import com.tw2.prepaid.domain.member.model.PartnerPaymentType.*
import com.tw2.prepaid.domain.wallet.dto.request.*
import com.tw2.prepaid.domain.wallet.dto.request.internal.WalletChargeInternalRequest
import com.tw2.prepaid.domain.wallet.dto.response.WalletTransferResponse
import com.tw2.prepaid.domain.wallet.model.LIMIT_KRW_AMOUNT
import com.tw2.prepaid.domain.wallet.model.PointTransactionType
import com.tw2.prepaid.domain.wallet.model.WalletActionType
import com.tw2.prepaid.domain.wallet.model.WalletHistoryType
import com.tw2.prepaid.domain.wallet.model.entity.Wallet
import com.tw2.prepaid.domain.wallet.model.entity.WalletPocket
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class WalletExchangeService(
    private val wcs: WalletCommonService,
    private val openbankService: OpenbankService,
    private val walletHistoryService: WalletHistoryService,
    private val walletPointService: WalletPointService,
    private val pp: PrepaidProperties,
) {
    @Transactional
    fun chargeWallet(walletId: Long, currency: String,
                     req: WalletChargeRequest, bank: BankAccount): WalletTransferResponse {
        val wallet = wcs.getWalletWithLock(walletId)
        val pocket = wcs.getOrCreatePocket(wallet = wallet, currency = currency)

        val finalLocalTotalAmt = req.localTotalAmount.floor(getCurrencyDigits(currency))
        val calculatedKrwAmount = finalLocalTotalAmt * req.spreadExchangeRate
        // req.domesticCashAmount 을 올림으로 줬다는 전제가 있다!!
        val krwCashAmount = req.domesticCashAmount ?: (calculatedKrwAmount.toCeilInt() - req.domesticPointAmount)
        val exchangeGains = krwCashAmount.toBigDecimal() - calculatedKrwAmount // 원화 올림 - 원화 원본

        val localPointAmount = if (isPointAll(req)) finalLocalTotalAmt else (req.domesticPointAmount.round() / req.spreadExchangeRate).ceil(getCurrencyDigits(currency))
        val localCashAmount = finalLocalTotalAmt - localPointAmount
        val businessUuid = UUID.randomUUID().toString()

        validateChargeRequest(
            req = req,
            totalBalance = wcs.getTotalBalance(wallet.pockets),
            calculatedKrwTotalAmount = calculatedKrwAmount.toCeilInt(),
        )
        val partnerPaymentType = if (krwCashAmount > 0 && wallet.user.partner.paymentType == OB) OB else NO_CASH
        val internalReq = WalletChargeInternalRequest(
            localCashAmount = localCashAmount,
            localPointAmount = localPointAmount,
            krwCashAmount = krwCashAmount,
            originalRequest = req,
            additionalInfos = additionalInfoDetailsMap(mutableMapOf(), bank),
            walletId = walletId,
            currency = currency,
            feeAmount = BigDecimal.ZERO,
            exchangeGains = exchangeGains,
            businessUuid = businessUuid,
            partnerPaymentType = partnerPaymentType
        )
        chargeWalletDb(req = internalReq, walletParam = wallet, pocketParam = pocket)
        // 고객의 계좌에서 인출 및 TW 계좌로 입금
        if (partnerPaymentType == OB) {
            openbankService.withdraw(
                userId = wallet.user.id, walletId = walletId, businessUuid = businessUuid,
                // 추후에 돈 빠져나갔을때 자동충전 하도록 한 기능인데 정합성을 위해 일단은 빼놓도록 한다. (금액 보상 처리)
                // additionalRetryInfo = mapOf(WALLET_CHARGE_REQ to internalReq),
                originReq = OpenbankWithdrawRequest(
                    dpsPrintContent = req.twPrintContent, // 트래블월렛 계좌에 어떻게 보여줄지에 대한 인자
                    fintechUseNum = req.fintechUseNum ?: wallet.getNotNullMainFintechNum(),
                    reqClientFintechUseNum = req.fintechUseNum ?: wallet.getNotNullMainFintechNum(),
                    tranAmt = krwCashAmount.toString(),
                    reqClientName = wallet.user.member.name ?: EMPTY_MESSAGE,
                    reqClientNum = wallet.getUserSeqNo(),
                    wdPrintContent = req.userPrintContent, // 사용자의 계좌에 어떻게 보여줄지에 대한 인자
                )
            )
        }
        return WalletTransferResponse(
                currency = currency,
                krwAmount = krwCashAmount + req.domesticPointAmount,
                pocketPointBalance = pocket.pointBalance,
                pocketCashBalance = pocket.cashBalance,
                pocketBalance = pocket.pointBalance + pocket.cashBalance
            )
    }
    private fun isPointAll(req: WalletChargeRequest) = req.domesticCashAmount == 0
    @Transactional
    fun eventChargeWallet(walletId: Long, currency: String, isForced: Boolean, req: WalletFreeChargeRequest): WalletTransferResponse {
        val wallet = wcs.getWalletWithLock(walletId)
        val pocket = wcs.getOrCreatePocket(wallet = wallet, currency = currency)
        val finalLocalTotalAmt = req.localCashAmount.floor(getCurrencyDigits(currency))

        if (!isForced && wcs.getTotalBalance(wallet.pockets) + (req.baseExchangeRate * finalLocalTotalAmt).toCeilInt() > LIMIT_KRW_AMOUNT)
            throw DefaultException(httpStatus = HttpStatus.BAD_REQUEST, errorCode = ErrorCode.충전금액초과)

        pocket.plusBalance(cashAmount = finalLocalTotalAmt, isForce = isForced)
        walletHistoryService.saveWalletExchangeHistory(
            wallet = wallet,
            pocket = pocket,
            actionType = req.actionType,
            historyType = WalletHistoryType.CHARGE,
            localCashAmount = finalLocalTotalAmt,
            baseExchangeRate = req.baseExchangeRate,
            spreadExchangeRate = req.baseExchangeRate,
            usdSpreadExchangeRate = req.usdSpreadExchangeRate,
            partnerPaymentType = NO_CASH,
            businessUuid = UUID.randomUUID().toString(),
            additionalInfos = req.additionalInfos,
        )
        return WalletTransferResponse(
            currency = currency,
            krwAmount = 0,
            pocketPointBalance = pocket.pointBalance,
            pocketCashBalance = pocket.cashBalance,
            pocketBalance = pocket.pointBalance + pocket.cashBalance
        )
    }
    @Transactional
    fun chargeWalletDb(req: WalletChargeInternalRequest, pocketParam: WalletPocket? = null, walletParam: Wallet? = null) {
        val wallet = walletParam ?: wcs.getWalletWithLock(req.walletId)
        val pocket = pocketParam ?: wcs.getOrCreatePocket(wallet = wallet, currency = req.currency)
        with(req) {
            // 지갑 DB 로 입금
            pocket.plusBalance(cashAmount = localCashAmount, pointAmount = localPointAmount)
            // 포인트 차감
            if (originalRequest.domesticPointAmount > 0)
                walletPointService.useWalletPoint(
                    walletId = walletId, point = originalRequest.domesticPointAmount, type = PointTransactionType.USE
                )
            walletHistoryService.saveWalletExchangeHistory(
                wallet = wallet,
                pocket = pocket,
                actionType = WalletActionType.CHARGE,
                historyType = WalletHistoryType.CHARGE,
                localCashAmount = localCashAmount,
                localPointAmount = localPointAmount,
                krwCashAmount = krwCashAmount,
                krwPointAmount = originalRequest.domesticPointAmount,
                baseExchangeRate = originalRequest.baseExchangeRate,
                spreadExchangeRate = originalRequest.spreadExchangeRate,
                usdSpreadExchangeRate = originalRequest.usdSpreadExchangeRate,
                twFee = feeAmount,
                krwExchangeGains = exchangeGains,
                partnerPaymentType = partnerPaymentType,
                businessUuid = businessUuid,
                additionalInfos = additionalInfos,
            )
        }
    }
    private fun validateChargeRequest(req: WalletChargeRequest, totalBalance: Int, calculatedKrwTotalAmount: Int) {
        if (req.domesticCashAmount != null && diffIsBig(req.domesticCashAmount + req.domesticPointAmount, calculatedKrwTotalAmount, 3))
            throw DefaultException(httpStatus = HttpStatus.BAD_REQUEST, errorCode = ErrorCode.최신환율아님)
        // 충전 금액이 180만원 초과 되는 경우
        if (totalBalance + calculatedKrwTotalAmount > LIMIT_KRW_AMOUNT)
            throw DefaultException(httpStatus = HttpStatus.BAD_REQUEST, errorCode = ErrorCode.충전금액초과)
    }

    @Transactional
    fun refundWallet(walletId: Long, currency: String,
                     req: WalletRefundRequest, bank: BankAccount): WalletTransferResponse {
        val wallet = wcs.getWalletWithLock(walletId)
        val pocket = wcs.getOrCreatePocket(wallet = wallet, currency = currency)

        val calculatedKrwAmount = req.localTotalAmount * req.spreadExchangeRate - req.refundFee
        val krwCashAmount = req.domesticTotalAmount ?: calculatedKrwAmount.toFloorInt()
        val exchangeGains = calculatedKrwAmount - krwCashAmount.toBigDecimal()
        val businessUuid = UUID.randomUUID().toString()

        validateRefundRequest(req = req, calculatedKrwAmount = calculatedKrwAmount.toFloorInt())

        // 지갑 DB 에서 인출
        pocket.plusBalance(cashAmount = -req.localTotalAmount)
        val partnerPaymentType = if (krwCashAmount > 0 && wallet.user.partner.paymentType == OB) OB else NO_CASH
        walletHistoryService.saveWalletExchangeHistory(
            wallet = wallet,
            pocket = pocket,
            actionType = WalletActionType.REFUND,
            historyType = WalletHistoryType.REFUND,
            localCashAmount = -req.localTotalAmount,
            krwCashAmount = krwCashAmount,
            baseExchangeRate = req.baseExchangeRate,
            spreadExchangeRate = req.spreadExchangeRate,
            usdSpreadExchangeRate = req.usdSpreadExchangeRate,
            twFee = req.refundFee,
            krwExchangeGains = exchangeGains,
            partnerPaymentType = partnerPaymentType,
            businessUuid = businessUuid,
            additionalInfos = additionalInfoDetailsMap(mutableMapOf(), bank)
        )

        // TW 계좌에 돈을 빼서 사용자 계좌로의 입금
        // 처리중은 성공으로 간주 (지갑 선차감)
        if (partnerPaymentType == OB) {
            openbankService.deposit(
                walletId = walletId, userId = wallet.user.id, businessUuid = businessUuid,
                originReq = OpenbankDepositRequest(
                    wdPassPhrase = pp.getSecretValue(SecretKey.OB_PASS_PHRASE), // 토큰으로서 상수로 갖고 있어야 함
                    wdPrintContent = req.twPrintContent, // 트래블월렛 계좌에 어떻게 보여줄지에 대한 인자
                    reqList = listOf(
                        OpenbankDepositTargetRequest(
                            tranNo = "1",
                            fintechUseNum = req.fintechUseNum ?: wallet.getNotNullMainFintechNum(),
                            reqClientFintechUseNum = req.fintechUseNum ?: wallet.getNotNullMainFintechNum(),
                            printContent = req.userPrintContent, // 사용자의 계좌에 어떻게 보여줄지에 대한 인자
                            tranAmt = krwCashAmount.toString(),
                            reqClientName = wallet.user.member.name ?: EMPTY_MESSAGE,
                            reqClientNum = wallet.getUserSeqNo(),
                        )
                    )
                )
            )
        }
        return WalletTransferResponse(
                currency = currency,
                krwAmount = krwCashAmount,
                pocketPointBalance = pocket.pointBalance,
                pocketCashBalance = pocket.cashBalance,
                pocketBalance = pocket.pointBalance + pocket.cashBalance,
        )
    }
    @Transactional
    fun exchangeWallet(walletId: Long, req: WalletExchangeRequest) {
        val wallet = wcs.getWalletWithLock(walletId)
        val fromPocket = wcs.getOrCreatePocket(wallet = wallet, currency = req.fromCurrency.name)
        val toPocket = wcs.getOrCreatePocket(wallet = wallet, currency = req.toCurrency.name)

        val krwAmount = req.fromAmount * req.spreadFromExchangeRate
        req.domesticAmount?.let {
            if ((it - krwAmount).abs() > (3).toBigDecimal())
                throw DefaultException(httpStatus = HttpStatus.BAD_REQUEST, errorCode = ErrorCode.최신환율아님)
            // toAmount 의 소수점을 전부 안보내면 3원 차이로 커버가 안된다.
            if ((req.toAmount * req.baseToExchangeRate - krwAmount).abs() > (3).toBigDecimal())
                throw DefaultException(httpStatus = HttpStatus.BAD_REQUEST, errorCode = ErrorCode.최신환율아님)
        }
        // 18.. 나눗셈은 BigDecimal 소숫점이 작은쪽으로 뭉개짐..
        val pennyExchangeRate = req.baseToExchangeRate.round(7) / req.spreadFromExchangeRate.round(7)
        val finalFromAmount = req.fromAmount.ceil(getCurrencyDigits(req.fromCurrency.name))
        val finalToAmount = req.toAmount.floor(getCurrencyDigits(req.toCurrency.name))

        fromPocket.plusBalance(cashAmount = -finalFromAmount)
        walletHistoryService.saveWalletExchangeHistory(
            wallet = wallet,
            pocket = fromPocket,
            actionType = WalletActionType.EXCHANGE_FROM,
            historyType = WalletHistoryType.REFUND,
            localCashAmount = -finalFromAmount,
            baseExchangeRate = req.baseFromExchangeRate,
            spreadExchangeRate = req.spreadFromExchangeRate,
            usdSpreadExchangeRate = req.usdFromSpreadExchangeRate,
            partnerPaymentType = NO_CASH,
            krwExchangeGains = (finalFromAmount - req.fromAmount) * req.spreadFromExchangeRate,
            businessUuid = UUID.randomUUID().toString(),
            additionalInfos = mapOf<String, Any>(
                "exchangeRate" to pennyExchangeRate,
                "toAmount" to finalToAmount,
                "toCurrency" to req.toCurrency.name,
            ),
        )
        toPocket.plusBalance(cashAmount = finalToAmount)
        walletHistoryService.saveWalletExchangeHistory(
            wallet = wallet,
            pocket = toPocket,
            actionType = WalletActionType.EXCHANGE_TO,
            historyType = WalletHistoryType.CHARGE,
            localCashAmount = finalToAmount,
            baseExchangeRate = req.baseToExchangeRate,
            spreadExchangeRate = req.baseToExchangeRate,
            usdSpreadExchangeRate = req.usdToSpreadExchangeRate,
            partnerPaymentType = NO_CASH,
            krwExchangeGains = (req.toAmount - finalToAmount) * req.baseToExchangeRate,
            businessUuid = UUID.randomUUID().toString(),
            additionalInfos = mapOf<String, Any>(
                "exchangeRate" to pennyExchangeRate,
                "fromAmount" to finalFromAmount,
                "fromCurrency" to req.fromCurrency.name,
            ),
        )
    }
    @Transactional
    fun clearWallet(walletId: Long, req: WalletClearRequest) {
        val wallet = wcs.getWalletWithLock(walletId)
        wallet.pockets.forEach {
            if (it.cashBalance < BigDecimal.ZERO || it.pointBalance < BigDecimal.ZERO)
                throw DefaultException(errorCode = ErrorCode.잔액부족)
            if (it.cashBalance.isZero() && it.pointBalance.isZero())
                return@forEach

            // 이전 잔액을 쓸 돈으로 미리 저장해둬야 함
            val cashAmount = -it.cashBalance
            val pointAmount = -it.pointBalance
            it.plusBalance(cashAmount = cashAmount, pointAmount = pointAmount)
            walletHistoryService.saveWalletCardHistory(
                wallet = wallet, pocket = it, actionType = WalletActionType.ADJUST,
                cashAmount = cashAmount, pointAmount = pointAmount,
                accountTxId = UUID.randomUUID().toString(),
                additionalInfos = req.additionalInfos
            )
        }
    }
    private fun validateRefundRequest(req: WalletRefundRequest, calculatedKrwAmount: Int) {
        if (req.domesticTotalAmount != null && diffIsBig(req.domesticTotalAmount, calculatedKrwAmount, 3))
            throw DefaultException(httpStatus = HttpStatus.BAD_REQUEST, errorCode = ErrorCode.최신환율아님)
    }
    private fun additionalInfoMap(infoMap: MutableMap<String, Any>, vararg infos: Pair<String, Any>) =
        infoMap.also { it.putAll(infos) }
    private fun additionalInfoDetailsMap(additionalInfo: MutableMap<String, Any>, bank: BankAccount) =
        additionalInfoMap(
            additionalInfo,
            "bankName" to getBankNameDefaultEmpty(bank.bankCodeStd),
            "accountNumMasked" to bank.accountNumMasked,
        )
}