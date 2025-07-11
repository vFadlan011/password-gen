package me.fadlan.passwordgen

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image;
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import me.fadlan.passwordgen.password.*
import me.fadlan.passwordgen.qr.QRGenerator
import me.fadlan.passwordgen.utils.*
import net.synedra.validatorfx.Validator


class MainViewController {
    private val validator: Validator = Validator()
    private val clipboard: Clipboard = Clipboard.getSystemClipboard()
    private lateinit var lastQRGenerator: QRGenerator
    private var isAboutWindowShowed: Boolean = false
    private var isScanWindowShowed: Boolean = false

    private val charSetOption = CharSetOption()
    private val dict = Dictionary()
    private var words = dict.ENGLISH_WORD
    private val classic = ClassicPasswordGen(charSetOption)
    private val readable = ReadablePasswordGen(charSetOption, words)
    private val wordNetwork = WordNetworkBuilder(words)
    private val markov = MarkovPasswordGen(charSetOption, wordNetwork.build())
    private var strategy: IPasswordGenerationStrategy = classic
    private val passwordGen = PasswordGenerator()

    private fun <T: Pane> swapChildren(parent: T) {
        if (parent.children.size >= 2) {
            val first = parent.children.removeFirst()
            val second = parent.children.removeFirst()
            parent.children.add(0, second)
            parent.children.add(1, first)
        }
    }

    @FXML
    private lateinit var btnScanQR: Button
    @FXML
    private lateinit var btnAbout: Button
    @FXML
    private lateinit var sliderPasswordLength: Slider
    @FXML
    private lateinit var txtPasswordLength: TextField
    @FXML
    private lateinit var cbxUseUppercase: CheckBox
    @FXML
    private lateinit var cbxUseLowercase: CheckBox
    @FXML
    private lateinit var cbxUseNumber: CheckBox
    @FXML
    private lateinit var cbxUseSpecial: CheckBox
    @FXML
    private lateinit var cbxAddReadableWords: CheckBox
    @FXML
    private lateinit var cbxAddIndonesianWords: CheckBox
    @FXML
    private lateinit var btnGeneratePassword: Button
    @FXML
    private lateinit var contPassword: FlowPane
    @FXML
    private lateinit var passGeneratedPassword: PasswordField
    @FXML
    private lateinit var txtGeneratedPassword: TextField
    @FXML
    private lateinit var btnHidePassword: Button
    @FXML
    private lateinit var btnCopy: Button
    @FXML
    private lateinit var tooltipBtnCopy: Tooltip
    @FXML
    private lateinit var contEncryptionKey: FlowPane
    @FXML
    private lateinit var passEncryptionKey: PasswordField
    @FXML
    private lateinit var txtEncryptionKey: TextField
    @FXML
    private lateinit var btnHideEncryptionKey: Button
    @FXML
    private lateinit var btnGenerateQR: Button
    @FXML
    private lateinit var tooltipBtnGenerateQR: Tooltip
    @FXML
    private lateinit var imgQR: ImageView
    @FXML
    private lateinit var btnSave: Button
    @FXML
    private lateinit var tooltipBtnSave: Tooltip
    @FXML
    private lateinit var rdbClassicMethod: RadioButton
    @FXML
    private lateinit var rdbMarkovMethod: RadioButton

    @FXML
    private fun initialize() {
        val generationMethod: ToggleGroup = ToggleGroup()
        rdbClassicMethod.toggleGroup = generationMethod
        rdbMarkovMethod.toggleGroup = generationMethod
    }

    @FXML
    private fun btnScanQROnClick(){
        val stage: Stage = Stage()

        if (!isScanWindowShowed) {
            val scanFXMLLoader: FXMLLoader = FXMLLoader(this::class.java.getResource("scan.fxml"))
            val root: Parent = scanFXMLLoader.load()
            stage.title = "Scan QR Code"
            stage.scene = Scene(root)
            stage.isResizable = false
            stage.icons.add(
                javafx.scene.image.Image(
                    this::class.java.getResource("icon.png").toURI().toURL().toExternalForm()
                )
            )
            stage.show()
            isScanWindowShowed = true
        } else {
            stage.toFront()
        }

        stage.setOnCloseRequest { _ -> isScanWindowShowed = false }
    }

