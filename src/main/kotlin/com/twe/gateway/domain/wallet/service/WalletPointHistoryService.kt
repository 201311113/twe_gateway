package com.tw2.prepaid.domain.wallet.service

import com.tw2.prepaid.domain.wallet.dto.response.WalletPointHistoryResponse
import com.tw2.prepaid.domain.wallet.dto.response.WalletPointHistorySimpleResponse
import com.tw2.prepaid.domain.wallet.model.repository.WalletPointHistoryRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class WalletPointHistoryService(
    private val walletPointHistoryRepository: WalletPointHistoryRepository,
    private val walletPointService: WalletPointService,
) {
    fun getWalletPointHistories(walletId: Long, pageable: Pageable,
                                startDt: LocalDate?, endDt: LocalDate?): WalletPointHistoryResponse {
        val histories = walletPointHistoryRepository
            .findWalletPointHistories(pageable = pageable, walletId = walletId, startDt = startDt, endDt = endDt)
            .map(WalletPointHistorySimpleResponse.Companion::createFromEntity)
        val balanceInfo = walletPointService.getWalletPoint(walletId)
        return WalletPointHistoryResponse(balanceInfo = balanceInfo, histories = histories)
    }
}