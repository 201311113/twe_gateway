package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.domain.wallet.model.entity.WalletHistoryExchangeDetail
import org.springframework.data.jpa.repository.JpaRepository

interface WalletHistoryExchangeDetailRepository:
    JpaRepository<WalletHistoryExchangeDetail, Long>, WalletHistoryExchangeDetailQueryRepository {
    fun findAllByWalletHistoryIdIn(walletHistoryIds: Set<Long>): List<WalletHistoryExchangeDetail>
}