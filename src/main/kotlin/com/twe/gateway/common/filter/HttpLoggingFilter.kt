package com.tw2.prepaid.common.filter

import com.google.common.base.Utf8
import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.domain.bank.controller.OB_REFRESH_TOKEN_PATH
import mu.KotlinLogging
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class HttpLoggingFilter : OncePerRequestFilter() {
    companion object {
        private val BODY_PRINT_LIMIT = if (log.isDebugEnabled) 1000 else 0
        private val lastTimeStamp: AtomicLong = AtomicLong(0)
        private val NO_LOG_PATH = listOf("/actuator")
        private val NO_LOG_HEADER = mapOf<String, CharSequence>(HttpHeaders.USER_AGENT.uppercase() to "HealthChecker")
        private val DUMMY_UUID = UUID.randomUUID().toString()
        private val IGNORE_LOG_BODY_PATHS = listOf(OB_REFRESH_TOKEN_PATH)
        private fun convertToUtf8(content: ByteArray) =
            if (Utf8.isWellFormed(content)) String(content, StandardCharsets.UTF_8) else "length=${content.size}"
        private fun noLoggingBody(reqLine: String) = IGNORE_LOG_BODY_PATHS.any { reqLine.contains(it) }
        private fun noLogging(request: ContentCachingRequestWrapper): Boolean {
            if (NO_LOG_PATH.any { request.requestURI.contains(it) }) return true
            if (request.headerNames.asSequence().any {
                StringUtils.hasText(request.getHeader(it)) && request.getHeader(it).contains(NO_LOG_HEADER[it.uppercase()] ?: DUMMY_UUID)
            }) return true
            return false
        }
        private fun dumpHttp(
            request: ContentCachingRequestWrapper,
            response: HttpServletResponse,
            startTime: Long
        ) {
            val elapsed = (System.nanoTime() - startTime) / 1_000_000
            var reqLine = "${request.method} ${request.requestURI}"
            reqLine = if (request.queryString != null) "$reqLine?${request.queryString}" else reqLine

            val reqBody = convertToUtf8(request.contentAsByteArray)
            val reqHeader = "[${request.headerNames.asSequence().map { it + "=" + request.getHeader(it) }.joinToString()}]"
            val resBody =
                if (response is ContentCachingResponseWrapper && !noLoggingBody(reqLine)) convertToUtf8(response.contentAsByteArray).take(BODY_PRINT_LIMIT)
                else EMPTY_MESSAGE

            var enableLog = true
            if (noLogging(request)) {
                val currentTs = System.currentTimeMillis()
                while (true) {
                    val lastTs = lastTimeStamp.get()
                    // 다른 스레드 누군가 갱신해 놓음 (로그 찍을 필요 없음), 60분마다 찍게함
                    if (lastTs + 60 * 1000 * 60 > currentTs) {
                        enableLog = false
                        break
                    }
                    // 내가 timestamp 갱신 성공했으므로 내가 로그 찍음
                    if (lastTimeStamp.compareAndSet(lastTs, currentTs))
                        break
                }
            }

            if (enableLog) {
                log.info("In ($elapsed ms) - $reqLine $reqHeader $reqBody")
                log.info("Out ($elapsed ms) - ${response.status} $resBody")
            }
        }
    }
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestWrapper =
            if (request is ContentCachingRequestWrapper) request else ContentCachingRequestWrapper(request, BODY_PRINT_LIMIT)
        val responseWrapper =
            if (response is ContentCachingResponseWrapper || BODY_PRINT_LIMIT == 0) response else ContentCachingResponseWrapper(response)

        val startTime = System.nanoTime()

        try {
            filterChain.doFilter(requestWrapper, responseWrapper)
        } finally {
            dumpHttp(requestWrapper, responseWrapper, startTime)
            if (responseWrapper is ContentCachingResponseWrapper)
                responseWrapper.copyBodyToResponse()
        }
    }
}