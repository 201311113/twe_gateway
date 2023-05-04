package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.domain.wallet.model.PointTransactionType
import com.tw2.prepaid.domain.wallet.model.entity.WalletPointHistory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface WalletPointHistoryRepository: JpaRepository<WalletPointHistory, Long>, WalletPointHistoryQueryRepository {
    fun findAllByWalletIdEqualsOrderByCreatedAtDesc(
        walletId: Long, pageable: Pageable
    ): Page<WalletPointHistory>
    fun countByWalletIdAndCreatedAtBetweenAndTransactionType(walletId: Long, startDt: LocalDateTime,
                                                             endDt: LocalDateTime, tranType: PointTransactionType): Int
    fun findAllByWalletPointIdIn(walletPointIds: List<Long>): List<WalletPointHistory>
}