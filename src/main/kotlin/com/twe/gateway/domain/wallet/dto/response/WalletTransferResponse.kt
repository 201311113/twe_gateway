package com.tw2.prepaid.domain.wallet.dto.response

import java.math.BigDecimal

data class WalletTransferResponse(
    val currency: String,
    val krwAmount: Int,
    val pocketPointBalance: BigDecimal,
    val pocketCashBalance: BigDecimal,
    val pocketBalance: BigDecimal,
)
