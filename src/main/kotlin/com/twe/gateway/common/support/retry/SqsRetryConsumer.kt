package com.tw2.prepaid.common.support.retry

import com.tw2.prepaid.common.error.IgnoreException
import com.tw2.prepaid.common.error.RetryAbortException
import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.properties.Profile
import com.tw2.prepaid.common.utils.toObject
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.*
import java.util.concurrent.Executors
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

private val log = KotlinLogging.logger {}
const val MAX_RETRY_CNT = 9
val RETRY_WORKER = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

@Component
class SqsConsumer(
    private val sqsClient: SqsClient,
    private val properties: PrepaidProperties,
    private val retryExecutors: List<RetryExecutor>,
    @Value("\${spring.profiles.active:local}")
    private val profile: String
) {
    lateinit var retryJob: Job

    @PostConstruct
    fun postConstruct() {
        if (Profile.valueOf(profile.uppercase()).isLocal)
            return

        log.info("prepaid retry worker start.")
        retryJob = CoroutineScope(RETRY_WORKER).launch { executeJob() }
    }

    private suspend fun executeJob() = coroutineScope {
        while(isActive) {
            val req = ReceiveMessageRequest.builder()
                .waitTimeSeconds(10)
                .maxNumberOfMessages(10)
                .attributeNames(QueueAttributeName.ALL)
                .queueUrl(properties.aws.sqsUrl).build()

            val messages = sqsClient.receiveMessage(req).messages()
            messages.forEach { message ->
                val reqId = RandomStringUtils.randomAlphanumeric(5).uppercase()
                val retryJob = toObject<RetryJob>(message.body())
                val retryCount = message.attributesAsStrings()[MessageSystemAttributeName.APPROXIMATE_RECEIVE_COUNT.toString()]?.toInt()
                    ?: Int.MAX_VALUE
                log.info("id: $reqId, retryCount: $retryCount, read message : ${message.body()}")

                if (retryCount > MAX_RETRY_CNT) {
                    deleteRetryJob(message.receiptHandle())
                    log.error("id: $reqId, maximum retry failed: $retryJob")
                    return@forEach
                }

                retryExecutors
                    .find { executor -> executor.isAssignable(retryJob.retryJobType) }
                    ?.runCatching {
                        execute(retryJob)
                        deleteRetryJob(message.receiptHandle())
                        log.info("id: $reqId, retry success.")
                    }?.onFailure {
                        if (it !is IgnoreException)
                            log.error("id: $reqId, sqs retry failed.", it)
                        when (it) {
                            is RetryAbortException -> deleteRetryJob(message.receiptHandle())
                            else -> {
                                val waitTimeSec = (1 shl retryCount) * 60
                                val changeReq = ChangeMessageVisibilityRequest.builder()
                                    .queueUrl(properties.aws.sqsUrl)
                                    .visibilityTimeout(if (waitTimeSec > 3600 * 3) 3600 * 3 else waitTimeSec)
                                    .receiptHandle(message.receiptHandle()).build()
                                sqsClient.changeMessageVisibility(changeReq)
                            }
                        }
                    }
            }
        }
    }
    private suspend fun deleteRetryJob(receiptHandle: String) =
        sqsClient.deleteMessage(
            DeleteMessageRequest.builder()
                .queueUrl(properties.aws.sqsUrl)
                .receiptHandle(receiptHandle)
                .build()
        )

    @PreDestroy
    fun preDestroy() {
        retryJob.cancel()
        log.info("prepaid retry worker stopped.")
    }
}