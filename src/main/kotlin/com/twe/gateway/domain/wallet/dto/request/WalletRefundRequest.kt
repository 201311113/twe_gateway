package com.tw2.prepaid.domain.wallet.dto.request

import com.tw2.prepaid.common.EMPTY_MESSAGE
import java.math.BigDecimal
import javax.validation.constraints.Positive
import javax.validation.constraints.PositiveOrZero

data class WalletRefundRequest(
    @field: Positive
    val domesticTotalAmount: Int? = null, // ex) 스프레드 먹여서 13만원
    @field: Positive
    val localTotalAmount: BigDecimal, // ex) 100불
    val twPrintContent: String = EMPTY_MESSAGE,
    val userPrintContent: String = EMPTY_MESSAGE,
    val fintechUseNum: String? = null,
    @field: Positive
    val baseExchangeRate: BigDecimal,
    @field: Positive
    val spreadExchangeRate: BigDecimal,
    @field: Positive
    val usdSpreadExchangeRate: BigDecimal,
    @field: PositiveOrZero
    val refundFee: BigDecimal,
)
