package com.tw2.prepaid.domain.wallet.dto.response

import com.tw2.prepaid.domain.wallet.model.entity.Wallet
import com.tw2.prepaid.domain.wallet.model.entity.WalletPocket
import java.time.LocalDateTime

data class WalletDetailResponse(
    val id: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val totalBalance: Int,
    val pocketSize: Int,
    val pockets: List<WalletPocketResponse> = emptyList(),
    val partnerName: String,
    val currencies: Set<String>,
) {
    companion object {
        fun createFromEntity(entity: Wallet, totalBalance: Int, partnerName: String): WalletDetailResponse = entity.run {
            WalletDetailResponse(
                id = id,
                pocketSize = pockets.size,
                createdAt = createdAt,
                updatedAt = updatedAt,
                totalBalance = totalBalance,
                partnerName = partnerName,
                pockets = pockets.map(::createFromEntity),
                currencies = pockets.map(WalletPocket::currency).toSet()
            )
        }
    }
}
