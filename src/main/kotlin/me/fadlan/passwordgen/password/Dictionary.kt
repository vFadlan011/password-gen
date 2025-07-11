package me.fadlan.passwordgen.password

import com.google.gson.Gson
import me.fadlan.passwordgen.PasswordGenApplication

class Dictionary {
    val INDONESIA_WORD = loadWordList("indonesia.json")
    val ENGLISH_WORD = loadWordList("english.json")

    private fun loadWordList(path: String): List<String> {
        val inputStream = PasswordGenApplication::class.java.getResourceAsStream(path) ?: throw IllegalArgumentException("File not found: $path")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val gson = Gson()
        val wordList = gson.fromJson(jsonString, Array<String>::class.java).toList()
        return wordList
    }
}