package com.tw2.prepaid.domain.wallet.dto.response

import com.tw2.prepaid.domain.wallet.model.LIMIT_KRW_AMOUNT

class WalletTotalBalanceResponse(
    val krwTotalBalance: Int,
    val krwCashBalance: Int,
    val todayPointRefundCnt: Int,
    val limitAmount : Int = LIMIT_KRW_AMOUNT
)