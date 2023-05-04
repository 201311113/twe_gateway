package com.tw2.prepaid.common.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tw2.prepaid.common.support.jackson.JacksonDecryptDeserializer

val mapper: ObjectMapper = jacksonObjectMapper()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .registerModule(JavaTimeModule())
    .registerModule(KotlinModule.Builder().build())
    //.registerModule(SimpleModule().also { it.addDeserializer(String::class.java, JacksonDecryptDeserializer()) })
fun toJsonString(value: Any): String = mapper.writeValueAsString(value)
inline fun <reified T> toObject(value: String): T = mapper.readValue(value, T::class.java)
fun mergeJsons(objectMapper: ObjectMapper = mapper, vararg values: Any): ObjectNode =
    values.fold(objectMapper.createObjectNode()) { acc, value -> mergeJson(acc, value, objectMapper) }
private fun mergeJson(v1: Any, v2: Any, objectMapper: ObjectMapper = mapper): ObjectNode =
    toObjectNode(v1, objectMapper).setAll(toObjectNode(v2, objectMapper))
private fun toObjectNode(v: Any, objectMapper: ObjectMapper = mapper): ObjectNode = when(v) {
    is String -> objectMapper.readTree(v) as ObjectNode
    is ObjectNode -> v
    else -> objectMapper.valueToTree(v)
}