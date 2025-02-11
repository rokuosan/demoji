package org.yaken.demoji.util

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun BufferedImage.toByteArray(): ByteArray {
    val output = ByteArrayOutputStream()
    ImageIO.write(this, "png", output)
    return output.toByteArray()
}
