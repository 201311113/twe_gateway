package com.tw2.prepaid.domain.wallet.controller

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.currency.model.CurrencyType
import com.tw2.prepaid.domain.wallet.dto.response.WalletHistoryResponse
import com.tw2.prepaid.domain.wallet.model.WalletActionType
import com.tw2.prepaid.domain.wallet.service.WalletHistoryService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping(path = ["/prepaid/v1.0/wallets/{walletId}"])
class WalletHistoryController(
    private val walletHistoryService: WalletHistoryService
) {
    @GetMapping("/histories")
    fun getWalletHistory(
        @PageableDefault(sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        @PathVariable walletId: Long,
        @RequestParam currency: CurrencyType?,
        @RequestParam startDt: LocalDate?,
        @RequestParam endDt: LocalDate?,
        @RequestParam actionTypes: List<WalletActionType>?,
    ): Response<Page<WalletHistoryResponse>> = Response(
        data = walletHistoryService.getWalletHistory(
            pageable = pageable, walletId = walletId, currency = currency?.name,
            startDt = startDt, endDt = endDt, actionTypes = actionTypes,
        )
    )
    @GetMapping("/histories/{historyId}")
    fun getCurrencyWalletHistoryDetail(
        @PathVariable walletId: Long,
        @PathVariable historyId: Long,
        @RequestParam historyDt: LocalDate?,
    ) = Response(
        data = walletHistoryService.getWalletHistoryDetail(
            walletId = walletId, historyId = historyId, historyDt = historyDt)
    )
}