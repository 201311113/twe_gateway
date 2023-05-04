package com.tw2.prepaid.domain.member.model.repository

import com.tw2.prepaid.domain.member.model.entity.Member
import com.tw2.prepaid.domain.member.model.entity.Partner
import com.tw2.prepaid.domain.member.model.entity.PartnerMember
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PartnerMemberRepository: JpaRepository<PartnerMember, Long> {
    fun findByMemberAndPartnerOrderByCreatedAtDesc(member: Member, partner: Partner): List<PartnerMember>
    fun findByPartner(partner: Partner, pageable: Pageable): Page<PartnerMember>
    fun findByMember(member: Member): List<PartnerMember>
}