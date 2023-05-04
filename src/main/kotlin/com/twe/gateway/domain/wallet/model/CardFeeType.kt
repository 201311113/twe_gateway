package com.tw2.prepaid.domain.wallet.model

enum class CardFeeType(
    val desc: String,
) {
    ANNUAL_FEE(desc = "연회비"),
    ISSUANCE_FEE(desc = "발급비"),
    RE_ISSUANCE_FEE(desc = "재발급비"),
    CANCEL(desc = "환불비"),
    ETC(desc = "기타")
}