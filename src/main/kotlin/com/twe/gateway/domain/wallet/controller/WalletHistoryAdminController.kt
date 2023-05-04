package com.tw2.prepaid.domain.wallet.controller

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.currency.model.CurrencyType
import com.tw2.prepaid.domain.wallet.dto.response.WalletHistoryAdminResponse
import com.tw2.prepaid.domain.wallet.dto.response.WalletPocketResponse
import com.tw2.prepaid.domain.wallet.model.WalletActionType
import com.tw2.prepaid.domain.wallet.service.WalletHistoryAdminService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping(path = ["/prepaid/v1.0/wallet-admin"])
class WalletHistoryAdminController(
    private val walletHistoryAdminService: WalletHistoryAdminService,
) {
    @GetMapping("/wallets/{walletId}/histories")
    fun getWalletHistory(
        @PageableDefault(sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        @PathVariable walletId: Long,
        @RequestParam currency: CurrencyType?,
        @RequestParam startDt: LocalDate?,
        @RequestParam endDt: LocalDate?,
        @RequestParam actionTypes: List<WalletActionType>?,
    ): Response<Page<WalletHistoryAdminResponse>> = Response(
        data = walletHistoryAdminService.getWalletHistory(
            pageable = pageable, walletId = walletId, currency = currency?.name,
            startDt = startDt, endDt = endDt, actionTypes = actionTypes,
        )
    )
    @GetMapping("/minus-balances")
    fun getMinusWalletBalances(@PageableDefault pageable: Pageable): Response<Page<WalletPocketResponse>> =
        Response(data = walletHistoryAdminService.getMinusWalletBalances(pageable = pageable))
}