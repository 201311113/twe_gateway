package com.tw2.prepaid.domain.bank.model.repository

import com.tw2.prepaid.domain.bank.model.entity.OpenbankTransferHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OpenbankTransferHistoryRepository: JpaRepository<OpenbankTransferHistory, Long> {
    fun findByIdAndCreatedAtBetween(id: Long, startDt: LocalDateTime, endDt: LocalDateTime): OpenbankTransferHistory?
    fun findByBusinessUuidAndCreatedAtBetween(businessUuid: String, startDt: LocalDateTime, endDt: LocalDateTime): OpenbankTransferHistory?
}