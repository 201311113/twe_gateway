package com.tw2.prepaid.domain.wallet.dto.request

import java.math.BigDecimal
import javax.validation.constraints.NotBlank

data class WalletPocketCreateRequest(
    @field: NotBlank(message = "currency(통화)는 빈값일 수 없습니다")
    val currency: String,
    val cashBalance: BigDecimal = BigDecimal.ZERO,
    val pointBalance: BigDecimal = BigDecimal.ZERO
)