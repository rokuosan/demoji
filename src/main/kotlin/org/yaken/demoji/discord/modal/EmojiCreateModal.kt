package org.yaken.demoji.discord.modal

import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.GuildModalSubmitInteraction
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.component.option
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

suspend fun GuildChatInputCommandInteractionCreateEvent.openEmojiCreateModal() = interaction
    .modal("絵文字ジェネレータ", "emoji_generator") {
        this.actionRow {
            this.textInput(TextInputStyle.Short, "name", "名前") {
                this.placeholder = "呼び出す時の名前を書いてください"
                this.required = true
            }
        }
        this.actionRow {
            this.textInput(TextInputStyle.Paragraph, "text", "テキスト") {
                this.placeholder = "絵文字にしたいテキストを入力してください"
                this.required = true
                this.value = "よさそう"
            }
        }
        this.actionRow {
            this.textInput(TextInputStyle.Short, "color", "文字色") {
                this.placeholder = "#EC71A1"
                this.required = false
                this.value = "#EC71A1"
            }
        }
        this.actionRow {
            this.textInput(TextInputStyle.Short, "bg_color", "背景色") {
                this.placeholder = "#FFFFFF"
                this.required = false
                this.value = "transparent"
            }
        }
    }

suspend fun handleEmojiCreateModalSubmit(interaction: GuildModalSubmitInteraction) {
    val name = interaction.textInputs["name"]?.value ?: return
    val text = interaction.textInputs["text"]?.value ?: return
    val color = interaction.textInputs["color"]?.value ?: "#EC71A1"
    val bgColor = interaction.textInputs["bg_color"]?.value ?: "transparent"

    val hexColor = color.replace("#", "").toIntOrNull(16) ?: run {
        interaction.deferEphemeralResponse().respond {
            content = "文字色が不正です"
        }
        return
    }

    // フォントを尋ねる処理
    interaction.deferEphemeralResponse().respond {
        this.content = "フォントを選択してください"
        this.embed {
            this.color = dev.kord.common.Color(hexColor)
            this.field {
                this.name = "Name"
                this.value = name
                this.inline = true
            }
            this.field {
                this.name = "Text"
                this.value = text
                this.inline = true
            }
            this.field {
                this.name = "Color"
                this.value = color
                this.inline = true
            }
            this.field {
                this.name = "Background Color"
                this.value = bgColor
                this.inline = true
            }
        }
        this.actionRow {
            this.stringSelect("font") {
                this.placeholder = "フォントを選択してください"
                this.option("Noto Sans Mono Bold", "notosans-mono-bold")
//                this.option("M+ 1p Black", "mplus-1p-black")
            }
        }
    }
}

fun BufferedImage.toByteArray(): ByteArray {
    val output = ByteArrayOutputStream()
    ImageIO.write(this, "png", output)
    return output.toByteArray()
}
