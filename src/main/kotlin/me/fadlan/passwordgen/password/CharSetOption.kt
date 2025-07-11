package me.fadlan.passwordgen.password

class CharSetOption(
    var includeUppercase: Boolean = true,
    var includeLowercase: Boolean = true,
    var includeNumber: Boolean = true,
    var includeSpecial: Boolean = true
) {
    fun getCharSet(): List<Char> {
        val charSet = mutableListOf<Char>()
        if (includeUppercase) {
            charSet.addAll(CharSet.UPPERCASE)
        }
        if (includeLowercase) {
            charSet.addAll(CharSet.LOWERCASE)
        }
        if (includeNumber) {
            charSet.addAll(CharSet.NUMBER)
        }
        if (includeSpecial) {
            charSet.addAll(CharSet.SPECIAL)
        }
        return charSet
    }
}
