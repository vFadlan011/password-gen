package me.fadlan.passwordgen

import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.*
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import me.fadlan.passwordgen.qr.QRReader
import me.fadlan.passwordgen.utils.AESCipher
import me.fadlan.passwordgen.utils.FileExplorer
import me.fadlan.passwordgen.utils.KeyHash
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.Flow
import javax.crypto.BadPaddingException
import javax.imageio.ImageIO

class ScanViewController {
    private val qrReader: QRReader = QRReader()
    private val clipboard: Clipboard = Clipboard.getSystemClipboard()
    private lateinit var qr: File
    private lateinit var tempQr: File
    private var isDecryptWindowShowed: Boolean = false

    private fun <T: Pane> swapChildren(parent: T) {
        if (parent.children.size >= 2) {
            val first = parent.children.removeFirst()
            val second = parent.children.removeFirst()
            parent.children.add(0, second)
            parent.children.add(1, first)
        }
    }


    @FXML
    private lateinit var imgQR: ImageView
    @FXML
    private lateinit var txtImageFile: TextField
    @FXML
    private lateinit var btnOpenFile: Button
    @FXML
    private lateinit var btnScan: Button
    @FXML
    private lateinit var txtEncrypted: TextArea
    @FXML
    private lateinit var contDecryptionKey: FlowPane
    @FXML
    private lateinit var passDecryptionKey: PasswordField
    @FXML
    private lateinit var txtDecryptionKey: TextField
    @FXML
    private lateinit var btnHideDecryptionKey: Button
    @FXML
    private lateinit var btnDecrypt: Button
    @FXML
    private lateinit var contPassword: FlowPane
    @FXML
    private lateinit var passPassword: PasswordField
    @FXML
    private lateinit var txtPassword: TextField
    @FXML
    private lateinit var btnHidePassword: Button
    @FXML
    private lateinit var btnCopy: Button

    @FXML
    private fun sceneOnKeyPressed(keyEvent: KeyEvent) {
        val pasteKeyCombination = KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY)
        if (pasteKeyCombination.match(keyEvent) && clipboard.hasImage()) {
            imgQR.image = clipboard.image
            if (!::qr.isInitialized) {
                val qrFile = Files.createTempFile("qr" + Date().time, ".png").toFile()
                qrFile.deleteOnExit()
                qr = qrFile
            } else qr.delete()
            ImageIO.write(SwingFXUtils.fromFXImage(imgQR.image, null), "png", qr)
        }
    }

    @FXML
    private fun btnOpenFileOnClick() {
        val stage: Stage = Stage()
        FileExplorer.openFileDialog(stage) { selectedFile ->
            qr = selectedFile
            txtImageFile.text = selectedFile.toURI().toURL().toString()
            imgQR.image = Image(selectedFile.toURI().toURL().toExternalForm())
        }
    }

    @FXML
    private fun btnScanOnClick() {
        if (!::qr.isInitialized) {
            val alert = Alert(AlertType.INFORMATION)
            alert.title = "No Image Found"
            alert.headerText = "No Image Found"
            alert.contentText = "Upload your QR code, or paste an image on the text field."
        } else {
            val result = qrReader.read(qr)
            if (result=="qr-404") {
                val alert = Alert(AlertType.INFORMATION)
                alert.title = "QR Code Not Found"
                alert.headerText = "No QR Code Found"
                alert.contentText = "Try again with a sharper image, make sure it contains QR."
            } else {
                txtEncrypted.isWrapText = true
                txtEncrypted.text = result
            }
        }
    }

    @FXML
    private fun decryptionKeyOnChange(){
        if (passDecryptionKey.isVisible) {
            txtDecryptionKey.text = passDecryptionKey.text
        } else {
            passDecryptionKey.text = txtDecryptionKey.text
        }
    }

    @FXML
    private fun btnHideDecryptionKeyOnClick() {
        if (passDecryptionKey.isVisible) {
            passDecryptionKey.isVisible = false
            txtDecryptionKey.isVisible = true
        } else {
            txtDecryptionKey.isVisible = false
            passDecryptionKey.isVisible = true
        }
        swapChildren(contDecryptionKey)
    }

    @FXML
    private fun btnDecryptOnClick() {
        try {
            val hashedKey = KeyHash.hash(txtDecryptionKey.text, 32)
            val decryptedPassword = AESCipher.decrypt(txtEncrypted.text, hashedKey)
            txtPassword.text = decryptedPassword
            passPassword.text = decryptedPassword
        } catch (e: BadPaddingException) {
            val alert = Alert(AlertType.INFORMATION)
            alert.title = "Wrong Key"
            alert.headerText = "Wrong Decryption Key"
            alert.contentText = "Try to remember it again."
            alert.showAndWait()
        }
    }

    @FXML
    private fun btnHidePasswordOnClick() {
        if (passPassword.isVisible) {
            passPassword.isVisible = false
            txtPassword.isVisible = true
        } else {
            txtPassword.isVisible = false
            passPassword.isVisible = true
        }
        swapChildren(contPassword)
    }

    @FXML
    private fun btnCopyOnClick() {
        val content = ClipboardContent()
        content.putString(txtPassword.text)
        clipboard.setContent(content)
    }
}