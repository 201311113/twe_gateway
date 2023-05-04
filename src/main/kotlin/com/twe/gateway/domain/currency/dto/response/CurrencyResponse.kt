package com.tw2.prepaid.domain.currency.dto.response

import com.tw2.prepaid.domain.currency.model.entity.Currency
import java.time.LocalDateTime

data class CurrencyResponse (
    val id: Long,
    val countryName: String, // 국가명
    val currencyCode: String, // 통화코드
    val countryCode: String, // 국가코드
    val currencyName: String, // 통화명
    val currencySymbol: String, // 통화 심볼
    val refundSpreadRate: Double,
    val chargeSpreadRate: Double,
    val minimumBuyAmount: Int,
    val minimumRefundAmount: Int,
    val decimalDigitNumber: Int,
    val currencyUnit: Int,
    val refundFeeRate: Double,
    val isActive: Boolean,
    val imageUrl: String,
    val currencyNameEn: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
){
    companion object {
        fun createFromEntity(entity: Currency): CurrencyResponse = entity.run {
            CurrencyResponse(
                id = id,
                countryName = countryName,
                currencyCode = currencyCode,
                countryCode = countryCode,
                currencyName = currencyName,
                currencySymbol = currencySymbol,
                refundSpreadRate = refundSpreadRate,
                chargeSpreadRate = chargeSpreadRate,
                minimumBuyAmount = minimumBuyAmount,
                minimumRefundAmount = minimumRefundAmount,
                decimalDigitNumber = decimalDigitNumber,
                currencyUnit = currencyUnit,
                refundFeeRate = refundFeeRate,
                isActive = isActive,
                imageUrl = imageUrl,
                currencyNameEn = currencyNameEn,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
