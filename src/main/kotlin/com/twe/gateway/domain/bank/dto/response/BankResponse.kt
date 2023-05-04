package com.tw2.prepaid.domain.bank.dto.response

import com.tw2.prepaid.domain.bank.model.BankType
import com.tw2.prepaid.domain.bank.model.entity.Bank
import java.time.LocalDateTime

data class BankResponse(
    val id: Long,
    val name: String,
    val bankCode: String,
    val imageUrl: String?,
    val bankType: BankType,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

fun createFromEntity(entity: Bank): BankResponse = entity.run{
    BankResponse(
        id = entity.id,
        name = entity.name,
        bankCode = entity.bankCode,
        imageUrl = entity.imageUrl,
        bankType = entity.bankType,
        isActive = entity.isActive,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )
}