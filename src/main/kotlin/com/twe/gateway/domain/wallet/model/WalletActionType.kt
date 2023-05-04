package com.tw2.prepaid.domain.wallet.model

const val AUTHORIZATION_PREFIX = "AUTHORIZATION_"
const val CANCEL_PREFIX = "CANCEL_"
// https://www.notion.so/travelwallet/1-0-vs-2-0-073a6a95281d4d708e4b059d7602904d
enum class WalletActionType(
    val isExchangeDetail: Boolean = false,
) {
    CHARGE(isExchangeDetail = true),
    REFUND(isExchangeDetail = true),
    CHARGE_EVENT(isExchangeDetail = true),
    AUTHORIZATION_PURCHASE,
    AUTHORIZATION_ATM,
    CANCEL_PURCHASE,
    CANCEL_ATM,
    BALANCE_INQUIRY,
    ADJUST,
    EXCHANGE_FROM(isExchangeDetail = true),
    EXCHANGE_TO(isExchangeDetail = true),
    ETC,
}