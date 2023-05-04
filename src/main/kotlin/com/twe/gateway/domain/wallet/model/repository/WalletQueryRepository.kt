package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.common.jpa.Querydsl4RepositorySupport
import com.tw2.prepaid.domain.member.model.entity.QPartnerMember.partnerMember
import com.tw2.prepaid.domain.wallet.model.entity.QWallet.wallet
import com.tw2.prepaid.domain.wallet.model.entity.Wallet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

interface WalletQueryRepository {
    fun findAllByPartner(partnerId: Long?, pageable: Pageable): Page<Wallet>
    fun findByIdNoLocking(id: Long): Wallet?
}
@Repository
class WalletQueryRepositoryImpl : WalletQueryRepository, Querydsl4RepositorySupport(Wallet::class.java) {
    override fun findAllByPartner(partnerId: Long?, pageable: Pageable): Page<Wallet> =
        applyPagination(pageable) { jpaQueryFactory ->
            jpaQueryFactory.selectFrom(wallet)
                .innerJoin(wallet.user, partnerMember)
                .where(partnerId?.let { partnerMember.partner.id.eq(partnerId) })
        }
    override fun findByIdNoLocking(id: Long): Wallet? = jpaQueryFactory.selectFrom(wallet).where(wallet.id.eq(id)).fetchOne()
}