package com.tw2.prepaid.domain.wallet.dto.response

data class WalletAccountResponse(
    val fintechNum: String,
    val userSeqNum: String,
    val bankStdCode: String,
    val bankName: String,
    val accountNum: String
)
