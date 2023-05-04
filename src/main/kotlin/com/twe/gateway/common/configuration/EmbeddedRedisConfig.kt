package com.tw2.prepaid.common.configuration

import mu.KotlinLogging
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer

private val log = KotlinLogging.logger {}

@Configuration
@ConditionalOnProperty(
    value = ["app.embedded.redis.enabled"], havingValue = "true", matchIfMissing = false
)
class EmbeddedRedisConfig: InitializingBean, DisposableBean {
    lateinit var redisServer: RedisServer
    override fun afterPropertiesSet() {
        try {
            log.info("redis is start.")
            redisServer = RedisServer()
            redisServer.start()
        } catch (ex: Exception) {
            log.error("already redis is running.")
        }
    }
    override fun destroy() {
        log.info("redis is stop.")
        redisServer.stop()
    }
}