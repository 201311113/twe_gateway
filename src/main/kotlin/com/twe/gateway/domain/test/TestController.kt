package com.tw2.prepaid.domain.test

import com.tw2.prepaid.common.support.redis.RedisRepository
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankRefreshAccessTokenRequest
import com.tw2.prepaid.domain.bank.feign.ObNonExchangeApiClient
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.*

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping(path = ["/test"])
@Profile("!prod")
class TestController(
    private val redisRepository: RedisRepository,
    private val nonExchangeApiClient: ObNonExchangeApiClient,
) {
    @GetMapping("/redis/{key}")
    fun getRedisValue(@PathVariable key: String): String? {
        log.info("hasKey: ${redisRepository.hasKey(key)}")
        return redisRepository.findByKey(key)
    }
    @PostMapping("/redis/{key}")
    fun saveRedisValue(
        @PathVariable key: String,
        @RequestParam value: String,
        @RequestParam timeout: Long = 30000
    ) = redisRepository.save(key, value, timeout)
    @GetMapping("/refresh_token")
    fun refreshToken() = nonExchangeApiClient.refreshAccessTokenUnLogging(OpenbankRefreshAccessTokenRequest(clientId = "a", clientSecret = "b"))
}