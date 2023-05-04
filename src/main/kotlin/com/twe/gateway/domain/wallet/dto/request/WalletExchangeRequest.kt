package com.tw2.prepaid.domain.wallet.dto.request

import com.tw2.prepaid.domain.currency.model.CurrencyType
import java.math.BigDecimal
import javax.validation.constraints.Positive

data class WalletExchangeRequest(
    val fromCurrency: CurrencyType,
    val toCurrency: CurrencyType,
    @field: Positive
    val fromAmount: BigDecimal,
    @field: Positive
    val toAmount: BigDecimal,
    // 이 값이 있으면 환율 3원 틀어짐 비교 없으면 무시
    @field: Positive
    val domesticAmount: BigDecimal?,
    @field: Positive
    val baseFromExchangeRate: BigDecimal,
    @field: Positive
    val spreadFromExchangeRate: BigDecimal,
    @field: Positive
    val baseToExchangeRate: BigDecimal,
    @field: Positive
    val usdFromSpreadExchangeRate: BigDecimal,
    @field: Positive
    val usdToSpreadExchangeRate: BigDecimal,
)
