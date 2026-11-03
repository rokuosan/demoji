package org.yaken.demoji.application.usecase

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.opentelemetry.api.OpenTelemetry
import org.yaken.demoji.common.Result
import org.yaken.demoji.domain.entity.Emoji
import org.yaken.demoji.domain.entity.EmojiFont
import org.yaken.demoji.infrastructure.generator.EmojiGenerator
import java.nio.file.Files

class EmojiCreationUseCaseTest : FunSpec({
    test("creates valid draft from input") {
        val useCase = testUseCase()

        val result = useCase.createDraft(
            EmojiDraftInput(
                name = "example",
                text = "EXAMPLE",
                color = "#FF5733",
                bgColor = "#FFFFFF",
            ),
        )

        (result is Result.Ok) shouldBe true
        val emoji = (result as Result.Ok).value
        emoji.name shouldBe "example"
        emoji.text shouldBe "EXAMPLE"
        emoji.color shouldBe "#FF5733"
        emoji.bgColor shouldBe "#FFFFFF"
    }

    test("applies default colors to draft input") {
        val useCase = testUseCase()

        val result = useCase.createDraft(
            EmojiDraftInput(
                name = "example",
                text = "EXAMPLE",
                color = null,
                bgColor = null,
            ),
        )

        (result is Result.Ok) shouldBe true
        val emoji = (result as Result.Ok).value
        emoji.color shouldBe "#EC71A1"
        emoji.bgColor shouldBe "transparent"
    }

    test("returns validation error for invalid draft") {
        val useCase = testUseCase()

        val result = useCase.createDraft(
            EmojiDraftInput(
                name = "x",
                text = "EXAMPLE",
                color = "#FF5733",
                bgColor = null,
            ),
        )

        result shouldBe Result.Err("名前は2文字以上32文字以下である必要があります")
    }

    test("returns available fonts") {
        val useCase = testUseCase()

        useCase.getAvailableFonts() shouldBe listOf(
            EmojiFont(
                id = "noto-sans-mono-cjk-jp-bold",
                name = "Noto Sans Mono CJK JP Bold",
                filename = "NotoSansMonoCJKjp-Bold.otf",
            ),
        )
    }

    test("generates preview file through generator service") {
        val useCase = testUseCase()
        val emoji = Emoji(
            name = "example",
            text = "OK",
            color = "#000000",
            bgColor = "#FFFFFF",
        )

        val result = useCase.generatePreviewFile(emoji)

        (result is Result.Ok) shouldBe true
        val path = (result as Result.Ok).value

        try {
            Files.exists(path) shouldBe true
        } finally {
            Files.deleteIfExists(path)
        }
    }
})

private fun testUseCase(): EmojiCreationUseCase {
    val tracer = OpenTelemetry.noop().getTracer("test")
    return EmojiCreationUseCaseImpl(
        generator = EmojiGenerator(tracer = tracer),
        emojiFontUseCase = EmojiFontUseCaseImpl(),
    )
}
