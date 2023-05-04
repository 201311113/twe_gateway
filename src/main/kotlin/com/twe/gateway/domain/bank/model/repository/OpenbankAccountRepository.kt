package com.tw2.prepaid.domain.bank.model.repository

import com.tw2.prepaid.domain.bank.model.entity.OpenbankAccount
import org.springframework.data.jpa.repository.JpaRepository

interface OpenbankAccountRepository: JpaRepository<OpenbankAccount, Long> {
    fun findByFintechUseNum(fintechUseNum: String): OpenbankAccount?
    fun deleteByFintechUseNum(fintechUseNum: String)
}