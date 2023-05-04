package com.tw2.prepaid.domain.wallet.dto.request

import com.tw2.prepaid.domain.wallet.model.CardProcessType
import java.math.BigDecimal
import javax.validation.constraints.NotBlank
import javax.validation.constraints.PositiveOrZero

data class WalletCardCancelRequest(
    @field: NotBlank
    val accountTxId: String,
    @field: PositiveOrZero
    val amount: BigDecimal,
    val processType: CardProcessType,
    val originalAccountTxIds: List<String> = emptyList(),
    val additionalInfos: Map<String, Any?> = emptyMap()
)
