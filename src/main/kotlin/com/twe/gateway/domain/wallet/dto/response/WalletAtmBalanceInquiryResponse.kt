package com.tw2.prepaid.domain.wallet.dto.response

import java.math.BigDecimal

data class WalletAtmBalanceInquiryResponse(
    val ledgerBalance: BigDecimal,
    val availableBalance: BigDecimal,
)
