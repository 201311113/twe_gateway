package com.tw2.prepaid.domain.member.dto.request

import com.tw2.prepaid.domain.member.model.PartnerPaymentType
import com.tw2.prepaid.domain.member.model.entity.Partner
import java.time.LocalDateTime

data class PartnerRequest(
    val name: String,
    val corporationName: String,
    val businessRegNum: String, // 사업자 등록번호
    val contactName: String,
    val contactMdn: String,
    val zipCode: String? = null,
    val address: String? = null,
    val contractDt: LocalDateTime,
    val terminationDt: LocalDateTime,
    val paymentType: PartnerPaymentType,
    val mainBankCode: String? = null,
    val mainAccountNum: String? = null,
    val subBankCode: String? = null,
    val subAccountNum: String? = null,
) {
    fun toEntity(): Partner = Partner(
        name = name,
        corporationName = corporationName,
        businessRegNum = businessRegNum,
        contactName = contactName,
        contactMdn = contactMdn,
        zipCode = zipCode,
        address = address,
        contractDt = contractDt,
        terminationDt = terminationDt,
        paymentType = paymentType,
        mainBankCode = mainBankCode,
        mainAccountNum = mainAccountNum,
        subBankCode = subBankCode,
        subAccountNum = subAccountNum
    )

    fun updateEntity(partner: Partner) = partner.also {
        it.name = name
        it.corporationName = corporationName
        it.contactName = contactName
        it.contactMdn = contactMdn
        it.zipCode = zipCode
        it.address = address
        it.contractDt = contractDt
        it.terminationDt = terminationDt
        it.paymentType = paymentType
        it.mainBankCode = mainBankCode
        it.mainAccountNum = mainAccountNum
        it.subBankCode = subBankCode
        it.subAccountNum = subAccountNum
    }
}