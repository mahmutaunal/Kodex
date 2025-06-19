package com.mahmutalperenunal.kodex.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    private const val PASSWORD = "SuperSecretPassword"
    private const val SALT = "SharedSalt1234"
    private const val IV = "1234567890abcdef"

    private fun getKeySpec(): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(PASSWORD.toCharArray(), SALT.toByteArray(), 10000, 128)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    private fun getIvSpec(): IvParameterSpec {
        return IvParameterSpec(IV.toByteArray(Charsets.UTF_8))
    }

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