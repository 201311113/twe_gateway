package com.tw2.prepaid.domain.currency.model.repository

import com.tw2.prepaid.common.configuration.CURRENCY_COUNTRY_CACHE
import com.tw2.prepaid.common.jpa.Querydsl4RepositorySupport
import com.tw2.prepaid.domain.currency.model.entity.Currency
import com.tw2.prepaid.domain.currency.model.entity.QCurrency.currency
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

interface CurrencyQueryRepository {
    fun findAllByIsActive(isActive: Boolean?): List<Currency>
}

@Repository
class CurrencyQueryRepositoryImpl: CurrencyQueryRepository, Querydsl4RepositorySupport(Currency::class.java) {
    @Cacheable(cacheNames = [CURRENCY_COUNTRY_CACHE])
    override fun findAllByIsActive(isActive: Boolean?): List<Currency> =
        jpaQueryFactory.selectFrom(currency).where(isActive?.let { currency.isActive.eq(isActive) }).fetch()
}