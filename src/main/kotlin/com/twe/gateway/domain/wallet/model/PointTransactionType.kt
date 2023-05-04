package com.tw2.prepaid.domain.wallet.model

enum class PointTransactionType {
    USE,                // 사용
    PURCHASE,
    REFUND,
    CS_REWARD,
    CS_REWARD_CANCEL,
    EVENT,
    CASHBACK,
    EARN,             // 적립
    EXPIRATION,
    CANCELLATION,
}

const val POINT_TYPE_PREFIX = "POINT_"