package org.yaken.demoji.discord.event

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.GuildModalSubmitInteraction
import dev.kord.rest.builder.component.option
import dev.kord.rest.builder.message.actionRow
import org.yaken.demoji.emoji.Emoji

suspend fun handleEmojiCreateModalSubmit(interaction: GuildModalSubmitInteraction) {
    val emoji = Emoji.fromInteraction(interaction)

    emoji.validate()?.let { message ->
        interaction.deferEphemeralResponse().respond {
            content = message
        }
        return
    }

    // フォントを尋ねる処理
    interaction.deferEphemeralResponse().respond {
        this.content = "フォントを選択してください"
        this.embeds = mutableListOf(emoji.embed())
        this.actionRow {
            this.stringSelect("font") {
                this.placeholder = "フォントを選択してください"
                this.option("Noto Sans Mono Bold", "notosans-mono-bold")
            }
        }
    }
}
