package com.tw2.prepaid.domain.bank.dto.request.openbank

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.common.pp
import com.tw2.prepaid.common.properties.SecretKey
import feign.form.FormProperty

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
// feign client encoder 가 MediaType.APPLICATION_FORM_URLENCODED_VALUE 처리할 때는 val (java Final)은 무시하기 때문에 var 을 씀.
// val 로 하면 empty body 로 날아감
data class OpenbankRefreshAccessTokenRequest(
    @FormProperty("client_id")
    var clientId: String = pp.getSecretValue(SecretKey.OB_CLIENT_ID),
    @FormProperty("client_secret")
    var clientSecret: String = pp.getSecretValue(SecretKey.OB_CLIENT_SECRET),
    var scope: String = "sa",
    @FormProperty("grant_type")
    var grantType: String = "client_credentials",
)