package com.tw2.prepaid.domain.wallet.dto.request

import com.tw2.prepaid.domain.wallet.model.CardProcessType
import java.math.BigDecimal
import javax.validation.constraints.NotBlank
data class WalletCardAuthorizationRequest(
    @field: NotBlank
    val accountTxId: String,
    val amount: BigDecimal,
    val isForced: Boolean,
    val processType: CardProcessType,
    val additionalInfos: Map<String, Any?> = emptyMap()
)
