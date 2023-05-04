package com.tw2.prepaid.domain.wallet.dto.response

import com.tw2.prepaid.domain.wallet.model.entity.WalletPocket
import java.math.BigDecimal
import java.time.LocalDateTime

data class WalletPocketResponse(
    val id: Long,
    val walletId: Long,
    val currency: String,
    val cashBalance: BigDecimal,
    val pointBalance: BigDecimal,
    val totalBalance: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
fun createFromEntity(entity: WalletPocket): WalletPocketResponse = entity.run {
    WalletPocketResponse(
        id = id,
        walletId = wallet.id,
        currency = currency,
        cashBalance = cashBalance,
        pointBalance = pointBalance,
        totalBalance = cashBalance + pointBalance,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}