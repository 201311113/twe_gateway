package com.tw2.prepaid.domain.wallet.dto.request

data class WalletPennyAuthRequest(
    val twContent: String,
    val userContent: String,
    val fintechUseNum: String,
    val userSeqNum: String,
)