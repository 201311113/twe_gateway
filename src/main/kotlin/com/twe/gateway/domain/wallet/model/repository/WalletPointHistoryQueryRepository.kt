package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.common.jpa.Querydsl4RepositorySupport
import com.tw2.prepaid.domain.wallet.model.entity.QWalletPointHistory.walletPointHistory
import com.tw2.prepaid.domain.wallet.model.entity.WalletPointHistory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

interface WalletPointHistoryQueryRepository {
    fun findWalletPointHistories(pageable: Pageable, walletId: Long,
                                 startDt: LocalDate?, endDt: LocalDate?): Page<WalletPointHistory>
}

@Repository
class WalletPointHistoryQueryRepositoryImpl:
    WalletPointHistoryQueryRepository, Querydsl4RepositorySupport(WalletPointHistory::class.java) {
    override fun findWalletPointHistories(
        pageable: Pageable,
        walletId: Long,
        startDt: LocalDate?,
        endDt: LocalDate?
    ): Page<WalletPointHistory> =
        applyPagination(pageable) { jpaQueryFactory ->
            val periodPredicate =
                if (startDt == null && endDt == null) null
                else walletPointHistory.createdAt.between(startDt?.atStartOfDay(), endDt?.plusDays(1)?.atStartOfDay())
            jpaQueryFactory.selectFrom(walletPointHistory)
                .where(
                    walletPointHistory.walletId
                        .eq(walletId)
                        .and(periodPredicate)
                )
                .orderBy(walletPointHistory.createdAt.desc())
        }
}