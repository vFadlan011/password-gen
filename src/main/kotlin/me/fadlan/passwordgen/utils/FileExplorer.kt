package me.fadlan.passwordgen.utils

import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File

class FileExplorer {
    companion object {
        fun openFileDialog(primaryStage: Stage, onFileSelected: (File) -> Unit) {
            val fileChooser = FileChooser()
            fileChooser.title = "Open File"

            val selectedFile = fileChooser.showOpenDialog(primaryStage)

            if (selectedFile != null) {
                onFileSelected(selectedFile)
            }
        }

        fun saveFileDialog(primaryStage: Stage, onFileSelected: (File) -> Unit) {
            val fileChooser = FileChooser()
            fileChooser.title = "Save File"

            val extensionFilter = FileChooser.ExtensionFilter("PNG Image (*.png)", "*.png")
            fileChooser.extensionFilters.addAll(extensionFilter)

            val selectedFile = fileChooser.showSaveDialog(primaryStage)

            if (selectedFile != null) {
                onFileSelected(selectedFile)
            }
        }
    }
}