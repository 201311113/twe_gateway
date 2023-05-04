package com.tw2.prepaid.domain.wallet.dto.response

import com.tw2.prepaid.domain.wallet.model.entity.Wallet
import java.time.LocalDateTime

data class WalletResponse(
    val id: Long,
    val totalBalance: Int,
    val pocketSize: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun createFromEntity(entity: Wallet, totalBalance: Int): WalletResponse = entity.run {
            WalletResponse(
                id = id,
                pocketSize = pockets.size,
                createdAt = createdAt,
                updatedAt = updatedAt,
                totalBalance = totalBalance
            )
        }
    }
}