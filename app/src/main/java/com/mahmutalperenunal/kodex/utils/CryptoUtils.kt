package com.mahmutalperenunal.kodex.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    private const val secretKey = "1234567890abcdef"
    private const val iv = "abcdef1234567890"

    private fun getKeySpec() = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
    private fun getIvSpec() = IvParameterSpec(iv.toByteArray(Charsets.UTF_8))

    fun encrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, getKeySpec(), getIvSpec())
        val encrypted = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, getKeySpec(), getIvSpec())
        val decoded = Base64.decode(input, Base64.DEFAULT)
        return String(cipher.doFinal(decoded), Charsets.UTF_8)
    }
}