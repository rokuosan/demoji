package org.yaken.demoji.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

class BufferedImageExtensionsTest : FunSpec({

    context("BufferedImage.toByteArray") {
        test("converts BufferedImage to ByteArray successfully") {
            val image = BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
            val byteArray = image.toByteArray()
            val inputStream = ByteArrayInputStream(byteArray)
            val readImage = ImageIO.read(inputStream)
            readImage.width shouldBe 100
            readImage.height shouldBe 100
        }
    }
})
