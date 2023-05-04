package com.tw2.prepaid.common.support.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.tw2.prepaid.common.utils.ENC_PREFIX
import com.tw2.prepaid.common.utils.ENC_SUFFIX
import com.tw2.prepaid.common.utils.decryptAES256

class JacksonDecryptDeserializer(val key: String, val iv: String): JsonDeserializer<String>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): String {
        val text = p.text.trim()
        return if (text.startsWith(ENC_PREFIX) && text.endsWith(ENC_SUFFIX))
            decryptAES256(text.substring(ENC_PREFIX.length, text.length - ENC_SUFFIX.length), key = key, iv = iv)
        else text
    }
    override fun handledType() = String::class.java
}