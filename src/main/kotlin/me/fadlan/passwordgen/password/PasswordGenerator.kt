package me.fadlan.passwordgen.password

class PasswordGenerator {
    fun generate(length: Int, strategy: IPasswordGenerationStrategy): String {
        return strategy.generate(length)
    }
}