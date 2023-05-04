package com.tw2.prepaid.domain.bank.dto.response.openbank

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.domain.bank.common.MILLIS_FORMATTER
import java.time.LocalDateTime
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankCloseUserResponse(
    val apiTranId: String = UUID.randomUUID().toString(),
    val apiTranDtm: String = LocalDateTime.now().format(MILLIS_FORMATTER),
    val rspCode: String,
    val rspMessage: String = EMPTY_MESSAGE
)
