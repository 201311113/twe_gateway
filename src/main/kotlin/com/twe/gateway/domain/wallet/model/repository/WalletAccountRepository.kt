package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.domain.wallet.model.entity.Wallet
import com.tw2.prepaid.domain.wallet.model.entity.WalletAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

interface WalletAccountRepository: JpaRepository<WalletAccount, Long> {
    fun findByWallet(wallet: Wallet): List<WalletAccount>
    fun findByAccountId(accountId: String): List<WalletAccount>
    fun findByWalletAndAccountId(wallet: Wallet, accountId: String): WalletAccount?
    fun existsByWalletAndIsMain(wallet: Wallet, isMain: Boolean): Boolean
    @Transactional
    fun deleteByWalletAndAccountIdIn(wallet: Wallet, accountIds: Set<String>)
}