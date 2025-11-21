package org.yaken.demoji.infrastructure.generator

import org.yaken.demoji.common.Result
import org.yaken.demoji.common.err
import org.yaken.demoji.common.ok
import org.yaken.demoji.domain.entity.Emoji
import org.yaken.demoji.domain.service.EmojiGeneratorService
import org.yaken.demoji.infrastructure.otel.withSpan
import java.awt.AlphaComposite
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.font.TextAttribute
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO


class EmojiGenerator(
    private val autoWidth: Boolean = true,
    private val width: Int = 128,
    private val height: Int = 128,
) : EmojiGeneratorService {
    override suspend fun generateImageFromEmoji(emoji: Emoji): Result<BufferedImage, Error> =
        withSpan("Generate Image From Emoji") {
            return@withSpan try {
            val image = build(emoji)
            ok(image)
        } catch (e: Exception) {
            err(Error("Failed to generate image: ${e.message}"))
        }
    }

    override suspend fun generateImageToTempFile(emoji: Emoji): Result<Path, Error> {
        return try {
            val image = build(emoji)
            val tempFile = kotlin.io.path.createTempFile(suffix = ".png")
            ImageIO.write(image, "PNG", tempFile.toFile())
            ok(tempFile)
        } catch (e: Exception) {
            err(Error("Failed to generate image file: ${e.message}"))
        }
    }

    private suspend fun build(emoji: Emoji): BufferedImage = withSpan("Build Emoji Image") {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()

        applyBackground(emoji, graphics)
        drawText(emoji, graphics, getFont(emoji))

        graphics.dispose()
        return@withSpan image
    }

    private fun getFont(emoji: Emoji): Font {
        return if (emoji.fontFile()?.exists() == true) {
            Font.createFont(Font.TRUETYPE_FONT, emoji.fontFile())
                .deriveFont(32f)
        } else {
            Font("SansSerif", Font.BOLD, 32)
        }
    }

    private suspend fun applyBackground(emoji: Emoji, graphics: Graphics2D) = withSpan("Apply Background") {
        val bg = emoji.bgColorInAwtOrNull()
        if (bg == null) {
            graphics.composite = AlphaComposite.Clear
            graphics.fillRect(0, 0, width, height)
            graphics.composite = AlphaComposite.SrcOver
        } else {
            graphics.color = bg
            graphics.fillRect(0, 0, width, height)
        }
    }

    private suspend fun drawText(emoji: Emoji, graphics: Graphics2D, font: Font) = withSpan("Draw Text") {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.color = emoji.colorInAWT()

        val tracking = -0.1
        val attributes = mapOf(TextAttribute.TRACKING to tracking)
        val tightFont = font.deriveFont(attributes)

        val lines = (emoji.text ?: "").split("\n").filter { it.isNotEmpty() }
        if (lines.isEmpty()) return@withSpan

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
}
