package org.yaken.demoji.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FontTest : FunSpec({
    val json = Json { encodeDefaults = true }

    context("NotoSansMonoBold") {
        test("serialize") {
            val font: Font = Font.NotoSansMonoBold
            val jsonString = json.encodeToString(font)
            jsonString shouldBe """"notosans-mono-bold""""
        }

        test("deserialize") {
            val jsonString = """"notosans-mono-bold""""
            val font = json.decodeFromString<Font>(jsonString)
            font shouldBe Font.NotoSansMonoBold
        }
    }

    context("MPlus1PBlack") {
        test("serialize") {
            val font: Font = Font.MPlus1PBlack
            val jsonString = json.encodeToString(font)
            jsonString shouldBe """"mplus-1p-black""""
        }

        test("deserialize") {
            val jsonString = """"mplus-1p-black""""
            val font = json.decodeFromString<Font>(jsonString)
            font shouldBe Font.MPlus1PBlack
        }
    }

    context("RoundedXMPlus1PBlack") {
        test("serialize") {
            val font: Font = Font.RoundedXMPlus1PBlack
            val jsonString = json.encodeToString(font)
            jsonString shouldBe """"rounded-x-mplus-1p-black""""
        }

        test("deserialize") {
            val jsonString = """"rounded-x-mplus-1p-black""""
            val font = json.decodeFromString<Font>(jsonString)
            font shouldBe Font.RoundedXMPlus1PBlack
        }
    }

    context("IPAMJM") {
        test("serialize") {
            val font: Font = Font.IPAMJM
            val jsonString = json.encodeToString(font)
            jsonString shouldBe """"ipamjm""""
        }

        test("deserialize") {
            val jsonString = """"ipamjm""""
            val font = json.decodeFromString<Font>(jsonString)
            font shouldBe Font.IPAMJM
        }
    }

    context("AoyagiReisyoShimo") {
        test("serialize") {
            val font: Font = Font.AoyagiReisyoShimo
            val jsonString = json.encodeToString(font)
            jsonString shouldBe """"aoyagireisyoshimo""""
        }

        test("deserialize") {
            val jsonString = """"aoyagireisyoshimo""""
            val font = json.decodeFromString<Font>(jsonString)
            font shouldBe Font.AoyagiReisyoShimo
        }
    }

    context("LinLibertineRBah") {
        test("serialize") {
            val font: Font = Font.LinLibertineRBah
            val jsonString = json.encodeToString(font)
            jsonString shouldBe """"LinLibertine_RBah""""
        }

        test("deserialize") {
            val jsonString = """"LinLibertine_RBah""""
            val font = json.decodeFromString<Font>(jsonString)
            font shouldBe Font.LinLibertineRBah
        }
    }

    test("知らないフォントはエラーを返す") {
        val jsonString = """"unknown""""
        shouldThrow<NoSuchElementException> {
            json.decodeFromString<Font>(jsonString)
        }
    }
})
