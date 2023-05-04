package com.tw2.prepaid.domain.bank.model.repository

import com.tw2.prepaid.domain.bank.model.entity.Bank
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BankRepository: JpaRepository<Bank, Long> {
    fun findByIsActive(isActive: Boolean): List<Bank>
}