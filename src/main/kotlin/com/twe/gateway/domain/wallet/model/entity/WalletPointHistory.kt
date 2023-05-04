package com.tw2.prepaid.domain.wallet.model.entity

import com.tw2.prepaid.common.EMPTY_JSON
import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.wallet.model.PointTransactionType
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "wallet_point_history")
class WalletPointHistory(
    val walletId: Long,
    val walletPointId: Long,
    val channel: String,
    val amount: Int,
    val expiredDt: LocalDate,
    val refundAvailable: Boolean,
    @Enumerated(EnumType.STRING)
    val transactionType: PointTransactionType,
    val additionalInfos: String = EMPTY_JSON,
): BaseEntity()