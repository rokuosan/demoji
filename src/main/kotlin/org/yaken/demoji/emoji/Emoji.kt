package org.yaken.demoji.emoji

import dev.kord.core.cache.data.EmbedData
import dev.kord.core.cache.data.EmbedFieldData
import dev.kord.core.entity.Embed
import dev.kord.core.entity.interaction.ModalSubmitInteraction
import dev.kord.rest.builder.message.EmbedBuilder
import java.io.File
import java.util.UUID


private enum class EmbedFiledName(
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

data class Emoji(
    val name: String? = null,
    val text: String? = null,
    val color: String? = null,
    val bgColor: String? = null,
    val font: String? = null,
    val id: String = UUID.randomUUID().toString()
) {
    private fun hexColor(c: String?): Int? = c?.replace("#", "")?.toIntOrNull(16)
    private fun awtColor(c: String?): java.awt.Color? = hexColor(c)?.let { java.awt.Color(it) }
    private fun kordColor(c: String?): dev.kord.common.Color? = hexColor(c)?.let { dev.kord.common.Color(it) }
    fun colorInAwt() = java.awt.Color(hexColor(color) ?: 0)
    fun bgColorInAwtOrNull() = awtColor(bgColor)

    fun fontFile() = font?.let { File(it) }

    fun validate(strict: Boolean = false): String? {
        if (strict && name == null) return "名前は必須です"

        // 基本的にフォームで見ているため、ここでエラーになることはないが検証しておく
        if (name != null && name.length !in 2..32) return "名前は2文字以上32文字以下である必要があります"

        // カラーコードの検証
        if (hexColor(color) == null) return "文字色が不正です"

        return null
    }


    fun embed(): EmbedBuilder {
        return EmbedBuilder().apply {
            this.color = kordColor(this@Emoji.color)
            val values  = listOf(name, text, this@Emoji.color, bgColor)
            footer { this.text = id }
            EmbedFiledName.listInJapanese().zip(values).forEach { (field, value) ->
                this.field {
                    this.name = field
                    this.value = value?:"未設定"
                    this.inline = true
                }
            }
        }
    }

    fun image() = emojiBuilder {
        this.fontFile = fontFile()
        this.text = this@Emoji.text!!
        this.color = colorInAwt()
        this.bgColor = bgColorInAwtOrNull()
    }

    companion object {
        fun fromInteraction(interaction: ModalSubmitInteraction) = Emoji(
            interaction.textInputs["name"]?.value,
            interaction.textInputs["text"]?.value,
            interaction.textInputs["color"]?.value ?: "#EC71A1",
            interaction.textInputs["bg_color"]?.value ?: "transparent"
        )

        fun fromEmbed(embed: EmbedData): Emoji {
            val fields = embed.fields.value ?: return Emoji()
            val nameToValue = fields.associate { it.name to if (it.value == "未設定") null else it.value }
            val id = embed.footer.value?.text?: UUID.randomUUID().toString()

            return Emoji(
                nameToValue[EmbedFiledName.NAME.ja],
                nameToValue[EmbedFiledName.TEXT.ja],
                nameToValue[EmbedFiledName.COLOR.ja],
                nameToValue[EmbedFiledName.BG_COLOR.ja],
                id = id
            )
        }
    }
}
