package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.common.jpa.Querydsl4RepositorySupport
import com.tw2.prepaid.domain.wallet.model.entity.QWalletHistoryExchangeDetail.walletHistoryExchangeDetail
import com.tw2.prepaid.domain.wallet.model.entity.WalletHistoryExchangeDetail
import org.springframework.stereotype.Repository
import java.time.LocalDate

interface WalletHistoryExchangeDetailQueryRepository {
    fun findByWalletHistoryId(walletHistoryId: Long, date: LocalDate?): WalletHistoryExchangeDetail?
}

@Repository
class WalletHistoryExchangeDetailQueryRepositoryImpl:
    WalletHistoryExchangeDetailQueryRepository,
    Querydsl4RepositorySupport(WalletHistoryExchangeDetail::class.java) {
    override fun findByWalletHistoryId(walletHistoryId: Long, date: LocalDate?): WalletHistoryExchangeDetail? =
        jpaQueryFactory.selectFrom(walletHistoryExchangeDetail)
            .where(walletHistoryExchangeDetail.walletHistoryId.eq(walletHistoryId)
                .and(date?.let { walletHistoryExchangeDetail.createdAt.between(it.atStartOfDay(), it.plusDays(1).atStartOfDay()) }))
                .fetchOne()
}