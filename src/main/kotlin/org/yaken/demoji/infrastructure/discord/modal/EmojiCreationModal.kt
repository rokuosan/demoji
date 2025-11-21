package org.yaken.demoji.infrastructure.discord.modal

import dev.kord.common.entity.TextInputStyle
import dev.kord.rest.builder.interaction.ModalBuilder
import org.yaken.demoji.domain.entity.Emoji

fun createEmojiModal(
    emoji: Emoji = Emoji(text = "よさ\nそう", color= "#EC71A1", bgColor = "transparent")
): ModalBuilder.() -> Unit {
    return {
        this.actionRow {
            this.textInput(TextInputStyle.Short, "name", "名前") {
                this.placeholder = "コロンなしで名前を入力（例: yosasou)"
                this.required = true
                this.value = emoji.name
                this.allowedLength = 2..32
            }
        }
        this.actionRow {
            this.textInput(TextInputStyle.Paragraph, "text", "テキスト") {
                this.placeholder = "絵文字にしたいテキストを入力してください"
                this.required = true
                this.value = emoji.text
            }
        }
        this.actionRow {
            this.textInput(TextInputStyle.Short, "color", "文字色") {
                this.placeholder = "#EC71A1"
                this.required = false
                this.value = emoji.color
            }
        }
        this.actionRow {
            this.textInput(TextInputStyle.Short, "bg_color", "背景色") {
                this.placeholder = "#FFFFFF"
                this.required = false
                this.value = emoji.bgColor
            }
        }
    }
}
