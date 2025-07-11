package me.fadlan.passwordgen.utils

import java.security.MessageDigest

class KeyHash {
    companion object {
        private fun sha256(text: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            val digested = md.digest(text.toByteArray())
            return digested.fold("", { str, it -> str + "%02x".format(it) })
        }

        fun hash(key: String, length: Int): String {
            return sha256(key).substring(0, length)
        }
    }
}