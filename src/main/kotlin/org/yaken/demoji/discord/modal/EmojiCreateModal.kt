package org.yaken.demoji.discord.modal

import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.GuildModalSubmitInteraction
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.component.option
import dev.kord.rest.builder.message.actionRow
import org.yaken.demoji.emoji.Emoji

suspend fun GuildChatInputCommandInteractionCreateEvent.openEmojiCreateModal(
    emoji: Emoji = Emoji(null, "よさ\nそう", "#EC71A1", "transparent")
) = interaction.modal("絵文字ジェネレータ", "emoji_generator") {
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
