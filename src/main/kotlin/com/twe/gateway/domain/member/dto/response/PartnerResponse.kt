package com.tw2.prepaid.domain.member.dto.response

import com.tw2.prepaid.domain.member.model.entity.Partner
import java.time.LocalDateTime

data class PartnerResponse(
    val id: Long,
    val name: String,
    val corporationName: String,
    val businessRegNum: String, // 사업자 등록번호
    val contactName: String,
    val contactMdn: String,
    val zipCode: String?,
    val address: String?,
    val contractDt: LocalDateTime,
    val terminationDt: LocalDateTime?,
    val mainBankCode: String? = null,
    val mainAccountNum: String? = null,
    val subBankCode: String? = null,
    val subAccountNum: String? = null,
    val paymentType: String,
) {
    companion object {
        fun createFromEntity(entity: Partner) = entity.run {
            PartnerResponse(
                id = id,
                name = name,
                corporationName = corporationName,
                businessRegNum = businessRegNum,
                contactName = contactName,
                contactMdn = contactMdn,
                zipCode = zipCode,
                address = address,
                contractDt = contractDt,
                terminationDt = terminationDt,
                mainBankCode = mainBankCode,
                mainAccountNum = mainAccountNum,
                subBankCode = subBankCode,
                subAccountNum = subAccountNum,
                paymentType = paymentType.desc,
            )
        }
    }
}
