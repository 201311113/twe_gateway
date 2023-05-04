package com.tw2.prepaid.domain.bank.dto.response

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.tw2.prepaid.common.support.jackson.JacksonEncryptConverter
import java.time.LocalDateTime

data class ObAccessToken(
    @JsonSerialize(converter = JacksonEncryptConverter::class)
    val accessToken: String,
    val updatedAt: LocalDateTime,
)
