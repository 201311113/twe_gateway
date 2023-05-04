package com.tw2.prepaid.domain.wallet.dto.response

import java.math.BigDecimal

class WalletCardAuthorizationResponse(
    val walletHistoryId: Long,
    val balanceAmount: BigDecimal,
)