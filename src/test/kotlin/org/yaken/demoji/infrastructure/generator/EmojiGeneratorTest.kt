package org.yaken.demoji.infrastructure.generator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.opentelemetry.api.OpenTelemetry
import org.yaken.demoji.common.Result
import org.yaken.demoji.domain.entity.Emoji
import java.nio.file.Files
import javax.imageio.ImageIO

class EmojiGeneratorTest : FunSpec({
    val tracer = OpenTelemetry.noop().getTracer("test")

    test("generates transparent image when background is unset") {
        val generator = EmojiGenerator(tracer = tracer)
        val result = generator.generateImageFromEmoji(
            Emoji(
                text = "",
                color = "#FFFFFF",
                bgColor = "transparent",
            ),
        )

        (result is Result.Ok) shouldBe true
        val image = (result as Result.Ok).value

        image.width shouldBe 128
        image.height shouldBe 128
        (image.getRGB(0, 0) ushr 24) shouldBe 0
    }

    test("generates image with background color") {
        val generator = EmojiGenerator(tracer = tracer)
        val result = generator.generateImageFromEmoji(
            Emoji(
                text = "",
                color = "#000000",
                bgColor = "#FFFFFF",
            ),
        )

        (result is Result.Ok) shouldBe true
        val image = (result as Result.Ok).value

        image.width shouldBe 128
        image.height shouldBe 128
        (image.getRGB(0, 0)) shouldBe 0xFFFFFFFF.toInt()
    }

    test("writes generated image to temp png file") {
        val generator = EmojiGenerator(tracer = tracer)
        val result = generator.generateImageToTempFile(
            Emoji(
                text = "OK",
                color = "#000000",
                bgColor = "#FFFFFF",
            ),
        )

        (result is Result.Ok) shouldBe true
        val path = (result as Result.Ok).value

        try {
            Files.exists(path) shouldBe true
            (path.fileName.toString().endsWith(".png")) shouldBe true

            val image = ImageIO.read(path.toFile())
            image.width shouldBe 128
            image.height shouldBe 128
        } finally {
            Files.deleteIfExists(path)
        }
    }
})
