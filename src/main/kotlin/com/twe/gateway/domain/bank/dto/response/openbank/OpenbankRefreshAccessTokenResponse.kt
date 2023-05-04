package com.tw2.prepaid.domain.bank.dto.response.openbank

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankRefreshAccessTokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val scope: String,
    val clientUseCode: String,
)
