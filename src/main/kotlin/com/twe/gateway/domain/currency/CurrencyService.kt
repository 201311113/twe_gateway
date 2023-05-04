package com.tw2.prepaid.domain.currency

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.properties.Profile
import com.tw2.prepaid.common.utils.round
import com.tw2.prepaid.domain.currency.dto.reqeust.CurrencyRequest
import com.tw2.prepaid.domain.currency.model.repository.CurrencyRepository
import com.tw2.prepaid.domain.currency.dto.response.CurrencyResponse
import com.tw2.prepaid.domain.currency.feign.CurrencyApiClient
import com.tw2.prepaid.domain.currency.model.CurrencyCountry
import com.tw2.prepaid.domain.currency.model.CurrencyType
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

private val log = KotlinLogging.logger {}

@Service
class CurrencyService(
    private val currencyRepository: CurrencyRepository,
    private val currencyApiClient: CurrencyApiClient,
    @Value("\${spring.profiles.active:local}")
    private val profile: String
) {
    fun createCurrency(request: CurrencyRequest) =
        CurrencyResponse.createFromEntity(currencyRepository.save(request.toEntity()))
    @Transactional
    fun updateCurrency(currency: CurrencyType, request: CurrencyRequest): CurrencyResponse {
        val currencyEntity = currencyRepository.findByCurrencyCode(currency.name) ?:
            throw DefaultException(errorCode = ErrorCode.INVALID_CURRENCY)
        return CurrencyResponse.createFromEntity(request.updateEntity(currencyEntity))
    }
    fun getCurrency(currency: String, isActive: Boolean? = null) = getCurrencies(isActive)
        .find { it.currencyCode == currency } ?: throw DefaultException(errorCode = ErrorCode.INVALID_CURRENCY)
    fun getCurrencies(isActive: Boolean?) =
        currencyRepository.findAllByIsActive(isActive).map(CurrencyResponse::createFromEntity)
    fun baseCurrencyExchangeRate(country: String): BigDecimal {
        // 통화제한 풀어놓음
        //getCurrencies(true).find { it.currencyCode == country }
        //    ?: throw DefaultException(errorCode = ErrorCode.INVALID_CURRENCY)
        return if (Profile.valueOf(profile.uppercase()).isLocal) {
            getDummyCurrencyExchange(country).getPrice()
        } else {
            // 밑에 100으로 나누는게 있어서 소수점이 뭉개지므로 안전하게 소수점을 8자리까지 빼놓음
            val baseRate = currencyApiClient.getExchangeRates()[country]!!.toBigDecimal().round(8)
            baseRate / currencyExchangeRateValueRatio(country).toBigDecimal()
        }
    }
    fun getCurrencyExchangeRates() = currencyApiClient.getExchangeRates()
    @Transactional
    fun deleteCurrency(currency: CurrencyType) = currencyRepository.deleteByCurrencyCode(currency.name)
    private fun getDummyCurrencyExchange(country: String) = CurrencyCountry.values().find { it.name == country } ?:
        throw DefaultException(errorCode = ErrorCode.INVALID_CURRENCY)

    private fun currencyExchangeRateValueRatio(country: String) =
        CurrencyCountry.values().find { it.name == country }?.valueRatio ?: 1
}
