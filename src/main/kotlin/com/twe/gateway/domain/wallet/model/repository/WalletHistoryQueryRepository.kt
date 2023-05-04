package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.common.jpa.Querydsl4RepositorySupport
import com.tw2.prepaid.domain.wallet.model.WalletActionType
import com.tw2.prepaid.domain.wallet.model.entity.QWalletHistory.walletHistory
import com.tw2.prepaid.domain.wallet.model.entity.WalletHistory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

interface WalletHistoryQueryRepository {
    fun findWalletHistories(pageable: Pageable, walletId: Long,
                            currency: String? = null, startDt: LocalDate?,
                            endDt: LocalDate?, actionTypes: List<WalletActionType>?,
    ): Page<WalletHistory>
    fun findWalletHistory(id: Long, date: LocalDate?): WalletHistory?
}

@Repository
class WalletHistoryQueryRepositoryImpl: WalletHistoryQueryRepository, Querydsl4RepositorySupport(WalletHistory::class.java) {
    override fun findWalletHistories(pageable: Pageable, walletId: Long,
                                     currency: String?, startDt: LocalDate?,
                                     endDt: LocalDate?, actionTypes: List<WalletActionType>?): Page<WalletHistory> =
        applyPagination(pageable) { jpaQueryFactory ->
            val periodPredicate =
                if (startDt == null && endDt == null) null
                else walletHistory.createdAt.between(startDt?.atStartOfDay(), endDt?.plusDays(1)?.atStartOfDay())
            jpaQueryFactory.selectFrom(walletHistory)
                .where(
                    walletHistory.walletId
                        .eq(walletId)
                        .and(currency?.let { walletHistory.currency.eq(it) })
                        .and(actionTypes?.let { walletHistory.actionType.`in`(actionTypes) })
                        .and(periodPredicate)
                )
        }
    override fun findWalletHistory(id: Long, date: LocalDate?): WalletHistory? {
        val periodPredicate = date?.let { walletHistory.createdAt.between(
            date.minusDays(1).atStartOfDay(), date.plusDays(1).atStartOfDay()) }
        return jpaQueryFactory.selectFrom(walletHistory)
            .where(walletHistory.id.eq(id).and(periodPredicate))
            .fetchOne()
    }
}