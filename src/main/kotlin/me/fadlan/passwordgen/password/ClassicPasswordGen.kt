package me.fadlan.passwordgen.password

class ClassicPasswordGen(var charSetOption: CharSetOption): IPasswordGenerationStrategy {
    override fun generate(length: Int): String {
        val charSetList = charSetOption.getCharSet()
        val password = StringBuilder()
        for (i in 0 until length) {
            password.append(charSetList.random())
        }
        return password.toString()
    }
}