package com.tw2.prepaid.common.support.retry

import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.properties.Profile
import com.tw2.prepaid.common.utils.toJsonString
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

private val log = KotlinLogging.logger {}

@Component
class SqsRetryTemplate(
    private val sqsClient: SqsClient,
    private val properties: PrepaidProperties,
    @Value("\${spring.profiles.active:local}")
    private val profile: String
) {
    fun pushJob(retryJobType: RetryJobType, data: Map<String, Any>) {
        if (Profile.valueOf(profile.uppercase()).isLocal)
            return

        val retryJob = RetryJob(retryJobType = retryJobType, data = toJsonString(data))
        log.info { retryJob }

        val req = SendMessageRequest.builder()
            .queueUrl(properties.aws.sqsUrl)
            .messageBody(toJsonString(retryJob))
            .build()
        sqsClient.sendMessage(req)
    }
}