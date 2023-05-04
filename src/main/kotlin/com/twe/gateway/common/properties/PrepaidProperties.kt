package com.tw2.prepaid.common.properties

import com.tw2.prepaid.common.utils.toObject
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest
import java.nio.charset.Charset
import javax.annotation.PostConstruct

private val log = KotlinLogging.logger {}

@ConstructorBinding
@ConfigurationProperties(prefix = "app")
data class PrepaidProperties(
    val swagger: SwaggerProperties,
    val aws: AwsProperties,
    val alwaysUsePrimaryDb: Boolean = false,
    val warmUp: WarmUpInfo = WarmUpInfo(),
    val encryption: EncryptionProperties,
    val secretProps: MutableMap<String, String> = mutableMapOf(),
) {
    @Value("\${spring.profiles.active:local}")
    private lateinit var profile: String
    data class SwaggerProperties(
        val urls: List<String> = emptyList(),
    )
    data class WarmUpInfo(
        val url: String = "http://localhost:8080/prepaid/v1.0/banks/warm-up",
        val enabled: Boolean = false,
        val count: Int = 10
    )
    data class AwsProperties(
        val sqsUrl: String,
        val region: String,
    )
    data class EncryptionProperties(
        val dbEnabled: Boolean,
        val apiEnabled: Boolean,
    )
    @PostConstruct
    fun postConstruct() {
        log.debug { "postConstruct. $this" }
        log.debug { "charSet=${Charset.defaultCharset().displayName()}" }
        if (Profile.valueOf(profile.uppercase()).isLocal)
            return

        val client = SecretsManagerClient.builder()
            .region(Region.of(aws.region))
            .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
            .build()
        val valueReq = GetSecretValueRequest.builder().secretId("${profile}/properties/prepaid").build()
        val valueRes: Map<String, String> = toObject(client.getSecretValue(valueReq).secretString())
        secretProps.putAll(valueRes)
    }
    fun getSecretValue(key: SecretKey) = secretProps[key.key] ?: key.defaultValue
}