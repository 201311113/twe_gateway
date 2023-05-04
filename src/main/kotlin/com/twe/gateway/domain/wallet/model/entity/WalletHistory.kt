package com.tw2.prepaid.domain.wallet.model.entity

import com.tw2.prepaid.common.EMPTY_JSON
import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.wallet.model.WalletActionType
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "wallet_history")
class WalletHistory(
    val userId: Long,
    val walletId: Long,
    val currency: String,
    @Enumerated(EnumType.STRING)
    val actionType: WalletActionType,
    val cashAmount: BigDecimal, // 거래 내역
    val pointAmount: BigDecimal,
    val cashBalance: BigDecimal, // 거래 후 잔액
    val pointBalance: BigDecimal,
    val additionalInfos: String = EMPTY_JSON
): BaseEntity()