package com.tw2.prepaid.common.support.redis

const val CHARGE_ERROR_KEY = "CHARGE_ERROR"
const val REDIS_KEY_DELIMITER = "#"

fun makeRedisKey(mainKey: String, subKeys: Map<String, String>) =
    "op=$mainKey${subKeys.entries.joinToString(prefix = REDIS_KEY_DELIMITER, separator = REDIS_KEY_DELIMITER)}"