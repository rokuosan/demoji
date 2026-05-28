package org.yaken.demoji.infrastructure.discord

import dev.kord.common.Color
import dev.kord.core.cache.data.EmbedData
import dev.kord.core.entity.interaction.ModalSubmitInteraction
import dev.kord.rest.builder.message.EmbedBuilder
import org.yaken.demoji.application.usecase.EmojiDraftInput
import org.yaken.demoji.domain.entity.Emoji
import java.util.UUID

private enum class EmbedFieldName(
    val ja: String,
) {
    NAME(ja = "名前"),
    TEXT(ja = "テキスト"),
    COLOR(ja = "文字色"),
    BG_COLOR(ja = "背景色"),
    FONT(ja = "フォント");

    companion object {
        fun listInJapanese() = entries.map { it.ja }
    }
}

object DiscordEmojiMapper {
    fun draftInputFromInteraction(interaction: ModalSubmitInteraction) = EmojiDraftInput(
        name = interaction.textInputs["name"]?.value,
        text = interaction.textInputs["text"]?.value,
        color = interaction.textInputs["color"]?.value,
        bgColor = interaction.textInputs["bg_color"]?.value,
    )

    fun fromEmbed(embed: EmbedData): Emoji {
        val fields = embed.fields.value ?: return Emoji()
        val nameToValue = fields.associate { it.name to if (it.value == "未設定") null else it.value }
        val id = embed.footer.value?.text ?: UUID.randomUUID().toString()

        return Emoji(
            name = nameToValue[EmbedFieldName.NAME.ja],
            text = nameToValue[EmbedFieldName.TEXT.ja],
            color = nameToValue[EmbedFieldName.COLOR.ja],
            bgColor = nameToValue[EmbedFieldName.BG_COLOR.ja],
            font = nameToValue[EmbedFieldName.FONT.ja],
            id = id,
        )
    }

    fun toEmbed(emoji: Emoji): EmbedBuilder {
        return EmbedBuilder().apply {
            this.color = emoji.color?.toKordColor()
            val values = listOf(emoji.name, emoji.text, emoji.color, emoji.bgColor, emoji.font)
            footer { this.text = emoji.id }
            EmbedFieldName.listInJapanese().zip(values).forEach { (field, value) ->
                this.field {
                    this.name = field
                    this.value = value ?: "未設定"
                    this.inline = true
                }
            }
        }
    }

    private fun String.toKordColor(): Color? {
        return replace("#", "").toIntOrNull(16)?.let { Color(it) }
    }
}
