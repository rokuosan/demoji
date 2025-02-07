package org.yaken.demoji.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EmojiGeneratorOptionTest: FunSpec({
    val json = Json { encodeDefaults = true }

    context("EmojiGeneratorOption") {
        test("serialize with default values") {
            val option = EmojiGeneratorOption(
                align = TextAlign.LeftAlign,
                color = "FFFFFF",
                font = Font.NotoSansMonoBold,
                text = "Sample"
            )
            val jsonString = json.encodeToString(option)
            jsonString shouldBe """{"align":"left","back_color":"00000000","color":"FFFFFF","font":"notosans-mono-bold","locale":"ja","public_fg":false,"size_fixed":false,"stretch":true,"text":"Sample"}"""
        }

        test("deserialize with default values") {
            val jsonString = """{"align":"left","back_color":"00000000","color":"FFFFFF","font":"notosans-mono-bold","locale":"ja","public_fg":false,"size_fixed":false,"stretch":true,"text":"Sample"}"""
            val option = json.decodeFromString<EmojiGeneratorOption>(jsonString)
            option shouldBe EmojiGeneratorOption(
                align = TextAlign.LeftAlign,
                color = "FFFFFF",
                font = Font.NotoSansMonoBold,
                text = "Sample"
            )
        }

        test("serialize with custom values") {
            val option = EmojiGeneratorOption(
                align = TextAlign.CenterAlign,
                backColor = "FF0000",
                color = "00FF00",
                font = Font.MPlus1PBlack,
                locale = "en",
                publicFg = true,
                sizeFixed = true,
                stretch = false,
                text = "Custom"
            )
            val jsonString = json.encodeToString(option)
            jsonString shouldBe """{"align":"center","back_color":"FF0000","color":"00FF00","font":"mplus-1p-black","locale":"en","public_fg":true,"size_fixed":true,"stretch":false,"text":"Custom"}"""
        }

        test("deserialize with custom values") {
            val jsonString = """{"align":"center","back_color":"FF0000","color":"00FF00","font":"mplus-1p-black","locale":"en","public_fg":true,"size_fixed":true,"stretch":false,"text":"Custom"}"""
            val option = json.decodeFromString<EmojiGeneratorOption>(jsonString)
            option shouldBe EmojiGeneratorOption(
                align = TextAlign.CenterAlign,
                backColor = "FF0000",
                color = "00FF00",
                font = Font.MPlus1PBlack,
                locale = "en",
                publicFg = true,
                sizeFixed = true,
                stretch = false,
                text = "Custom"
            )
        }

        test("serialize with empty text") {
            val option = EmojiGeneratorOption(
                align = TextAlign.RightAlign,
                color = "000000",
                font = Font.RoundedXMPlus1PBlack,
                text = ""
            )
            val jsonString = json.encodeToString(option)
            jsonString shouldBe """{"align":"right","back_color":"00000000","color":"000000","font":"rounded-x-mplus-1p-black","locale":"ja","public_fg":false,"size_fixed":false,"stretch":true,"text":""}"""
        }

        test("deserialize with empty text") {
            val jsonString = """{"align":"right","back_color":"00000000","color":"000000","font":"rounded-x-mplus-1p-black","locale":"ja","public_fg":false,"size_fixed":false,"stretch":true,"text":""}"""
            val option = json.decodeFromString<EmojiGeneratorOption>(jsonString)
            option shouldBe EmojiGeneratorOption(
                align = TextAlign.RightAlign,
                color = "000000",
                font = Font.RoundedXMPlus1PBlack,
                text = ""
            )
        }
    }
})
