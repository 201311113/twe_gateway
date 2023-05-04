package com.tw2.prepaid.common

import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.utils.getBean
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

const val EMPTY_MESSAGE = ""
const val SUCCESS_CODE = 0
const val FAILURE_CODE = 1

const val UNKNOWN = "UNKNOWN"
const val IGNORE = "IGNORE"
const val EMPTY_JSON = "{}"

val ISO8601DateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000+09:00", Locale.KOREA)
val MAX_LOCAL_DATE: LocalDate = LocalDate.of(9999, 12, 31)
enum class YnType { Y, N }
const val TW_ACCOUNT_NUM = "1005403633318"
const val OB_ADJUSTMENT_MARKER = "[OB_ADJUSTMENT_MARKER]"
const val OB_ADJUSTMENT_PROCESSING = "[PROCESSING]"
const val OB_ADJUSTMENT_READ_TIMEOUT = "[READ_TIMEOUT]"

val pp: PrepaidProperties by lazy { getBean(PrepaidProperties::class.java) }