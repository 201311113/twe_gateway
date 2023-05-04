package com.tw2.prepaid.common.support.jackson

import com.fasterxml.jackson.databind.util.StdConverter
import com.tw2.prepaid.common.pp
import com.tw2.prepaid.common.properties.SecretKey
import com.tw2.prepaid.common.utils.ENC_PREFIX
import com.tw2.prepaid.common.utils.ENC_SUFFIX
import com.tw2.prepaid.common.utils.encryptAES256

class JacksonEncryptConverter: StdConverter<String, String>() {
    val key = pp.getSecretValue(SecretKey.API_ENCRYPTION_KEY)
    val iv = pp.getSecretValue(SecretKey.API_ENCRYPTION_IV)
    override fun convert(value: String?) = value?.let {
        if (pp.encryption.apiEnabled) ENC_PREFIX + encryptAES256(it, key = key, iv = iv) + ENC_SUFFIX else it
    }
}