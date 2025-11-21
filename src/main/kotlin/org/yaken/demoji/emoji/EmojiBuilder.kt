package org.yaken.demoji.emoji

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.font.TextAttribute
import java.awt.geom.AffineTransform
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
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.color = color

        val tracking = -0.1
        val attributes = mapOf(TextAttribute.TRACKING to tracking)
        val tightFont = font.deriveFont(attributes)

        val lines = text.split("\n").filter { it.isNotEmpty() }
        if (lines.isEmpty()) return

        val frc = graphics.fontRenderContext

        val heightPerLine = height.toDouble() / lines.size

        lines.forEachIndexed { index, line ->
            // 図形（GlyphVector）として扱う
            val glyphVector = tightFont.createGlyphVector(frc, line)
            val outline = glyphVector.outline

            // 図形の正確な範囲（インクがある部分の境界線）を取得
            val visualBounds = glyphVector.visualBounds

            // 横方向の倍率計算 (キャンバス幅 / 図形の実質の幅)
            val scaleX = if (autoWidth && visualBounds.width > 0) width.toDouble() / visualBounds.width else 1.0

            // 縦方向の倍率計算 (1行分の高さ / 図形の実質の高さ)
            val scaleY = if (visualBounds.height > 0) heightPerLine / visualBounds.height else 1.0

            val transform = AffineTransform()
            transform.translate(0.0, index * heightPerLine)
            transform.scale(scaleX, scaleY)
            // 図形の左上を原点(0,0)に合わせる
            // visualBounds.x/y はベースラインからのオフセットなので、それを打ち消す
            transform.translate(-visualBounds.x, -visualBounds.y)

            graphics.fill(transform.createTransformedShape(outline))
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
