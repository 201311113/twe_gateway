package com.tw2.prepaid.domain.wallet.model.entity

import com.tw2.prepaid.common.EMPTY_JSON
import com.tw2.prepaid.common.jpa.BaseEntity
import javax.persistence.Entity

@Entity(name = "wallet_history_ob_detail")
class WalletHistoryObDetail(
    val walletHistoryId: Long,
    val walletId: Long,
    val actionType: String,
    val accountTxId: String,
    val additionalInfos: String = EMPTY_JSON
): BaseEntity()