    @FXML
    private fun btnAboutOnClick() {
        val stage: Stage = Stage()

        if (!isAboutWindowShowed) {
            val aboutFXMLLoader: FXMLLoader = FXMLLoader(this::class.java.getResource("about.fxml"))
            val root: Parent = aboutFXMLLoader.load()
            stage.title = "About"
            stage.scene = Scene(root)
            stage.isResizable = false
            stage.icons.add(
                javafx.scene.image.Image(
                    this::class.java.getResource("icon.png").toURI().toURL().toExternalForm()
                )
            )
            stage.show()
            isAboutWindowShowed = true
        } else {
            stage.toFront()
        }

        stage.setOnCloseRequest { _ -> isAboutWindowShowed = false }
    }

    @FXML
    private fun sliderPasswordLengthOnChange() {
        txtPasswordLength.text = sliderPasswordLength.value.toInt().toString()
    }

    @FXML
    private fun txtPasswordLengthOnAction() {
        validator.createCheck()
            .dependsOn("txtPasswordLength", txtPasswordLength.textProperty())
            .withMethod { c ->
                val doubleValue = c.get<String>("txtPasswordLength").toDoubleOrNull()
                if (doubleValue == null) c.error("Password length only accept integers")
                else if (doubleValue < 8) c.error("Minimum password length is 8 characters")
                else if (doubleValue > 120) c.error("Maximum password length is 120 characters")
                else sliderPasswordLength.value = doubleValue
            }
            .decorates(txtPasswordLength)
            .immediate()
    }

    private fun selectOneRandomCharType(event: ActionEvent) {
        val charTypeOptions = listOf(cbxUseUppercase, cbxUseLowercase, cbxUseNumber, cbxUseSpecial).filter { it != event.target }
        val selectedCheckBox = charTypeOptions.randomOrNull()
        selectedCheckBox?.isSelected = true
    }

    @FXML
    private fun charTypeOnChange(event: ActionEvent) {
        if (!(cbxUseUppercase.isSelected or cbxUseLowercase.isSelected or cbxUseNumber.isSelected or cbxUseSpecial.isSelected)) {
            val alert = Alert(Alert.AlertType.INFORMATION)
            alert.title = "Info"
            alert.headerText = "Select at least one character type for your password"
            alert.contentText = "Don't worry, we pick it up for you."
            alert.showAndWait()
            selectOneRandomCharType(event)
        }

        if (!(cbxUseUppercase.isSelected or cbxUseLowercase.isSelected)) {
            cbxAddReadableWords.isSelected = false
            cbxAddReadableWords.isDisable = true
            cbxAddIndonesianWords.isSelected = false
            cbxAddIndonesianWords.isDisable = true
        } else {
            cbxAddReadableWords.isDisable = false
        }
    }

    @FXML
    private fun cbxUseReadableOnChange() {
        if (cbxAddReadableWords.isSelected) {
            cbxAddIndonesianWords.isDisable = false
        } else {
            cbxAddIndonesianWords.isSelected = false
            cbxAddIndonesianWords.isDisable = true
        }
    }

    private fun refreshCharsetOption() {
        charSetOption.includeUppercase = cbxUseUppercase.isSelected
        charSetOption.includeLowercase = cbxUseLowercase.isSelected
        charSetOption.includeNumber = cbxUseNumber.isSelected
        charSetOption.includeSpecial = cbxUseSpecial.isSelected
    }

    private fun refreshLang(){
        if (cbxAddIndonesianWords.isSelected) {
            words = dict.INDONESIA_WORD
            wordNetwork.words = dict.INDONESIA_WORD
            readable.words = dict.INDONESIA_WORD
            markov.wordNetworkAndUsedWords = wordNetwork.build()
        }
        else {
            words = dict.ENGLISH_WORD
            wordNetwork.words = dict.ENGLISH_WORD
            readable.words = dict.ENGLISH_WORD
            markov.wordNetworkAndUsedWords = wordNetwork.build()
        }
    }

