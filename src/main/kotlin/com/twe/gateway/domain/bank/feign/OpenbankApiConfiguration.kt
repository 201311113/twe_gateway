package com.tw2.prepaid.domain.bank.feign

import com.tw2.prepaid.domain.bank.service.OpenbankTokenService
import feign.Logger
import feign.RequestInterceptor
import feign.codec.Decoder
import feign.codec.Encoder
import feign.form.spring.SpringFormEncoder
import feign.optionals.OptionalDecoder
import mu.KotlinLogging
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder
import org.springframework.cloud.openfeign.support.SpringDecoder
import org.springframework.cloud.openfeign.support.SpringEncoder
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

private val log = KotlinLogging.logger {}
class OpenbankApiConfiguration(
    private val messageConverters: ObjectFactory<HttpMessageConverters>,
    private val obTokenService: OpenbankTokenService,
) {
    @Bean
    fun requestInterceptor() = RequestInterceptor {
        if (!it.url().contains(REFRESH_ACCESS_TOKEN_PATH))
            it.header(HttpHeaders.AUTHORIZATION, "Bearer ${obTokenService.getAccessToken().accessToken}")
    }
    @Bean
    fun feignLogger(): Logger = FeignLogger()
    //@Bean
    //fun encoder(converters: ObjectFactory<HttpMessageConverters>): Encoder = SpringFormEncoder(SpringEncoder(converters))
    @Bean
    fun decoder(customizers: ObjectProvider<HttpMessageConverterCustomizer>): Decoder =
        OpenbankApiCustomDecoder(OptionalDecoder(ResponseEntityDecoder(SpringDecoder(messageConverters, customizers))))
}