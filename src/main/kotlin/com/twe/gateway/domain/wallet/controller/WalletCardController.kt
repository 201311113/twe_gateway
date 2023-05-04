package com.tw2.prepaid.domain.wallet.controller

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.currency.model.CurrencyType
import com.tw2.prepaid.domain.wallet.service.WalletCardService
import com.tw2.prepaid.domain.wallet.dto.request.*
import com.tw2.prepaid.domain.wallet.dto.response.*
import com.tw2.prepaid.domain.wallet.model.CardFeeType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/prepaid/v1.0/wallets/{walletId}"])
class WalletCardController(
    private val walletCardService: WalletCardService,
) {
    @PostMapping("/{currency}:authorize")
    fun authorizeCard(
        @PathVariable walletId: Long,
        @PathVariable currency: CurrencyType,
        @Validated @RequestBody request: WalletCardAuthorizationRequest
    ): Response<WalletCardAuthorizationResponse> =
        Response(data = walletCardService.authorizeCard(walletId = walletId, currency = currency, req = request))
    @PostMapping("/{currency}:reverse")
    fun cancelCard(
        @PathVariable walletId: Long,
        @PathVariable currency: CurrencyType,
        @Validated @RequestBody request: WalletCardCancelRequest
    ): Response<WalletCardCancelResponse> =
        Response(data = walletCardService.cancelCard(walletId = walletId, currency = currency, req = request))
    @PostMapping("/{currency}:adjust")
    fun adjustCard(
        @PathVariable walletId: Long,
        @PathVariable currency: CurrencyType,
        @Validated @RequestBody request: WalletCardAdjustRequest
    ): Response<WalletCardAdjustResponse> =
        Response(data = walletCardService.adjustCard(walletId = walletId, currency = currency, req = request))

    @PostMapping("/{currency}:inquiry-atm-balance")
    fun atmBalanceInquiry(
        @PathVariable walletId: Long,
        @PathVariable currency: CurrencyType,
        @Validated @RequestBody request: WalletAtmBalanceInquiryRequest,
    ): Response<WalletAtmBalanceInquiryResponse> =
        Response(data = walletCardService.atmBalanceInquiry(walletId = walletId, currency = currency, req = request))
    @PostMapping("/{cardFeeType}:withdraw-account")
    fun accountWithdraw(
        @PathVariable walletId: Long,
        @PathVariable cardFeeType: CardFeeType,
        @Validated @RequestBody request: WalletBankWithdrawRequest,
    ): Response<Unit> = Response(data = walletCardService.accountWithdraw(walletId = walletId, cardFeeType = cardFeeType, req = request))

    @PostMapping("/{cardFeeType}:deposit-account")
    fun accountDeposit(
        @PathVariable walletId: Long,
        @PathVariable cardFeeType: CardFeeType,
        @Validated @RequestBody request: WalletBankDepositRequest,
    ): Response<Unit> = Response(data = walletCardService.accountDeposit(walletId = walletId, cardFeeType = cardFeeType, req = request))
}