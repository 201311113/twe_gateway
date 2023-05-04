package com.tw2.prepaid.domain.wallet.dto.response

import com.tw2.prepaid.common.utils.toObject
import com.tw2.prepaid.domain.wallet.model.PointTransactionType
import com.tw2.prepaid.domain.wallet.model.entity.WalletPointHistory
import org.springframework.data.domain.Page
import java.time.LocalDate
import java.time.LocalDateTime

data class WalletPointHistoryResponse(
    val balanceInfo: WalletValidPointResponse,
    val histories: Page<WalletPointHistorySimpleResponse>
)

data class WalletPointHistorySimpleResponse(
    val walletId: Long = -1,
    val channel: String,
    val amt: Int,
    val transactionType: PointTransactionType,
    val createdAt: LocalDateTime,
    val expiredDt: LocalDate,
    val additionalInfos: Map<String, Any?> = emptyMap(),
) {
    companion object {
        fun createFromEntity(entity: WalletPointHistory) = entity.run {
            WalletPointHistorySimpleResponse(
                walletId = walletId,
                channel = channel,
                additionalInfos = toObject(additionalInfos),
                amt = amount,
                transactionType = transactionType,
                createdAt = createdAt,
                expiredDt = expiredDt
            )
        }
    }
}
