package com.tw2.prepaid.domain.wallet.dto.request

import com.tw2.prepaid.common.EMPTY_MESSAGE
import java.math.BigDecimal
import javax.validation.constraints.Positive
import javax.validation.constraints.PositiveOrZero

data class WalletChargeRequest(
    @field: PositiveOrZero
    val domesticCashAmount: Int?,
    @field: PositiveOrZero
    val domesticPointAmount: Int = 0,
    @field: Positive
    var localTotalAmount: BigDecimal,
    val twPrintContent: String = EMPTY_MESSAGE,
    val userPrintContent: String = EMPTY_MESSAGE,
    val fintechUseNum: String? = null,

    @field: Positive
    val baseExchangeRate: BigDecimal,
    @field: Positive
    val spreadExchangeRate: BigDecimal,
    @field: Positive
    val usdSpreadExchangeRate: BigDecimal,
)
