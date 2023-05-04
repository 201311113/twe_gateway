package com.tw2.prepaid.domain.currency.feign

import com.tw2.prepaid.common.configuration.CURRENCY_EXCHANGE_RATE_CACHE
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(
    url = "\${app.currency-api.url}",
    value = "currency-api",
)
interface CurrencyApiClient {
    @GetMapping
    @Cacheable(cacheNames = [CURRENCY_EXCHANGE_RATE_CACHE])
    fun getExchangeRates(): Map<String, String>
}