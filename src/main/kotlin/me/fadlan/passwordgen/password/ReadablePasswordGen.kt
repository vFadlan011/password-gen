package me.fadlan.passwordgen.password

import java.util.*

class ReadablePasswordGen(var charSetOption: CharSetOption, var words: List<String>): IPasswordGenerationStrategy {
    private val random = Random()
    override fun generate(length: Int): String {
        val totalWords = random.nextInt(length / 5) + 1
        val maxWordLength = if (totalWords==1) 4 else length / totalWords
        words = words.filter { it.length <= maxWordLength }

        val chosenWords = mutableListOf<String>()
        for (i in 0 until totalWords) {
            var chosenWord = words[random.nextInt(words.size)]
            chosenWord =
                if (charSetOption.includeUppercase && charSetOption.includeLowercase && random.nextBoolean()) chosenWord.replaceFirstChar() {c -> c.uppercase()}
                else if (charSetOption.includeUppercase && random.nextBoolean()) chosenWord.uppercase()
                else chosenWord.lowercase()
            chosenWords.add(chosenWord)
        }

        var remainingLength = length - (chosenWords.map {it.length}).sum()
        var classicRandomStr = ClassicPasswordGen(charSetOption).generate(remainingLength)

        val parts = mutableListOf<String>()
        for (word in chosenWords) {
            parts.add(word)
            if (remainingLength > 0) {
                val chunkLength = random.nextInt(Math.min(remainingLength, length)) + 1
                parts.add(classicRandomStr.substring(0, chunkLength))
                remainingLength -= chunkLength
                classicRandomStr = classicRandomStr.substring(chunkLength)
            }
        }
        if (remainingLength > 0) {
            val chunkLength = random.nextInt(Math.min(remainingLength, length)) + 1
            parts.add(classicRandomStr.substring(0, chunkLength))
            remainingLength -= chunkLength
        }

        Collections.shuffle(parts)

        val generatedPassword = parts.joinToString("")
        return generatedPassword
    }
}