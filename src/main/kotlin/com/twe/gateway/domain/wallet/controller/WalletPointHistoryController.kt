package com.tw2.prepaid.domain.wallet.controller

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.wallet.service.WalletPointHistoryService
import com.tw2.prepaid.domain.wallet.dto.response.WalletPointHistoryResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping(path = ["/prepaid/v1.0/wallets/{walletId}/point-histories"])
class WalletPointHistoryController(
    private val walletPointHistoryService: WalletPointHistoryService
) {

    @GetMapping
    fun getWalletPointHistories(
        @PathVariable walletId: Long,
        @RequestParam startDt: LocalDate?,
        @RequestParam endDt: LocalDate?,
        @PageableDefault(sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Response<WalletPointHistoryResponse> =
        Response(
            data = walletPointHistoryService.getWalletPointHistories(walletId = walletId, pageable = pageable,
                startDt = startDt, endDt = endDt)
        )
}