package org.yaken.demoji.domain.entity

import dev.kord.core.cache.data.EmbedData
import dev.kord.core.entity.interaction.ModalSubmitInteraction
import dev.kord.rest.builder.message.EmbedBuilder
import org.yaken.demoji.common.Result
import org.yaken.demoji.common.err
import org.yaken.demoji.common.ok
import java.io.File
import java.util.*

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
    val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val text: String? = null,
    val color: String? = null,
    val bgColor: String? = null,
    val font: String? = null,
) {
    private fun hexColor(c: String?): Int? = c?.replace("#", "")?.toIntOrNull(16)
    private fun awtColor(c: String?): java.awt.Color? = hexColor(c)?.let { java.awt.Color(it) }
    private fun kordColor(c: String?): dev.kord.common.Color? = hexColor(c)?.let { dev.kord.common.Color(it) }
    fun colorInAWT() = java.awt.Color(hexColor(color) ?: 0)
    fun bgColorInAwtOrNull() = awtColor(bgColor)

    fun fontFile(): File? = font?.let {
        val tt = File("fonts", it)
        if (tt.exists()) tt else null
    }

    fun validate(strict: Boolean = false): Result<Unit, String> {
        if (strict && name == null)
            return err("名前は必須です")

        // 基本的にフォームで見ているため、ここでエラーになることはないが検証しておく
        if (name != null && name.length !in 2..32)
            return err("名前は2文字以上32文字以下である必要があります")

        // カラーコードの検証
        if (hexColor(color) == null)
            return err("文字色が不正です")

        return ok(Unit)
    }

    fun toEmbed(): EmbedBuilder {
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



    companion object {
        fun fromInteraction(interaction: ModalSubmitInteraction) = Emoji(
            name = interaction.textInputs["name"]?.value,
            text = interaction.textInputs["text"]?.value,
            color = interaction.textInputs["color"]?.value ?: "#EC71A1",
            bgColor = interaction.textInputs["bg_color"]?.value ?: "transparent"
        )

        fun fromEmbed(embed: EmbedData): Emoji {
            val fields = embed.fields.value ?: return Emoji()
            val nameToValue = fields.associate { it.name to if (it.value == "未設定") null else it.value }
            val id = embed.footer.value?.text ?: UUID.randomUUID().toString()

            return Emoji(
                name = nameToValue[EmbedFiledName.NAME.ja],
                text = nameToValue[EmbedFiledName.TEXT.ja],
                color = nameToValue[EmbedFiledName.COLOR.ja],
                bgColor = nameToValue[EmbedFiledName.BG_COLOR.ja],
                id = id
            )
        }
    }
}
