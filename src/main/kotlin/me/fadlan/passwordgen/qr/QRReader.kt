package me.fadlan.passwordgen.qr

import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class QRReader() {
    fun read(qrCodeimage: File): String {
        val bufferedImage: BufferedImage = ImageIO.read(qrCodeimage)
        val source: LuminanceSource = BufferedImageLuminanceSource(bufferedImage)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            val result = MultiFormatReader().decode(bitmap)
            return result.getText()
        } catch (e: NotFoundException) {
            return "qr-404"
        }
    }
}