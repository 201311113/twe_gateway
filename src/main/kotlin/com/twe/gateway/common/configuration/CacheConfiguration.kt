package com.tw2.prepaid.common.configuration

import mu.KotlinLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

private val log = KotlinLogging.logger {}
const val CURRENCY_COUNTRY_CACHE = "currency-country"
const val BANK_CACHE_KEY = "bank"
const val CURRENCY_EXCHANGE_RATE_CACHE = "currency-exchange-rate"

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun cacheManager() = ConcurrentMapCacheManager(CURRENCY_COUNTRY_CACHE, BANK_CACHE_KEY, CURRENCY_EXCHANGE_RATE_CACHE)
    @CacheEvict(value = [CURRENCY_COUNTRY_CACHE], allEntries = true)
    @Scheduled(fixedRateString = "PT1800S")
    fun evictCurrencyCountryCache() {
        log.info("$CURRENCY_COUNTRY_CACHE cache evicted.")
    }
    @CacheEvict(value = [BANK_CACHE_KEY], allEntries = true)
    @Scheduled(fixedRateString = "PT1800S")
    fun evictBankCache() {
        log.info("$BANK_CACHE_KEY cache evicted.")
    }
    @CacheEvict(value = [CURRENCY_EXCHANGE_RATE_CACHE], allEntries = true)
    @Scheduled(fixedRateString = "\${app.currency-api.cache-invalidate-check-seconds}")
    fun evictCurrencyExchangeRatesCache() {
        // log.info("$CURRENCY_EXCHANGE_RATE_CACHE cache evicted.")
    }
}