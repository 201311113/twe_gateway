package com.tw2.prepaid.domain.wallet.model.entity

import com.tw2.prepaid.common.jpa.BaseEntity
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
@Entity(name = "wallet_account")
class WalletAccount (
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    val wallet: Wallet,
    val accountId: String, // ex) fintechUseNum
    var isMain: Boolean,
): BaseEntity()