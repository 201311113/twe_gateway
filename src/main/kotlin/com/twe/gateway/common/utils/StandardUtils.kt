package com.tw2.prepaid.common.utils

import com.tw2.prepaid.common.FAILURE_CODE
import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.domain.currency.model.CurrencyType
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.SocketTimeoutException
import java.util.*
import kotlin.math.*

fun need(value: Boolean, errorCode: ErrorCode) {
    if (!value) {
        throw DefaultException(errorCode = errorCode, httpStatus = HttpStatus.BAD_REQUEST)
    }
}

fun need(value: Boolean, code: Int = FAILURE_CODE, message: String) {
    if (!value) {
        throw DefaultException(code = code, message = message, httpStatus = HttpStatus.BAD_REQUEST)
    }
}

fun needNot(value: Boolean, errorCode: ErrorCode) {
    if (value) {
        throw DefaultException(errorCode = errorCode, httpStatus = HttpStatus.BAD_REQUEST)
    }
}

fun needNot(value: Boolean, code: Int = FAILURE_CODE, message: String) {
    if (value) {
        throw DefaultException(code = code, message = message, httpStatus = HttpStatus.BAD_REQUEST)
    }
}

fun diffIsBig(val1: Int, val2: Int, diff: Int) = abs(val1 - val2) > diff
fun convertPositive(value: BigDecimal) = if (value < BigDecimal.ZERO) BigDecimal.ZERO else value
fun errorIfNotNullBlankString(param: String?) { if (param?.isBlank() == true) throw DefaultException(errorCode = ErrorCode.BAD_REQUEST) }
fun errorIfNotNullBlankStrings(vararg params: String?) = params.forEach(::errorIfNotNullBlankString)
const val DEFAULT_DIGITS: Int = 2
fun BigDecimal.ceil(scale: Int = DEFAULT_DIGITS): BigDecimal = this.setScale(scale, RoundingMode.CEILING)
fun Double.ceil(scale: Int = DEFAULT_DIGITS): BigDecimal = this.toBigDecimal().ceil(scale)
fun Int.ceil(scale: Int = DEFAULT_DIGITS): BigDecimal = this.toBigDecimal().ceil(scale)
fun Long.ceil(scale: Int = DEFAULT_DIGITS): BigDecimal = this.toBigDecimal().ceil(scale)
fun BigDecimal.floor(scale: Int = DEFAULT_DIGITS): BigDecimal = this.setScale(scale, RoundingMode.FLOOR)
fun Double.floor(scale: Int = DEFAULT_DIGITS): BigDecimal = this.toBigDecimal().floor(scale)
fun Int.floor(scale: Int = DEFAULT_DIGITS): BigDecimal = this.toBigDecimal().floor(scale)
fun Long.floor(scale: Int = DEFAULT_DIGITS): BigDecimal = this.toBigDecimal().floor(scale)
fun BigDecimal.round(scale: Int = DEFAULT_DIGITS): BigDecimal = this.setScale(scale, RoundingMode.HALF_UP)
fun Double.round(scale: Int = DEFAULT_DIGITS): BigDecimal = this.toBigDecimal().round(scale)
fun Int.round(scale: Int = DEFAULT_DIGITS): BigDecimal = this.toBigDecimal().round(scale)
fun Long.round(scale: Int = DEFAULT_DIGITS): BigDecimal = this.toBigDecimal().round(scale)
fun BigDecimal.toFloorInt(): Int = this.floor(0).toInt()
fun BigDecimal.toCeilInt(): Int = this.ceil(0).toInt()
fun BigDecimal.toRoundInt(): Int = this.round(0).toInt()
fun BigDecimal.isZero() = this.signum() == 0
fun BigDecimal.isSame(other: BigDecimal) = this in other..other
// 국가별 소수점 처리를 안하기로 함
fun getCurrencyDigits(currency: String) = DEFAULT_DIGITS/*try {
    val digits = Currency.getInstance(currency).defaultFractionDigits
    if (digits > DEFAULT_DIGITS) DEFAULT_DIGITS else digits
} catch (ex: Throwable) { throw DefaultException(errorCode = ErrorCode.INVALID_CURRENCY) }*/
fun getCurrencyDigits(currency: CurrencyType) = getCurrencyDigits(currency.name)
fun isReadTimeoutException(ex: Throwable?) = ex is SocketTimeoutException && ex.localizedMessage?.contains("Read time") == true