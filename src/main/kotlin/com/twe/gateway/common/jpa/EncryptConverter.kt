package com.tw2.prepaid.common.jpa

import com.tw2.prepaid.common.pp
import com.tw2.prepaid.common.properties.SecretKey
import com.tw2.prepaid.common.utils.ENC_PREFIX
import com.tw2.prepaid.common.utils.ENC_SUFFIX
import com.tw2.prepaid.common.utils.decryptAES256
import com.tw2.prepaid.common.utils.encryptAES256
import javax.persistence.AttributeConverter

class EncryptConverter: AttributeConverter<String, String> {
    override fun convertToDatabaseColumn(attribute: String?): String? = attribute?.let {
        val key = pp.getSecretValue(SecretKey.DB_ENCRYPTION_KEY)
        val iv = pp.getSecretValue(SecretKey.DB_ENCRYPTION_IV)
        ENC_PREFIX + encryptAES256(text = it, key = key, iv = iv) + ENC_SUFFIX
    }

    override fun convertToEntityAttribute(dbData: String?): String? = dbData?.let {
        val key = pp.getSecretValue(SecretKey.DB_ENCRYPTION_KEY)
        val iv = pp.getSecretValue(SecretKey.DB_ENCRYPTION_IV)
        if (it.startsWith(ENC_PREFIX) && it.endsWith(ENC_SUFFIX))
            decryptAES256(cipherText = it.substring(ENC_PREFIX.length, it.length - ENC_SUFFIX.length), key = key, iv = iv)
        else it
    }
}