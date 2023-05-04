package com.tw2.prepaid.domain.currency.model.repository

import com.tw2.prepaid.domain.currency.model.entity.Currency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CurrencyRepository: JpaRepository<Currency, Long>, CurrencyQueryRepository {
    fun findByCurrencyCode(currencyCode: String): Currency?
    fun deleteByCurrencyCode(currencyCode: String)
}