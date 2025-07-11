package me.fadlan.passwordgen.password

interface IPasswordGenerationStrategy {
    fun generate(length: Int): String
}