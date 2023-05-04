package com.tw2.prepaid.domain.bank.simulator.model.repository

import com.tw2.prepaid.domain.bank.simulator.model.entity.SimulatorOpenbankAccountEntity
import com.tw2.prepaid.domain.member.model.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface SimulatorOpenbankAccountRepository: JpaRepository<SimulatorOpenbankAccountEntity, Long> {
    fun deleteByMember(member: Member)
    fun findByMember(member: Member): List<SimulatorOpenbankAccountEntity>
    fun findByAccountNum(accountNum: String): SimulatorOpenbankAccountEntity?
    fun findByFintechUseNum(fintechUseNum: String): SimulatorOpenbankAccountEntity?
}