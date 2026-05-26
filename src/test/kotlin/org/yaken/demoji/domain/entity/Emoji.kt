package org.yaken.demoji.domain.entity

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.yaken.demoji.common.Result
import java.awt.Color

class EmojiTest: FunSpec({
    context("validate") {
        test("valid input") {
            val emoji = Emoji(
                name = "example",
                text = "EXAMPLE",
                color = "#FF5733",
            )

            val result = emoji.validate(strict = true)
            result shouldBe Result.Ok(Unit)
        }

        test("too short name") {
            val emoji = Emoji(
                name = "a",
                text = "EXAMPLE",
                color = "#FF5733",
            )

            val result = emoji.validate(strict = true)
            result shouldBe Result.Err("名前は2文字以上32文字以下である必要があります")
        }

        test("too long name") {
            val emoji = Emoji(
                name = "a".repeat(33),
                text = "EXAMPLE",
                color = "#FF5733",
            )

            val result = emoji.validate(strict = true)
            result shouldBe Result.Err("名前は2文字以上32文字以下である必要があります")
        }

        test("name is required in strict mode") {
            val emoji = Emoji(
                name = null,
                text = "EXAMPLE",
                color = "#FF5733",
            )

            val result = emoji.validate(strict = true)
            result shouldBe Result.Err("名前は必須です")
        }

        test("name is not required in non-strict mode") {
            val emoji = Emoji(
                name = null,
                text = "EXAMPLE",
                color = "#FF5733",
            )

            val result = emoji.validate(strict = false)
            result shouldBe Result.Ok(Unit)
        }

        test("color is invalid") {
            val emoji = Emoji(
                name = "example",
                text = "EXAMPLE",
                color = "invalid_color",
            )

            val result = emoji.validate(strict = true)
            result shouldBe Result.Err("文字色が不正です")
        }
    }

    context("color conversion") {
        test("converts hex text color to AWT color") {
            val emoji = Emoji(color = "#FF5733")

            emoji.colorInAWT() shouldBe Color(0xFF5733)
        }

        test("uses black when text color is invalid") {
            val emoji = Emoji(color = "invalid")

            emoji.colorInAWT() shouldBe Color(0)
        }

        test("returns background color when background is a hex color") {
            val emoji = Emoji(bgColor = "#FFFFFF")

            emoji.bgColorInAwtOrNull() shouldBe Color(0xFFFFFF)
        }

        test("returns null when background is transparent") {
            val emoji = Emoji(bgColor = "transparent")

            emoji.bgColorInAwtOrNull() shouldBe null
        }
    }

    context("fontFile") {
        test("returns existing font file") {
            val emoji = Emoji(font = "NotoSansMonoCJKjp-Bold.otf")

            emoji.fontFile()?.exists() shouldBe true
        }

        test("returns null for missing font file") {
            val emoji = Emoji(font = "missing.otf")

            emoji.fontFile() shouldBe null
        }
    }

})
