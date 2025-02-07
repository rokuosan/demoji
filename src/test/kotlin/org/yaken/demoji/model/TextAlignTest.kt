package org.yaken.demoji.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class TextAlignTest : FunSpec({

    val json = Json { encodeDefaults = true }

    context("LeftAlign") {
        test("serialize") {
            val align: TextAlign = TextAlign.LeftAlign
            val jsonString = json.encodeToString(align)
            jsonString shouldBe """"left""""
        }

        test("deserialize") {
            val jsonString = """"left""""
            val align = json.decodeFromString<TextAlign>(jsonString)
            align shouldBe TextAlign.LeftAlign
        }
    }

    context("CenterAlign") {
        test("serialize") {
            val align: TextAlign = TextAlign.CenterAlign
            val jsonString = json.encodeToString(align)
            jsonString shouldBe """"center""""
        }

        test("deserialize") {
            val jsonString = """"center""""
            val align = json.decodeFromString<TextAlign>(jsonString)
            align shouldBe TextAlign.CenterAlign
        }
    }

    context("RightAlign") {
        test("serialize") {
            val align: TextAlign = TextAlign.RightAlign
            val jsonString = json.encodeToString(align)
            jsonString shouldBe """"right""""
        }

        test("deserialize") {
            val jsonString = """"right""""
            val align = json.decodeFromString<TextAlign>(jsonString)
            align shouldBe TextAlign.RightAlign
        }
    }

    test("deserialize with invalid value") {
        val jsonString = """"invalid""""
        val e = shouldThrow<IllegalArgumentException> {
            json.decodeFromString<TextAlign>(jsonString)
        }
        e.message shouldBe "Unknown TextAlign: invalid"
    }
})
