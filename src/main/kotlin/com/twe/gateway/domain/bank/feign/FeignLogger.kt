package com.tw2.prepaid.domain.bank.feign

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.error.IgnoreException
import com.tw2.prepaid.common.utils.isReadTimeoutException
import feign.*
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter

private val log = KotlinLogging.logger {}
const val UN_LOG_SUFFIX = "UnLogging"
class FeignLogger: Logger() {
    override fun log(configKey: String, format: String?, vararg args: Any?) =
        log.info(String.format(methodTag(configKey) + format, *args))
    override fun logRequest(configKey: String, logLevel: Level, request: Request) {
        val tokenHeaderLastString = request.headers()[HttpHeaders.AUTHORIZATION]?.firstOrNull()?.takeLast(15)
        val bodyText = request.body()?.let { String(it) } ?: EMPTY_MESSAGE
        log(configKey, "%s %s %s %s", request.httpMethod().name, request.url(), tokenHeaderLastString,
            if (configKey.contains(UN_LOG_SUFFIX)) EMPTY_MESSAGE else bodyText)
    }
    override fun logAndRebufferResponse(configKey: String, logLevel: Level,
                                        response: Response, elapsedTime: Long): Response {
        val reason = response.reason() ?: EMPTY_MESSAGE
        val status = response.status()
        val bodyData = if (response.body() != null && !(status == 204 || status == 205) && !configKey.contains(UN_LOG_SUFFIX)) {
            // HTTP 204 No Content "...response MUST NOT include a message-body"
            // HTTP 205 Reset Content "...response MUST NOT include an entity"
            Util.toByteArray(response.body().asInputStream())
        } else {
            ByteArray(0)
        }
        log(configKey, "%s%s (%sms) %s %s", status, reason, elapsedTime, bodyData.size,
            Util.decodeOrDefault(bodyData, Util.UTF_8, "Unknown"))
        return if (bodyData.isEmpty()) response else response.toBuilder().body(bodyData).build()
    }
    override fun logIOException(configKey: String, logLevel: Level,
                                ioe: IOException, elapsedTime: Long): IOException {
        val sw = StringWriter()
        ioe.printStackTrace(PrintWriter(sw))
        log(configKey, "ERROR %s: %s (%sms) %s", ioe.javaClass.simpleName, ioe.message, elapsedTime, sw.toString())
        return ioe
    }
}

val obExchangePaths = listOf(WITHDRAW_PATH, DEPOSIT_PATH)
fun feignExceptionHandling(ex: FeignException): Exception {
    // 입출금 read timeout 은 특별대우
    if (obExchangePaths.any { ex.request().url().contains(it) } && isReadTimeoutException(ex.cause))
        return ex
    // 그외 network error 는 노관심.
    if (ex.cause is IOException)
        return IgnoreException()
    return ex
}