package com.tw2.prepaid.domain.wallet.dto.request

data class WalletPointRefundRequest(
    val pointAmount: Int,
    val twPrintContent: String,
    val userPrintContent: String,
    val fintechUseNum: String? = null
)
