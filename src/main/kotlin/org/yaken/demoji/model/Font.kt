package org.yaken.demoji.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * 絵文字に使用可能なフォントを表すクラス。
 * このクラスは API との通信で使用されることを想定している。
 *
 * また、このクラスをそのままデシリアライズすると名前がそのまま出るため、
 * JSONとして無効な値になることに注意する。
 *
 * ## フォントを追加する場合
 * 1. この interface に新しいオブジェクトを追加する。
 * 2. FontTable にフォント名とフォントオブジェクトの対応を追加する。
 */
@Serializable(with = FontSerializer::class)
sealed interface Font {
    data object NotoSansMonoBold : Font
    data object MPlus1PBlack : Font
    data object RoundedXMPlus1PBlack : Font
    data object IPAMJM : Font
    data object AoyagiReisyoShimo : Font
    data object LinLibertineRBah : Font
}

object FontSerializer : KSerializer<Font> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Font", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Font) {
        val stringValue = FontTable.getFontName(value)
        encoder.encodeString(stringValue)
    }

    override fun deserialize(decoder: Decoder): Font {
        return FontTable.getFontType(decoder.decodeString())
    }
}


/**
 * フォントの情報を管理するクラス。
 * API 側でのフォント管理名と合わせている。
 */
private object FontTable {
    private data class FontInformation(
        val type: Font,
        val name: String
    )

    private val fonts: List<FontInformation> = listOf(
        FontInformation(Font.NotoSansMonoBold, "notosans-mono-bold"),
        FontInformation(Font.MPlus1PBlack, "mplus-1p-black"),
        FontInformation(Font.RoundedXMPlus1PBlack, "rounded-x-mplus-1p-black"),
        FontInformation(Font.IPAMJM, "ipamjm"),
        FontInformation(Font.AoyagiReisyoShimo, "aoyagireisyoshimo"),
        FontInformation(Font.LinLibertineRBah, "LinLibertine_RBah")
    )

    fun getFontName(font: Font): String {
        return fonts.first { it.type == font }.name
    }

    fun getFontType(name: String): Font {
        return fonts.first { it.name == name }.type
    }
}
