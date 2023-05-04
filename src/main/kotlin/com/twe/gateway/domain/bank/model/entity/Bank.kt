package com.tw2.prepaid.domain.bank.model.entity

import com.tw2.prepaid.common.EMPTY_JSON
import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.bank.model.BankType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "bank")
class Bank (
    var name: String,
    @Enumerated(EnumType.STRING)
    var bankType: BankType,
    var bankCode: String,
    var imageUrl: String?,
    var isActive: Boolean = true,
    val additionalInfos: String = EMPTY_JSON,
): BaseEntity()