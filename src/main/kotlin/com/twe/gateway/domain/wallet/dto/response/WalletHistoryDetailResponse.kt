package com.tw2.prepaid.domain.wallet.dto.response

import com.tw2.prepaid.common.utils.round
import com.tw2.prepaid.common.utils.toObject
import com.tw2.prepaid.common.utils.toRoundInt
import com.tw2.prepaid.domain.wallet.model.WalletActionType
import com.tw2.prepaid.domain.wallet.model.entity.WalletHistory
import com.tw2.prepaid.domain.wallet.model.entity.WalletHistoryExchangeDetail
import java.math.BigDecimal
import java.time.LocalDateTime

data class WalletHistoryDetailResponse(
    val actionType: WalletActionType,
    val createdAt: LocalDateTime,
    val currency: String,
    val domesticCashAmount: Int = 0,  // 국내
    val domesticPointAmount: Int = 0, // 국내
    val domesticTotalAmount: Int = 0, // 국내
    val totalAmount: BigDecimal, // 해외
    val cashAmount: BigDecimal,
    val pointAmount: BigDecimal,
    val cashBalance: BigDecimal,
    val pointBalance: BigDecimal,
    val totalBalance: BigDecimal,
    val spreadExchangeRate: BigDecimal = BigDecimal.ZERO,
    val feeAmount: BigDecimal = BigDecimal.ZERO,
    val additionalInfos: Map<String, Any>
) {
    companion object {
        fun createFromEntity(history: WalletHistory, exchangeHistory: WalletHistoryExchangeDetail) = with(history) {
            WalletHistoryDetailResponse(
                actionType = actionType,
                createdAt = createdAt,
                currency = currency,
                domesticCashAmount = exchangeHistory.krwCashAmount, // 수수료 빠진 값 (실제 오픈 뱅킹에 넣어줄 금액)
                domesticPointAmount = exchangeHistory.krwPointAmount,
                domesticTotalAmount = exchangeHistory.krwCashAmount + exchangeHistory.krwPointAmount + exchangeHistory.feeAmount.toRoundInt(),
                cashAmount = cashAmount,
                pointAmount = pointAmount,
                totalAmount = cashAmount + pointAmount,
                cashBalance = cashBalance,
                pointBalance = pointBalance,
                totalBalance = cashBalance + pointBalance,
                spreadExchangeRate = exchangeHistory.spreadExchangeRate.toBigDecimal().round(7),
                feeAmount = exchangeHistory.feeAmount,
                additionalInfos = toObject<Map<String, Any>>(exchangeHistory.additionalInfos) + toObject<Map<String, Any>>(additionalInfos)
            )
        }
        fun createFromEntity(history: WalletHistory) = with(history) {
            WalletHistoryDetailResponse(
                actionType = actionType,
                createdAt = createdAt,
                currency = currency,
                cashAmount = cashAmount,
                pointAmount = pointAmount,
                totalAmount = cashAmount + pointAmount,
                cashBalance = cashBalance,
                pointBalance = pointBalance,
                totalBalance = cashBalance + pointBalance,
                additionalInfos = toObject(additionalInfos)
            )
        }
    }
}
