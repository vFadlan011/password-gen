package me.fadlan.passwordgen.password

import java.util.*
import kotlin.collections.HashMap

class WordNetworkBuilder(var words: List<String>) {
    val random = Random()
    fun build(charSetOption: CharSetOption = CharSetOption()): Pair<HashMap<String, WordNode>, List<String>> {
        val network = HashMap<String, WordNode>()
        val usedWords = mutableListOf<String>()
        for (word in words) {
            val processedWord = processWord(word, charSetOption)
            val nextWords = buildNextWords(processedWord)
            network[processedWord] = WordNode(processedWord, nextWords)
            usedWords.add(processedWord)
        }
        return Pair(network, usedWords)
    }

    private fun processWord(word: String, charSetOption: CharSetOption): String {
        val replacements = mapOf(
            'a' to listOf('4', '@'),
            'e' to listOf('3'),
            'i' to listOf('1', '|', '!'),
            'o' to listOf('0'),
            'l' to listOf('1', '7'),
            's' to listOf('5', '$'),
            't' to listOf('+')
        )
        val processedWord = StringBuilder(word)
        if (!charSetOption.includeLowercase) {
            processedWord.replace(Regex("[a-z]")) { it.value.uppercase() }
        }
        if (charSetOption.includeUppercase && charSetOption.includeLowercase) {
            for (i in processedWord.indices) {
                val char = processedWord[i]
                if (random.nextDouble() < 0.3) {
                    processedWord[i] = char.uppercaseChar()
                }
                if (replacements.containsKey(char) && random.nextBoolean()) {
                    val replacement = replacements[char.lowercaseChar()]?.random() ?: continue
                    val charSet = charSetOption.getCharSet()
                    processedWord[i] =
                        if (charSet.contains(replacement)) replacement
                        else char
                }
            }
        }
        return processedWord.toString()
    }

    private fun buildNextWords(word: String): HashMap<String, Double> {
        val nextWords = HashMap<String, Double>()
        if (word.length > 1) {
            for (i in 1 until word.length) {
                val nextWord = word.substring(i)
                val count = nextWords.getOrDefault(nextWord, 0.0) + 1.0
                nextWords[nextWord] = count
            }
            val total = nextWords.values.sum()
            nextWords.replaceAll { _, value -> value / total }
        }
        return nextWords
    }
}
