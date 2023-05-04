package com.tw2.prepaid.common.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tw2.prepaid.common.ISO8601DateFormat
import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.properties.SecretKey
import com.tw2.prepaid.common.support.jackson.JacksonDecryptDeserializer
import io.micrometer.core.aop.CountedAspect
import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import javax.annotation.PostConstruct
import javax.persistence.EntityManager

private val log = KotlinLogging.logger {}

@Configuration
/*
@EnableJpaRepositories(
    basePackages = ["com.tw2.prepaid"],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ["com.tw2.prepaid.domain.bank.simulator.*"]
        )
    ]
)*/
class PrepaidApplicationConfiguration(
    private val em: EntityManager,
    private val properties: PrepaidProperties
){
    @PostConstruct
    fun postConstruct() {}
    @Bean
    fun jsonCustomizer() = Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder.serializationInclusion(JsonInclude.Include.NON_NULL)
            builder.featuresToEnable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            builder.failOnUnknownProperties(false)
            builder.serializers(LocalDateTimeSerializer(ISO8601DateFormat)) // json body 출력용
            builder.deserializers(JacksonDecryptDeserializer(
                key = properties.getSecretValue(SecretKey.API_ENCRYPTION_KEY),
                iv = properties.getSecretValue(SecretKey.API_ENCRYPTION_KEY)
            ))
        }
    @Bean
    fun sqsClient(): SqsClient = SqsClient.builder()
        .region(Region.of(properties.aws.region))
        .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
        .build()
    @Bean
    fun jpaQueryFactory() = JPAQueryFactory(em)
    @Bean
    fun restTemplate() = RestTemplate()
    @Bean
    fun countedAspect(registry: MeterRegistry): CountedAspect = CountedAspect(registry)
    @Bean
    fun timedAspect(registry: MeterRegistry): TimedAspect = TimedAspect(registry)
}