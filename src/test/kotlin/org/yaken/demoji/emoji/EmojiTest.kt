package org.yaken.demoji.emoji

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.core.cache.data.EmbedData
import dev.kord.core.cache.data.EmbedFieldData
import dev.kord.core.entity.interaction.ModalSubmitInteraction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class EmojiCompanionObjectTest : FunSpec({

    context("Emoji.Companion") {
        test("fromInteraction with all fields") {
            val interaction = mockk<ModalSubmitInteraction> {
                every { textInputs["name"]?.value } returns "SampleName"
                every { textInputs["text"]?.value } returns "SampleText"
                every { textInputs["color"]?.value } returns "#FFFFFF"
                every { textInputs["bg_color"]?.value } returns "#000000"
            }
            val emoji = Emoji.fromInteraction(interaction)
            emoji.name shouldBe "SampleName"
            emoji.text shouldBe "SampleText"
            emoji.color shouldBe "#FFFFFF"
            emoji.bgColor shouldBe "#000000"
        }

        test("fromInteraction with missing optional fields") {
            val interaction = mockk<ModalSubmitInteraction> {
                every { textInputs["name"]?.value } returns "SampleName"
                every { textInputs["text"]?.value } returns "SampleText"
                every { textInputs["color"]?.value } returns null
                every { textInputs["bg_color"]?.value } returns null
            }
            val emoji = Emoji.fromInteraction(interaction)
            emoji.name shouldBe "SampleName"
            emoji.text shouldBe "SampleText"
            emoji.color shouldBe "#EC71A1"
            emoji.bgColor shouldBe "transparent"
        }

        test("fromEmbed with all fields") {
            val fields = listOf(
                EmbedFieldData("名前", "SampleName", OptionalBoolean.Value(true)),
                EmbedFieldData("テキスト", "SampleText", OptionalBoolean.Value(true)),
                EmbedFieldData("文字色", "#FFFFFF", OptionalBoolean.Value(true)),
                EmbedFieldData("背景色", "#000000", OptionalBoolean.Value(true))
            )
            val embed = EmbedData(fields = Optional.Value(fields))
            val emoji = Emoji.fromEmbed(embed)
            emoji.name shouldBe "SampleName"
            emoji.text shouldBe "SampleText"
            emoji.color shouldBe "#FFFFFF"
            emoji.bgColor shouldBe "#000000"
        }

        test("fromEmbed with missing optional fields") {
            val fields = listOf(
                EmbedFieldData("名前", "SampleName", OptionalBoolean.Value(true)),
                EmbedFieldData("テキスト", "SampleText", OptionalBoolean.Value(true)),
                EmbedFieldData("文字色", "未設定", OptionalBoolean.Value(true)),
                EmbedFieldData("背景色", "未設定", OptionalBoolean.Value(true))
            )
            val embed = EmbedData(fields = Optional.Value(fields))
            val emoji = Emoji.fromEmbed(embed)
            emoji.name shouldBe "SampleName"
            emoji.text shouldBe "SampleText"
            emoji.color shouldBe null
            emoji.bgColor shouldBe null
        }
    }
})
