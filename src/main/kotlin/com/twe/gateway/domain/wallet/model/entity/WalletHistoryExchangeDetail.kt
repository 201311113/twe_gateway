package com.tw2.prepaid.domain.wallet.model.entity

import com.tw2.prepaid.common.EMPTY_JSON
import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.member.model.PartnerPaymentType
import com.tw2.prepaid.domain.wallet.model.WalletHistoryType
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "wallet_history_exchange_detail")
class WalletHistoryExchangeDetail (
    val walletHistoryId: Long,
    val walletId: Long,
    @Enumerated(EnumType.STRING)
    val historyType: WalletHistoryType, // CHARGE or REFUND
    val baseExchangeRate: Double,
    val spreadExchangeRate: Double,
    val usdSpreadExchangeRate: Double,
    val feeAmount: BigDecimal,
    val krwExchangeGains: Double,
    val businessUuid: String,
    val krwCashAmount: Int,
    val krwPointAmount: Int,
    @Enumerated(EnumType.STRING)
    val partnerPaymentType: PartnerPaymentType,
    val additionalInfos: String = EMPTY_JSON
): BaseEntity()
