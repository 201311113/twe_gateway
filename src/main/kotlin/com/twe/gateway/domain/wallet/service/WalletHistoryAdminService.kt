package com.tw2.prepaid.domain.wallet.service

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.domain.wallet.dto.response.*
import com.tw2.prepaid.domain.wallet.model.WalletActionType
import com.tw2.prepaid.domain.wallet.model.entity.*
import com.tw2.prepaid.domain.wallet.model.repository.WalletHistoryExchangeDetailRepository
import com.tw2.prepaid.domain.wallet.model.repository.WalletHistoryRepository
import com.tw2.prepaid.domain.wallet.model.repository.WalletPocketRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class WalletHistoryAdminService(
    private val walletHistoryRepository: WalletHistoryRepository,
    private val walletPocketRepository: WalletPocketRepository,
    private val walletHistoryExchangeDetailRepository: WalletHistoryExchangeDetailRepository,
) {
    fun getWalletHistory(pageable: Pageable, walletId: Long, currency: String?,
                         startDt: LocalDate?, endDt: LocalDate?, actionTypes: List<WalletActionType>?): Page<WalletHistoryAdminResponse> {
        if (startDt != null && endDt != null && startDt.isAfter(endDt))
            throw DefaultException(errorCode = ErrorCode.BAD_REQUEST)

        val pageableWalletHis = walletHistoryRepository.findWalletHistories(
            pageable = pageable, walletId = walletId,
            currency = currency, startDt = startDt,
            endDt = endDt, actionTypes = actionTypes
        )
        val walletHisIds = pageableWalletHis.content.filter { it.actionType.isExchangeDetail }.map(WalletHistory::id).toSet()
        val detailHistories = walletHistoryExchangeDetailRepository.findAllByWalletHistoryIdIn(walletHisIds)

        return pageableWalletHis.map {
            createWalletHistoryAdminResponse(
                entity = it,
                detailEntity = if (walletHisIds.contains(it.id)) detailHistories.firstOrNull { detailHis -> detailHis.walletHistoryId == it.id } else null
            )
        }
    }
    fun getMinusWalletBalances(pageable: Pageable) =
        walletPocketRepository.findAllByCashBalanceLessThan(pageable = pageable).map(::createFromEntity)
}