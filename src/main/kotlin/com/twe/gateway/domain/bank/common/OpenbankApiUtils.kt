package com.tw2.prepaid.domain.bank.common

import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.error.ErrorCodeHolder
import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.properties.SecretKey
import com.tw2.prepaid.common.utils.getBean
import com.tw2.prepaid.domain.bank.model.OpenBankApiResponseCode
import org.apache.commons.lang3.RandomStringUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val YMDT_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
val MILLIS_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
val HOUR_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HHmmss")
val YMD_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

fun makeTranDtime(): String = LocalDateTime.now().format(YMDT_FORMATTER)
// 하루동안 유일성을 보장해야 함
fun makeBankTranId(): String {
    val pp = getBean(PrepaidProperties::class.java)
    return "${pp.getSecretValue(SecretKey.OB_TRANSACTION_KEY)}U${LocalDateTime.now().format(HOUR_FORMATTER)}${RandomStringUtils.randomAlphanumeric(3).uppercase()}"
}
fun convertErrorCodeFromOpenBanking(apiRetCode: String, bankRetCode: String, rspMsg: String) = ErrorCodeHolder(
    errorCode = OpenBankApiResponseCode.values().find { it.name == apiRetCode }?.getDetailedErrorCode(bankRetCode) ?: ErrorCode.오픈뱅킹에러,
    msg = rspMsg,
    isErrorLog = OpenBankApiResponseCode.values().find { it.name == apiRetCode }?.getIsErrorLog(bankRetCode) ?: true
)