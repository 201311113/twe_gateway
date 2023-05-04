package com.tw2.prepaid.domain.member.model.repository

import com.tw2.prepaid.domain.member.model.entity.Partner
import org.springframework.data.jpa.repository.JpaRepository

interface PartnerRepository: JpaRepository<Partner, Long>