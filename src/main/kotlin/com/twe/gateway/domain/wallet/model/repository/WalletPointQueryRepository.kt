package com.tw2.prepaid.domain.wallet.model.repository

import com.querydsl.core.Tuple
import com.tw2.prepaid.common.jpa.Querydsl4RepositorySupport
import com.tw2.prepaid.domain.wallet.model.entity.QWalletPoint.walletPoint
import com.tw2.prepaid.domain.wallet.model.entity.QWalletPointHistory.walletPointHistory
import com.tw2.prepaid.domain.wallet.model.entity.WalletPoint
import org.springframework.stereotype.Repository
import java.time.LocalDate

interface WalletPointQueryRepository {
    fun findByExpiredDtBeforeQueryDsl(expiredDt: LocalDate): List<Tuple>
}

@Repository
class WalletPointQueryRepositoryImpl: WalletPointQueryRepository, Querydsl4RepositorySupport(WalletPoint::class.java) {
    override fun findByExpiredDtBeforeQueryDsl(expiredDt: LocalDate) =
        jpaQueryFactory.select(walletPoint, walletPointHistory.channel) // tuple.get(walletPointHistory.channel)
            .from(walletPoint)
            .innerJoin(walletPointHistory).on(walletPoint.id.eq(walletPointHistory.walletPointId))
            .where(walletPoint.expiredDt.before(LocalDate.now()))
            .fetch()
}