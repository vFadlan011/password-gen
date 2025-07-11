package me.fadlan.passwordgen.password

import java.util.Random

class MarkovPasswordGen(
    var charSetOption: CharSetOption = CharSetOption(),
    var wordNetworkAndUsedWords: Pair<HashMap<String, WordNode>, List<String>>,
): IPasswordGenerationStrategy {
    val random = Random()

    override fun generate(length: Int): String {
        val (wordNetwork, usedWords) = wordNetworkAndUsedWords
        val charSetList = charSetOption.getCharSet()
        var string = ""
        var currentWord = usedWords.random()

        while (string.length < length){
            if (random.nextDouble() < (1.0 - ( 1.0 / length.toFloat() ))) {
                string += currentWord
                currentWord = getNextWord(currentWord, wordNetwork) ?: ""
            } else {
                string += charSetList.random()
            }
        }

        return string.trim().substring(0, length-1)
    }

    private fun getNextWord(currentWord: String, wordNetwork: HashMap<String, WordNode>): String? {
        val nextWords = wordNetwork[currentWord]?.nextWords ?: return null
        val randomValue = random.nextDouble()
        var sum = 0.0
        for (word in nextWords.keys) {
            sum += nextWords[word]!!
            if (sum >= randomValue) {
                return word
            }
        }
        return null
    }
}
