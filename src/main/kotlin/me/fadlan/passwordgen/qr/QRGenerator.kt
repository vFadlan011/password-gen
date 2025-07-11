package me.fadlan.passwordgen.qr
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import java.io.File
import java.nio.file.Files
import java.util.Date
import kotlin.io.path.deleteIfExists

class QRGenerator(var text: String) {
    private val writer: MultiFormatWriter = MultiFormatWriter()
    private val width: Int = 256
    private val height: Int  = 256
    private lateinit var bitMatrix: BitMatrix
    private val addrs = Files.createTempFile("qr" + Date().time, ".png")

    fun generate() {
        bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height)
    }

    fun store() {
        MatrixToImageWriter.writeToPath(bitMatrix, "png", addrs)
    }

    fun store(path: File) {
        MatrixToImageWriter.writeToPath(bitMatrix, "png", path.toPath())
    }

    fun getImage(): File {
        val image = addrs.toFile()
        image.deleteOnExit()
        return image
    }

    fun destroy() {
        addrs.deleteIfExists()
    }
}