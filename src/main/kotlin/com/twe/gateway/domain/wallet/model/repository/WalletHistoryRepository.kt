package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.domain.wallet.model.entity.WalletHistory
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface WalletHistoryRepository: JpaRepository<WalletHistory, Long>, WalletHistoryQueryRepository {
    fun findByIdAndCreatedAtBetween(id: Long, startDt: LocalDateTime, endDt: LocalDateTime): WalletHistory?
}