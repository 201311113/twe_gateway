package com.tw2.prepaid.domain.wallet.dto.request

import com.tw2.prepaid.common.MAX_LOCAL_DATE
import com.tw2.prepaid.common.utils.toJsonString
import com.tw2.prepaid.domain.wallet.model.PointTransactionType
import com.tw2.prepaid.domain.wallet.model.entity.WalletPoint
import com.tw2.prepaid.domain.wallet.model.entity.WalletPointHistory
import java.time.LocalDate

data class WalletPointCreateRequest(
    val refundAvailable: Boolean,
    val expiredDt: LocalDate = MAX_LOCAL_DATE,
    val channel: String,
    val amount: Int,
    val transactionType: PointTransactionType,
    val memo: String? = null,
    val displayDetail: String? = null,
) {
    fun toEntity(walletId: Long) = WalletPoint(
        walletId = walletId,
        refundAvailable = refundAvailable,
        expiredDt = expiredDt,
    )
    fun toHistoryEntity(walletId: Long, walletPointId: Long) = WalletPointHistory(
        walletId = walletId,
        channel = channel,
        amount = amount,
        transactionType = transactionType,
        expiredDt = expiredDt,
        refundAvailable = refundAvailable,
        walletPointId = walletPointId,
        additionalInfos = toJsonString(
            mapOf<String, Any?>("displayDetail" to displayDetail, "memo" to memo).filterValues { it != null }
        ),
    )
}