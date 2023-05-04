package com.tw2.prepaid.domain.wallet.dto.response

// initial value 0 is required
data class WalletValidPointResponse(
    val refundAvailableBalance: Int = 0,
    val onlyChargeAvailableBalance: Int = 0,
    val thisMonthExpiredRefundAvailableBalance: Int = 0,
    val thisMonthExpiredOnlyChargeAvailableBalance: Int = 0,
    val totalAvailableBalance: Int = 0,
    val totalThisMonthAvailableBalance: Int = 0,
    val todayPointRefundCnt: Int = 0,
)
