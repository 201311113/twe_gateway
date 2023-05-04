package com.tw2.prepaid.common.configuration

import com.tw2.prepaid.common.properties.PrepaidProperties
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!prod")
class OpenApiConfiguration(
    private val properties: PrepaidProperties,

    @Value("\${spring.application.name}")
    private val appName: String

) {
    @Bean
    fun customOpenAPI(): OpenAPI = OpenAPI().servers(
        properties.swagger.urls.map {
            Server().url(it)
        }).info(Info().title(appName))
}