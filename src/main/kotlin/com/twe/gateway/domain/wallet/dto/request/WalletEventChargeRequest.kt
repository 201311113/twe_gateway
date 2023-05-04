package com.tw2.prepaid.domain.wallet.dto.request

import com.tw2.prepaid.common.utils.floor
import com.tw2.prepaid.domain.wallet.model.WalletActionType
import java.math.BigDecimal

data class WalletFreeChargeRequest(
    var localCashAmount: BigDecimal, // 100불
    val baseExchangeRate: BigDecimal,
    val usdSpreadExchangeRate: BigDecimal,
    val actionType: WalletActionType,
    val additionalInfos: Map<String, Any> = emptyMap() // eventName
) {
    init {
        localCashAmount = localCashAmount.floor()
    }
}