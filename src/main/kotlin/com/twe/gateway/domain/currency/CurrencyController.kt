package com.tw2.prepaid.domain.currency

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.currency.model.repository.CurrencyRepository
import com.tw2.prepaid.domain.currency.dto.reqeust.CurrencyRequest
import com.tw2.prepaid.domain.currency.dto.response.CurrencyResponse
import com.tw2.prepaid.domain.currency.model.CurrencyType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/prepaid/v1.0/currencies"])
class CurrencyController (
    private val currencyRepository: CurrencyRepository,
    private val currencyService: CurrencyService
){
    @PostMapping
    fun create(@RequestBody request: CurrencyRequest) = Response(data = currencyService.createCurrency(request))
    @PutMapping("/{currency}")
    fun update(@PathVariable currency: CurrencyType, @RequestBody request: CurrencyRequest) =
        Response(data = currencyService.updateCurrency(currency, request))
    @GetMapping("/{currency}")
    fun get(@PathVariable currency: CurrencyType) = Response(data = currencyService.getCurrency(currency.name))
    @DeleteMapping("/{currency}")
    fun delete(@PathVariable currency: CurrencyType) = Response(data = currencyService.deleteCurrency(currency))
    @GetMapping("/exchange-rates")
    fun getCurrencyExchangeRates() = Response(data = currencyService.getCurrencyExchangeRates())
    @GetMapping
    fun gets(@RequestParam isActive: Boolean?): Response<List<CurrencyResponse>> =
        Response(data = currencyService.getCurrencies(isActive))
}