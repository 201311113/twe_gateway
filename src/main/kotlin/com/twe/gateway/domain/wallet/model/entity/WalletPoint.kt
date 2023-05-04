package com.tw2.prepaid.domain.wallet.model.entity

import com.tw2.prepaid.common.MAX_LOCAL_DATE
import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.jpa.BaseEntity
import java.time.LocalDate
import javax.persistence.Entity

@Entity(name = "wallet_point")
class WalletPoint(
    val walletId: Long, // FK
    val refundAvailable: Boolean,
    var expiredDt: LocalDate,
    balance: Int = 0,
): BaseEntity() {
    var balance = balance
        protected set
    fun usePoint(point: Int): Int {
        if (balance <= 0)
            return 0
        val originBalance = balance
        if (balance >= point)
            balance -= point
        else
            balance = 0
        return originBalance - balance
    }
    fun usePointForced(point: Int) {
        if (expiredDt != MAX_LOCAL_DATE)
            throw DefaultException(errorCode = ErrorCode.INTERNAL_DATA_INTEGRITY)
        balance -= point
    }
    fun chargePoint(point: Int) { balance += point }
    fun isEmpty() = balance == 0 && expiredDt < MAX_LOCAL_DATE
}