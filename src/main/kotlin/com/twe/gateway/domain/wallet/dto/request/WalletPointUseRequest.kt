package com.tw2.prepaid.domain.wallet.dto.request

import com.tw2.prepaid.domain.wallet.model.PointTransactionType

data class WalletPointUseRequest(
    val amount: Int,
    val transactionType: PointTransactionType,
    val memo: String? = null,
    val displayDetail: String? = null,
    val isForced: Boolean = false,
    val isRefundableIfMinus: Boolean = true,
)
