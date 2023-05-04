package com.tw2.prepaid.domain.bank.simulator.model.repository

import com.tw2.prepaid.domain.bank.simulator.model.entity.SimulatorOpenbankUserEntity
import com.tw2.prepaid.domain.member.model.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface SimulatorOpenbankUserRepository: JpaRepository<SimulatorOpenbankUserEntity, Long> {
    fun findByReqClientNum(reqClientNum: String): SimulatorOpenbankUserEntity?
    fun findByMember(member: Member): SimulatorOpenbankUserEntity?
}