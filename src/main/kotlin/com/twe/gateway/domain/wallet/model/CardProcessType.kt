package com.tw2.prepaid.domain.wallet.model

enum class CardProcessType {
    PURCHASE,
    ATM;

    fun getWalletActionType(prefix: String) = WalletActionType.valueOf(prefix + name)
}