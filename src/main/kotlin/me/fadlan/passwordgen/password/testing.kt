package me.fadlan.passwordgen.password

fun main() {
    val charSetOption = CharSetOption()
    val wordNetwork = WordNetworkBuilder(Dictionary().INDONESIA_WORD).build(charSetOption)

    val mv = MarkovPasswordGen(charSetOption, wordNetwork)
    val cl = ClassicPasswordGen(charSetOption)
    val rb = ReadablePasswordGen(charSetOption, Dictionary().INDONESIA_WORD)
    for (i in 8..50) println(mv.generate(i))
    for (i in 8..50) println(cl.generate(i))
    for (i in 8..50) println(rb.generate(i))
}