package org.yaken.demoji.emoji

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.font.FontRenderContext
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class EmojiBuilder(
    private val width: Int = 128,
    private val height: Int = 128,
) {
    var text: String = ""
    var fontFile: File? = null
    var color: Color? = Color.ORANGE
    var bgColor: Color? = null
    var autoWidth: Boolean = true

    fun build(): BufferedImage {
        val font = loadFont()
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()

        applyBackground(graphics)
        drawText(graphics, font)

        graphics.dispose()
        return image
    }

    private fun loadFont(): Font {
        return if (fontFile?.exists() == true) {
            Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(32f)
        } else {
            Font("SansSerif", Font.BOLD, 32)
        }
    }

    private fun applyBackground(graphics: Graphics2D) {
        if (bgColor == null) {
            graphics.composite = AlphaComposite.Clear
            graphics.fillRect(0, 0, width, height)
            graphics.composite = AlphaComposite.SrcOver
        } else {
            graphics.color = bgColor
            graphics.fillRect(0, 0, width, height)
        }
    }

    private fun drawText(graphics: Graphics2D, font: Font) {
        graphics.font = font
        graphics.color = color
        val frc = FontRenderContext(null, true, true)
        val lines = text.split("\n")

        // 行間の調整
        val lineSpacingFactor = 0.8
        val lineHeights = lines.map { font.getLineMetrics(it, frc).height * lineSpacingFactor }
        val totalTextHeight = lineHeights.sum()
        val stretchY = (height - 10) / totalTextHeight
        val margin = (height - lines.sumOf {
            font.getLineMetrics(it, frc).height * lineSpacingFactor * stretchY
        }) / 2

        // テキストを中央に配置するためのオフセット計算
        var yOffset = (height - 10 - totalTextHeight * stretchY) / 2
        for ((index, line) in lines.withIndex()) {
            val lineBounds = font.getStringBounds(line, frc)
            val textWidth = lineBounds.width.toInt()
            val stretchX = if (autoWidth) width / textWidth.toDouble() else 1.0
            val x = (width - textWidth * stretchX) / 2
            val ascent = font.getLineMetrics(line, frc).ascent
            val y = yOffset + ascent * stretchY - margin

            graphics.translate(x, y)
            graphics.scale(stretchX, stretchY)
            graphics.drawString(line, 0, 0)
            graphics.scale(1.0 / stretchX, 1.0 / stretchY)
            graphics.translate(-x, -y)

            yOffset += lineHeights[index] * stretchY
        }
    }

    companion object {
        fun saveImage(image: BufferedImage, outputFile: File) {
            ImageIO.write(image, "png", outputFile)
        }
    }
}

fun emojiBuilder(init: EmojiBuilder.() -> Unit): BufferedImage {
    return EmojiBuilder().apply(init).build()
}
