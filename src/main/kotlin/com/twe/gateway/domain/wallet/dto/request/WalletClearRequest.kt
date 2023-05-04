package com.tw2.prepaid.domain.wallet.dto.request

data class WalletClearRequest (
    val additionalInfos: Map<String, Any> = emptyMap() // eventName
)