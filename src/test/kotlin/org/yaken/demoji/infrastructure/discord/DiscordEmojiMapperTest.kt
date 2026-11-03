package org.yaken.demoji.infrastructure.discord

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.yaken.demoji.domain.entity.Emoji

class DiscordEmojiMapperTest : FunSpec({
    context("toEmbed") {
        test("converts emoji fields to embed fields") {
            val emoji = Emoji(
                id = "emoji-id",
                name = "example",
                text = "EXAMPLE",
                color = "#FF5733",
                bgColor = "#FFFFFF",
                font = "NotoSansMonoCJKjp-Bold.otf",
            )

            val embed = DiscordEmojiMapper.toEmbed(emoji)

            embed.color?.rgb shouldBe 0xFF5733
            embed.footer?.text shouldBe "emoji-id"
            embed.fields.map { it.name to it.value } shouldBe listOf(
                "名前" to "example",
                "テキスト" to "EXAMPLE",
                "文字色" to "#FF5733",
                "背景色" to "#FFFFFF",
                "フォント" to "NotoSansMonoCJKjp-Bold.otf",
            )
            embed.fields.all { it.inline == true } shouldBe true
        }

        test("uses unset text for null fields") {
            val emoji = Emoji(color = "#FF5733")

            val embed = DiscordEmojiMapper.toEmbed(emoji)

            embed.fields.map { it.name to it.value } shouldBe listOf(
                "名前" to "未設定",
                "テキスト" to "未設定",
                "文字色" to "#FF5733",
                "背景色" to "未設定",
                "フォント" to "未設定",
            )
        }
    }
})
