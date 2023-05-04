package com.tw2.prepaid.domain.wallet.dto.request

import javax.validation.constraints.NotBlank

data class WalletBankWithdrawRequest (
    @field: NotBlank
    val accountTxId: String,
    val amount: Int,
    val twPrintContent: String,
    val userPrintContent: String,
    val accountId: String? = null,
)
data class WalletBankDepositRequest (
    @field: NotBlank
    val accountTxId: String,
    val amount: Int,
    val twPrintContent: String,
    val userPrintContent: String,
    val accountId: String? = null,
)