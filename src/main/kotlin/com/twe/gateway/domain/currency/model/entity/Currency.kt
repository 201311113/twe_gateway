package com.tw2.prepaid.domain.currency.model.entity

import com.tw2.prepaid.common.jpa.BaseEntity
import javax.persistence.Entity

@Entity(name = "currency")
data class Currency(
    var countryName: String, // 국가명
    var currencyCode: String, // 통화코드
    var countryCode: String, // 국가코드
    var currencyName: String, // 통화명
    var currencyNameEn: String, // 통화명영어
    var currencySymbol: String, // 통화 심볼
    var refundSpreadRate: Double,
    var chargeSpreadRate: Double,
    var minimumBuyAmount: Int,
    var minimumRefundAmount: Int,
    var decimalDigitNumber: Int,
    var currencyUnit: Int,
    var refundFeeRate: Double,
    var imageUrl: String = "",
    var isActive: Boolean,
): BaseEntity()
