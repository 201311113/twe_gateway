package com.tw2.prepaid.domain.wallet.dto.request

import java.math.BigDecimal
import javax.validation.constraints.NotBlank
data class WalletCardAdjustRequest(
    @field: NotBlank
    val accountTxId: String,
    val amount: BigDecimal, // after amount - before amount
    val originalAccountTxIds: List<String> = emptyList(),
    val additionalInfos: Map<String, Any?> = emptyMap()
)