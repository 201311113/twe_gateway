package com.tw2.prepaid.common.utils

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val transType = "AES/CBC/PKCS5Padding"
private const val alg = "AES"
const val ENC_PREFIX = "ENC("
const val ENC_SUFFIX = ")"
fun encryptAES256(text: String, key: String, iv: String): String {
    val cipher: Cipher = Cipher.getInstance(transType)
    val keySpec = SecretKeySpec(key.toByteArray(), alg)
    val ivParamSpec = IvParameterSpec(iv.toByteArray())
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec)

    val encrypted: ByteArray = cipher.doFinal(text.toByteArray())
    return Base64.getEncoder().encodeToString(encrypted)
}
fun decryptAES256(cipherText: String, key: String, iv: String): String {
    val cipher = Cipher.getInstance(transType)
    val keySpec = SecretKeySpec(key.toByteArray(), alg)
    val ivParamSpec = IvParameterSpec(iv.toByteArray())
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec)

    val decodedBytes = Base64.getDecoder().decode(cipherText)
    val decrypted = cipher.doFinal(decodedBytes)
    return String(decrypted)
}