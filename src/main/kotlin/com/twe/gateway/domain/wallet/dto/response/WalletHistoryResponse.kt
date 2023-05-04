package com.tw2.prepaid.domain.wallet.dto.response

import com.tw2.prepaid.common.utils.toObject
import com.tw2.prepaid.domain.wallet.model.WalletActionType
import com.tw2.prepaid.domain.wallet.model.entity.WalletHistory
import java.math.BigDecimal
import java.time.LocalDateTime

data class WalletHistoryResponse(
    val id: Long,
    val createdAt: LocalDateTime,
    val currency: String,
    val actionType: WalletActionType,
    val cashAmount: BigDecimal,
    val pointAmount: BigDecimal,
    val totalAmount: BigDecimal,
    val cashBalance: BigDecimal,
    val pointBalance: BigDecimal,
    val totalBalance: BigDecimal,
    val additionalInfos: Map<String, Any?> = emptyMap()
)
fun createFromEntity(entity: WalletHistory): WalletHistoryResponse = entity.run {
    WalletHistoryResponse(
        id = id,
        currency = currency,
        actionType = actionType,
        cashAmount = cashAmount,
        pointAmount = pointAmount,
        totalAmount = cashAmount + pointAmount,
        cashBalance = cashBalance,
        pointBalance = pointBalance,
        totalBalance = cashBalance + pointBalance,
        additionalInfos = toObject(additionalInfos),
        createdAt = createdAt,
    )
}