package com.tw2.prepaid.domain.member.model.entity

import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.member.model.PartnerPaymentType
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "partner")
class Partner(
    var name: String,
    var corporationName: String,
    @Column(nullable = false)
    val businessRegNum: String, // 사업자 등록번호
    var contactName: String,
    var contactMdn: String,
    var zipCode: String? = null,
    var address: String? = null,
    var contractDt: LocalDateTime,
    var terminationDt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var paymentType: PartnerPaymentType = PartnerPaymentType.OB,
    var mainBankCode: String? = null,
    var mainAccountNum: String? = null,
    var subBankCode: String? = null,
    var subAccountNum: String? = null,

    // Partner 가 유저 정보를 들고 있을 필요는 없다.
    // @OneToMany(mappedBy = "partner")
    // val partnerMembers: MutableList<PartnerMember> = mutableListOf(),
): BaseEntity()