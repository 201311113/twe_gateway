package com.tw2.prepaid.domain.member.model

enum class PartnerPaymentType(val desc: String) {
    OB(desc = "오픈뱅킹"), NO_CASH(desc = "결제금액없음"),
}