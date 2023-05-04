package com.tw2.prepaid.common.jpa.oddments

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints
import javax.persistence.LockModeType
import javax.persistence.QueryHint

interface OddmentsHolderEntityRepository: JpaRepository<OddmentsHolderEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(value = [QueryHint(name = "javax.persistence.lock.timeout", value = "10000")])
    fun queryByCategory(category: OddmentsCategory): OddmentsHolderEntity?
    fun findByCategory(category: OddmentsCategory): OddmentsHolderEntity?
}