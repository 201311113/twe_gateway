package com.tw2.prepaid.domain.wallet.controller

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.common.support.annotation.PrimaryDb
import com.tw2.prepaid.domain.bank.dto.response.BankAccount
import com.tw2.prepaid.domain.bank.service.OpenbankService
import com.tw2.prepaid.domain.currency.model.CurrencyType
import com.tw2.prepaid.domain.wallet.dto.request.*
import com.tw2.prepaid.domain.wallet.dto.response.WalletTransferResponse
import com.tw2.prepaid.domain.wallet.service.WalletCommonService
import com.tw2.prepaid.domain.wallet.service.WalletExchangeService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/prepaid/v1.0/wallets/{walletId}"])
class WalletExchangeController(
    private val walletExchangeService: WalletExchangeService,
    private val openbankService: OpenbankService,
    private val wcs: WalletCommonService,
) {
    @PostMapping("/{currency}:charge")
    @PrimaryDb
    fun chargeWallet(
        @PathVariable walletId: Long,
        @PathVariable currency: CurrencyType,
        @Validated @RequestBody request: WalletChargeRequest
    ): Response<WalletTransferResponse> {
        val bank = getAccount(walletId, request.fintechUseNum ?: wcs.getWalletNoLocking(walletId).getNotNullMainFintechNum())
        return Response(data = walletExchangeService.chargeWallet(walletId, currency.name, request, bank))
    }
    @PostMapping("/{currency}:free-charge")
    @PrimaryDb
    fun freeChargeWallet(
        @PathVariable walletId: Long,
        @PathVariable currency: CurrencyType,
        @RequestParam isForced: Boolean? = false,
        @RequestBody request: WalletFreeChargeRequest
    ): Response<WalletTransferResponse> = Response(data = walletExchangeService.eventChargeWallet(walletId, currency.name, isForced = isForced ?: false, request))
    @PostMapping("/{currency}:refund")
    @PrimaryDb
    fun refundWallet(
        @PathVariable walletId: Long,
        @PathVariable currency: CurrencyType,
        @Validated @RequestBody request: WalletRefundRequest
    ): Response<WalletTransferResponse> {
        val bank = getAccount(walletId, request.fintechUseNum ?: wcs.getWalletNoLocking(walletId).getNotNullMainFintechNum())
        return Response(data = walletExchangeService.refundWallet(walletId, currency.name, request, bank))
    }
    @PostMapping("/exchange")
    fun exchangeWallet(
        @PathVariable walletId: Long,
        @Validated @RequestBody request: WalletExchangeRequest
    ): Response<Unit> {
        return Response(data = walletExchangeService.exchangeWallet(walletId, request))
    }
    @DeleteMapping
    fun clearWallet(@PathVariable walletId: Long, @RequestBody request: WalletClearRequest): Response<Unit> =
        Response(data = walletExchangeService.clearWallet(walletId, request))
    private fun getAccount(walletId: Long, fintechUseNum: String?): BankAccount {
        val banks = openbankService.getAccounts(walletId = walletId)
        return banks.find { it.fintechUseNum == fintechUseNum }
            ?: throw DefaultException(errorCode = ErrorCode.NOT_EXIST_TARGET_ACCOUNT)
    }
}