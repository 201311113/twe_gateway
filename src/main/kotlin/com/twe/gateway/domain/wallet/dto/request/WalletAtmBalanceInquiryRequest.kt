package com.tw2.prepaid.domain.wallet.dto.request

import java.math.BigDecimal
import javax.validation.constraints.NotBlank
data class WalletAtmBalanceInquiryRequest(
    @field: NotBlank
    val accountTxId: String,
    val feeAmount: BigDecimal,
)