    private fun refreshMethod() {
        if (cbxAddReadableWords.isSelected) {
            if (rdbClassicMethod.isSelected) strategy = readable
            else strategy = markov
        } else strategy = classic
    }

    @FXML
    private fun btnGeneratePasswordOnClick() {
        refreshCharsetOption()
        refreshLang()
        refreshMethod()
        val passwordLength = txtPasswordLength.text.toInt()
        val password = passwordGen.generate(passwordLength, strategy)
        txtGeneratedPassword.text = password
        passGeneratedPassword.text = password

        tooltipBtnCopy.text = "Copy Password"
        tooltipBtnGenerateQR.text = if (txtEncryptionKey.text == "") "Please, enter an encryption key."
                                else "Generate QR Code"
    }

    @FXML
    private fun generatedPasswordOnChange() {
        if (passGeneratedPassword.isVisible)  {
            txtGeneratedPassword.text = passGeneratedPassword.text
        } else {
            passGeneratedPassword.text = txtGeneratedPassword.text
        }
    }

    @FXML
    private fun btnHidePasswordOnClick() {
        if (passGeneratedPassword.isVisible) {
            passGeneratedPassword.isVisible = false
            txtGeneratedPassword.isVisible = true
        } else {
            txtGeneratedPassword.isVisible = false
            passGeneratedPassword.isVisible = true
        }
        swapChildren(contPassword)
    }

    @FXML
    private fun btnCopyOnClick() {
        val content = ClipboardContent()
        content.putString(txtGeneratedPassword.text)
        clipboard.setContent(content)
    }

    private fun generateQR() {
        if(::lastQRGenerator.isInitialized) lastQRGenerator.destroy()
        else tooltipBtnSave.text = "Save your generated QR Code"

        val hashedKey = KeyHash.hash(txtEncryptionKey.text, 32)
        val encryptedPassword = AESCipher.encrypt(txtGeneratedPassword.text, hashedKey)

        val qr = QRGenerator(encryptedPassword)
        qr.generate()
        qr.store()
        imgQR.image = Image(qr.getImage().toURI().toURL().toExternalForm())

        lastQRGenerator = qr
        lastQRGenerator.getImage().deleteOnExit()
    }

    @FXML
    private fun encryptionKeyOnChange() {
        validator.createCheck()
            .dependsOn("txtEncryptionKey", passEncryptionKey.textProperty())
            .withMethod { c ->
                val key = c.get<String>("txtEncryptionKey")
                if (key == "") {
                    c.error("Please, fill the encryption key.")
                } else {
                    tooltipBtnGenerateQR.text = "Generate QR Code"
                    if (passEncryptionKey.isVisible) {
                        txtEncryptionKey.text = passEncryptionKey.text
                    } else {
                        passEncryptionKey.text = txtEncryptionKey.text
                    }
                }
            }
            .decorates(passEncryptionKey)
            .decorates(txtEncryptionKey)
            .immediate()
    }

    @FXML
    private fun btnHideEncryptionKeyOnClick() {
        if (passEncryptionKey.isVisible) {
            passEncryptionKey.isVisible = false
            txtEncryptionKey.isVisible = true
        } else {
            txtEncryptionKey.isVisible = false
            passEncryptionKey.isVisible = true
        }
        swapChildren(contEncryptionKey)
    }

    @FXML
    private fun btnGenerateQROnClick() {
        if (txtEncryptionKey.text == null || txtEncryptionKey.text == "") {
            val alert =  Alert(Alert.AlertType.WARNING)
            alert.title = "Fill the encryption key"
            alert.headerText = "Please fill the encryption key"
            alert.contentText = "Encryption key will be used to protect your QR Code. QR Code that will be generated is always encrypted with this key."
            alert.showAndWait()
        } else {
            generateQR()
        }
    }

    @FXML
    private fun btnSaveOnClick() {
        val stage: Stage = Stage()
        FileExplorer.saveFileDialog(stage) { selectedFile ->
            lastQRGenerator.store(selectedFile)
        }
    }
}