package com.tw2.prepaid.common.support.redis

import com.tw2.prepaid.common.EMPTY_JSON
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RedisRepository (private val redisTemplate: StringRedisTemplate) {
    fun findByKey(key: String, ignoreError: Boolean = true): String? = redisTemplate.opsForValue()[key]
    fun save(key: String, value: String = EMPTY_JSON, timeoutMillis: Long, ignoreError: Boolean = true) {
        try {
            redisTemplate.opsForValue().set(key, value, timeoutMillis, TimeUnit.MILLISECONDS)
        } catch (ex: Throwable) { if (!ignoreError) throw ex }
    }
    fun save(key: String, value: String = EMPTY_JSON, ignoreError: Boolean = true) {
        try {
            redisTemplate.opsForValue()[key] = value
        } catch (ex: Throwable) { if (!ignoreError) throw ex }
    }
    fun hasKey(key: String, ignoreError: Boolean = true): Boolean {
        return try {
            redisTemplate.hasKey(key)
        } catch (ex: Throwable) { if (!ignoreError) throw ex else return false }
    }
}