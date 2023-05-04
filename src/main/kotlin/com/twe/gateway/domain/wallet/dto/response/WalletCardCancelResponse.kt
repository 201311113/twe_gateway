package com.tw2.prepaid.domain.wallet.dto.response

import java.math.BigDecimal

data class WalletCardCancelResponse(
    val walletHistoryId: Long,
    val balanceAmount: BigDecimal,
)
