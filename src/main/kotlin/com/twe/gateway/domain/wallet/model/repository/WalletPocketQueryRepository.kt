package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.common.jpa.Querydsl4RepositorySupport
import com.tw2.prepaid.domain.wallet.model.entity.WalletPocket
import org.springframework.stereotype.Repository

interface WalletPocketQueryRepository

@Repository
class WalletPocketQueryRepositoryImpl:
    WalletPocketQueryRepository, Querydsl4RepositorySupport(WalletPocket::class.java)