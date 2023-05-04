package com.tw2.prepaid.domain.wallet.model.repository

import com.tw2.prepaid.domain.member.model.entity.PartnerMember
import com.tw2.prepaid.domain.wallet.model.entity.Wallet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.stereotype.Repository
import javax.persistence.LockModeType
import javax.persistence.QueryHint

@Repository
interface WalletRepository: JpaRepository<Wallet, Long>, WalletQueryRepository {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // 사실 QueryHint 옵션은 DB level 에서 지원이 안되어 db 에 직접 timeout 을 세팅함.
    // SET PERSIST innodb_lock_wait_timeout = 10;
    @QueryHints(value = [QueryHint(name = "javax.persistence.lock.timeout", value = "10000")])
    fun queryById(id: Long): Wallet?
    fun findByUser(user: PartnerMember): Wallet?
}