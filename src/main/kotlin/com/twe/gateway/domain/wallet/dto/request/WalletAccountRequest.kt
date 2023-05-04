package com.tw2.prepaid.domain.wallet.dto.request

import javax.validation.constraints.Min

data class WalletAccountRequest(
    val bankCodeStd: String,
    @Min(value = 7)
    val accountNum: String,
    val fintechUseNum: String,
    val email: String,
)
