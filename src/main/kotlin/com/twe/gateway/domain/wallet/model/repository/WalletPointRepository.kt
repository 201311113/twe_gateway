package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.domain.wallet.model.entity.WalletPoint
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface WalletPointRepository: JpaRepository<WalletPoint, Long>, WalletPointQueryRepository {
    fun findAllByWalletIdAndExpiredDtAfter(walletId: Long, expiredDt: LocalDate): List<WalletPoint>
    fun findByWalletIdAndExpiredDt(walletId: Long, expiredDt: LocalDate): List<WalletPoint>
    fun findByExpiredDtBefore(expiredDt: LocalDate): List<WalletPoint>
}