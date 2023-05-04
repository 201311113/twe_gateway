package com.tw2.prepaid.domain.currency.model

import java.math.BigDecimal
import java.security.SecureRandom

val secureRandom: SecureRandom = SecureRandom()

enum class CurrencyCountry(
    val basePrice: Double,
    val refundSpreadRate: Double = 0.02,
    val buySpreadRate: Double = 0.025,
    val refundFee: Double = 0.01,
    val decimalDigitNumber: Int = 2,
    val valueRatio: Int = 1, // naver 가 특정 국가는 valueRatio 만큼 곱해서 제공함
) {
    USD(refundSpreadRate = 0.05, buySpreadRate = 0.0, basePrice = 1200.50),
    EUR(refundSpreadRate = 0.05, buySpreadRate = 0.0, basePrice = 1300.50),
    JPY(valueRatio = 100, refundSpreadRate = 0.05, buySpreadRate = 0.0, basePrice = 9.6393, decimalDigitNumber = 4),
    CNY(refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 193.93),
    HKD(refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 180.94),
    TWD(refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 44.06),
    GBP(refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 1621.10),
    CAD(refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 1037.28),
    CHF(refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 1411.45),
    PHP(refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 23.49),
    VND(valueRatio = 100, refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 0.0571, decimalDigitNumber = 4),
    THB(refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 37.53),
    SGD(refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 968.46),
    MYR(refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 296.11),
    IDR(valueRatio = 100, refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 0.0910, decimalDigitNumber = 4),
    AUD(refundSpreadRate = 0.05, buySpreadRate = 0.005, basePrice = 902.52),
    OMR(basePrice = 3461.64),
    SEK(basePrice = 127.19),
    NZD(basePrice = 833.61),
    CZK(basePrice = 56.89),
    CLP(basePrice = 1.45),
    TRY(basePrice = 71.43),
    MNT(basePrice = 0.39),
    ILS(basePrice = 388.84),
    DKK(basePrice = 186.24),
    NOK(basePrice = 133.82),
    SAR(basePrice = 354.22),
    KWD(basePrice = 4330.85),
    BHD(basePrice = 3530.22),
    AED(basePrice = 362.36),
    JOD(basePrice = 1875.04),
    EGP(basePrice = 54.15),
    QAR(basePrice = 362.97),
    KZT(basePrice = 2.86),
    BND(basePrice = 968.46),
    INR(basePrice = 16.30),
    PKR(basePrice = 5.93),
    BDT(basePrice = 13.07),
    MXN(basePrice = 68.81),
    BRL(basePrice = 248.34),
    ZAR(basePrice = 78.31),
    RUB(basePrice = 22.05),
    HUF(basePrice = 3.35),
    PLN(basePrice = 294.73),
    LKR(basePrice = 3.61),
    DZD(basePrice = 9.61),
    KES(basePrice = 10.88),
    COP(basePrice = 0.27),
    TZS(basePrice = 0.57),
    NPR(basePrice = 10.19),
    RON(basePrice = 281.00),
    LYD(basePrice = 271.19),
    MOP(basePrice = 165.42),
    MMK(basePrice = 0.63),
    ETB(basePrice = 24.91),
    UZS(basePrice = 0.12),
    KHR(basePrice = 0.32),
    KRW(basePrice = 1.0, decimalDigitNumber = 0),
    FJD(basePrice = 601.48),

    HRK(basePrice = 184.06),
    ISK(basePrice = 9.41),
    PEN(basePrice = 344.78),
    LAK(basePrice = 0.078);
    private fun decimalPointFormat() = "%.${decimalDigitNumber}f"
    //fun getPrice() = if (this == KRW) 1.0 else String.format(decimalPointFormat(), basePrice + secureRandom.nextDouble(6.0) - 3.0).toDouble()
    fun getPrice() = if (this == KRW) BigDecimal.ONE else String.format(decimalPointFormat(), basePrice).toBigDecimal()
}