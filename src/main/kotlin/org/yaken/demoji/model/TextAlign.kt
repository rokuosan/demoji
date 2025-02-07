package org.yaken.demoji.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TextAlignSerializer::class)
sealed interface TextAlign {
    data object LeftAlign : TextAlign
    data object CenterAlign : TextAlign
    data object RightAlign : TextAlign
}

object TextAlignSerializer : KSerializer<TextAlign> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("TextAlign", PrimitiveKind.STRING)


    override fun serialize(encoder: Encoder, value: TextAlign) {
        val stringValue = when (value) {
            TextAlign.LeftAlign -> "left"
            TextAlign.CenterAlign -> "center"
            TextAlign.RightAlign -> "right"
        }
        encoder.encodeString(stringValue)
    }

    override fun deserialize(decoder: Decoder): TextAlign {
        return when (val stringValue = decoder.decodeString()) {
            "left" -> TextAlign.LeftAlign
            "center" -> TextAlign.CenterAlign
            "right" -> TextAlign.RightAlign
            else -> throw IllegalArgumentException("Unknown TextAlign: $stringValue")
        }
    }
}
