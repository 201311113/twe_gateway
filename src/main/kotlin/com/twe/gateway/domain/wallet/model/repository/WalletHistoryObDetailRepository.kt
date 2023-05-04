package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.domain.wallet.model.entity.WalletHistoryObDetail
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface WalletHistoryObDetailRepository: JpaRepository<WalletHistoryObDetail, Long> {
    fun findByAccountTxIdAndCreatedAtBetween(
        accountTxId: String,
        startDt: LocalDateTime,
        endDt: LocalDateTime): WalletHistoryObDetail?
}