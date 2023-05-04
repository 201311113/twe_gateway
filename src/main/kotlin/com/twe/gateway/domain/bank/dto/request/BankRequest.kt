package com.tw2.prepaid.domain.bank.dto.request

import com.tw2.prepaid.domain.bank.model.BankType
import com.tw2.prepaid.domain.bank.model.entity.Bank

data class BankRequest(
    val name: String,
    val bankCode: String,
    val imageUrl: String?,
    val bankType: BankType,
    val isActive: Boolean = true,
) {
    fun updateEntity(entity: Bank): Bank = entity.also {
        it.name = name
        it.isActive = isActive
        it.bankCode = bankCode
        it.bankType = bankType
        it.imageUrl = imageUrl
    }
    fun toEntity(): Bank = Bank(
        name = name,
        isActive = isActive,
        bankCode = bankCode,
        bankType = bankType,
        imageUrl = imageUrl
    )
}
