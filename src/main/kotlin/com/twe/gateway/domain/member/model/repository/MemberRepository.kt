package com.tw2.prepaid.domain.member.model.repository

import com.tw2.prepaid.domain.member.model.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, Long> {
    fun findByCi(ci: String): Member?
}