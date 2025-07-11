package me.fadlan.passwordgen

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class PasswordGenApplication : Application() {
    companion object {
        fun main(args: Array<String>) {
            launch(*args)
        }
    }

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(this::class.java.getResource("main.fxml"))
        val scene = Scene(fxmlLoader.load(), 640.0, 444.0)
        stage.title = "Password Generator"
        stage.scene = scene
        stage.isResizable = false
        stage.icons.add(
            javafx.scene.image.Image(
                this::class.java.getResource("icon.png").toURI().toURL().toExternalForm()
            )
        )
        stage.show()
    }
}