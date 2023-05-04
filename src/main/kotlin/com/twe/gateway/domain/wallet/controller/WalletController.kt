package com.tw2.prepaid.domain.wallet.controller

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.currency.model.CurrencyType
import com.tw2.prepaid.domain.wallet.dto.request.*
import com.tw2.prepaid.domain.wallet.dto.response.*
import com.tw2.prepaid.domain.wallet.service.*
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping(path = ["/prepaid/v1.0/wallets"])
class WalletController(
    private val walletSimpleService: WalletSimpleService,
) {
    @GetMapping
    fun getWallets(@RequestParam partnerId: Long?, @PageableDefault pageable: Pageable) =
        Response(data = walletSimpleService.getWallets(partnerId, pageable))

    @PostMapping
    fun createWallet(@RequestBody request: WalletCreateRequest): Response<WalletResponse> =
        Response(data = walletSimpleService.createWallet(request))

    @GetMapping("/{walletId}")
    fun getWallet(@PathVariable walletId: Long): Response<WalletDetailResponse> =
        Response(data = walletSimpleService.getWallet(walletId))

    @GetMapping("/{walletId}/pockets")
    fun getPockets(@PathVariable walletId: Long): Response<List<WalletPocketResponse>> = Response(
        data = walletSimpleService.getPockets(walletId)
    )
    @GetMapping("/{walletId}/pocket-currencies")
    fun getPocketCurrencies(@PathVariable walletId: Long): Response<List<String>> = Response(
        data = walletSimpleService.getPocketCurrencies(walletId)
    )
    @PostMapping("/{walletId}/pockets")
    fun createPocket(
        @Valid @RequestBody request: WalletPocketCreateRequest,
        @PathVariable walletId: Long
    ): Response<WalletPocketResponse> = Response(data = walletSimpleService.createPocket(walletId = walletId, request = request))

    @GetMapping("/{walletId}/total-balance")
    fun getTotalBalance(@PathVariable walletId: Long) =
        Response(data = walletSimpleService.getTotalBalance(walletId))

    @GetMapping("/{walletId}/{currency}")
    fun getPocketByCurrency(
        @PathVariable walletId: Long,
        @PathVariable currency: CurrencyType
    ) = Response(data = walletSimpleService.getPocket(walletId, currency.name))
}