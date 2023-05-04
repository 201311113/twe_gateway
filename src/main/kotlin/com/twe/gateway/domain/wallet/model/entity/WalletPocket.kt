package com.tw2.prepaid.domain.wallet.model.entity

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.common.utils.floor
import com.tw2.prepaid.common.utils.isZero
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "wallet_pocket")
class WalletPocket(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    val wallet: Wallet,
    val currency: String,

    cashBalance: BigDecimal = BigDecimal.ZERO,
    pointBalance: BigDecimal = BigDecimal.ZERO,
): BaseEntity() {
    var cashBalance = cashBalance
        protected set(value) { field = value.floor() }
    var pointBalance = pointBalance
        protected set(value) { field = value.floor() }
    fun plusBalance(cashAmount: BigDecimal, pointAmount: BigDecimal = BigDecimal.ZERO, isForce: Boolean = false) {
        this.cashBalance += cashAmount
        this.pointBalance += pointAmount

        if (!isForce && (this.cashBalance < BigDecimal.ZERO || this.pointBalance < BigDecimal.ZERO))
            if (cashAmount < BigDecimal.ZERO || pointAmount < BigDecimal.ZERO) // 충전의 케이스는 돈을 채워넣기 때문에 잔고가 여전히 음수일수 있다.
                throw DefaultException(httpStatus = HttpStatus.OK, errorCode = ErrorCode.잔액부족)
    }
    fun spendAmount(amount: BigDecimal, isForce: Boolean = false): Pair<BigDecimal, BigDecimal> {
        val (originalCashAmt, originalPointAmt) = cashBalance to pointBalance

        if (!isForce && totalAmount() < amount)
            throw DefaultException(httpStatus = HttpStatus.OK, errorCode = ErrorCode.잔액부족)
        if (amount < BigDecimal.ZERO)
            throw DefaultException(errorCode = ErrorCode.NOT_POSITIVE_AMOUNT)

        // 포인트가 차감할 돈보다 많은 경우 포인트로 다 차감
        if (pointBalance >= amount)
            pointBalance -= amount
        // 포인트가 없는 경우 캐시에서 차감
        else if (pointIsEmpty())
            cashBalance -= amount
        // 포인트가 애매하게 있는 경우 다 쓰고 나머지 캐시에서 차감
        else {
            pointBalance = BigDecimal.ZERO
            cashBalance -= (amount - originalPointAmt)
        }
        return originalCashAmt - cashBalance to originalPointAmt - pointBalance
    }
    fun pointIsEmpty() = this.pointBalance.isZero()
    fun totalAmount() = pointBalance + cashBalance
}