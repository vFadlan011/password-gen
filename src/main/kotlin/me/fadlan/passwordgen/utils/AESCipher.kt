package me.fadlan.passwordgen.utils

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.*

class AESCipher {
    companion object {
        private const val ALGORITHM = "AES/ECB/PKCS5Padding"

        fun encrypt(plaintext: String, key: String): String {
            val cipher = Cipher.getInstance(ALGORITHM)
            val keySpec = SecretKeySpec(key.toByteArray(), "AES")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            return Base64.getEncoder().encodeToString(encrypted)
        }

        fun decrypt(ciphertext: String, key: String): String {
            val cipher = Cipher.getInstance(ALGORITHM)
            val keySpec = SecretKeySpec(key.toByteArray(), "AES")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            val decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext))
            return String(decrypted, Charsets.UTF_8)
        }
    }
}