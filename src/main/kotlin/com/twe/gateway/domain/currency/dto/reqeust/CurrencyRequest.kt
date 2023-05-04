package com.tw2.prepaid.domain.currency.dto.reqeust

import com.tw2.prepaid.domain.currency.model.entity.Currency

data class CurrencyRequest(
    val countryName: String, // 국가명
    val currencyCode: String, // 통화코드
    val countryCode: String, // 국가코드
    val currencyName: String, // 통화명
    val currencySymbol: String, // 통화 심볼
    val refundSpreadRate: Double,
    val buySpreadRate: Double,
    val minimumBuyAmount: Int,
    val minimumRefundAmount: Int,
    val decimalDigitNumber: Int,
    val currencyUnit: Int,
    val refundFee: Double,
    val isActive: Boolean,
    val imageUrl: String,
    val currencyNameEn: String,
) {
    fun updateEntity(entity: Currency): Currency = entity.also {
        it.countryName = countryName
        it.currencyCode = currencyCode
        it.countryCode = countryCode
        it.currencyName = currencyName
        it.currencySymbol = currencySymbol
        it.refundSpreadRate = refundSpreadRate
        it.chargeSpreadRate = buySpreadRate
        it.minimumBuyAmount = minimumBuyAmount
        it.minimumRefundAmount = minimumRefundAmount
        it.decimalDigitNumber = decimalDigitNumber
        it.currencyUnit = currencyUnit
        it.refundFeeRate = refundFee
        it.isActive = isActive
        it.imageUrl = imageUrl
        it.currencyNameEn = currencyNameEn
    }

    fun toEntity() = Currency(
        countryName = countryName,
        currencyCode = currencyCode,
        countryCode = countryCode,
        currencyName = currencyName,
        currencySymbol = currencySymbol,
        refundSpreadRate = refundSpreadRate,
        chargeSpreadRate = buySpreadRate,
        minimumBuyAmount = minimumBuyAmount,
        minimumRefundAmount = minimumRefundAmount,
        decimalDigitNumber = decimalDigitNumber,
        currencyUnit = currencyUnit,
        refundFeeRate = refundFee,
        isActive = isActive,
        imageUrl = imageUrl,
        currencyNameEn = currencyNameEn,
    )
}