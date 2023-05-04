package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.domain.wallet.model.entity.WalletPocket
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface WalletPocketRepository: JpaRepository<WalletPocket,Long>, WalletPocketQueryRepository {
    fun findByWalletIdAndCurrency(walletId: Long, currency: String): WalletPocket?
    fun findByWalletId(walletId: Long): List<WalletPocket>
    fun findAllByCashBalanceLessThan(cashBalance: BigDecimal = BigDecimal.ZERO, pageable: Pageable): Page<WalletPocket>
}