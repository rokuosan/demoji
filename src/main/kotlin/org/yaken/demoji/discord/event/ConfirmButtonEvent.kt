package org.yaken.demoji.discord.event

import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.ButtonInteraction
import org.yaken.demoji.emoji.Emoji
import org.yaken.demoji.util.createEmoji

suspend fun onEmojiConfirmButtonClicked(interaction: ButtonInteraction) {
    val field = interaction.message.data.embeds.first()
    val emoji = Emoji.fromEmbed(field)
    emoji.validate(strict = true)?.let { message ->
        interaction.deferEphemeralResponse().respond {
            content = message
        }
        return
    }

    val guild = interaction.message.getGuild()
    val image = try {
        emoji.image()
    } catch (e: Exception) {
        interaction.deferEphemeralResponse().respond {
            content = "エラーが発生しました"
        }
        println(e.stackTraceToString())
        return
    }

    val guildEmoji = guild.createEmoji(emoji.name!!, image)
    interaction.deferEphemeralMessageUpdate().edit {
        content = "絵文字を登録しました: ${guildEmoji.mention}"
        embeds = mutableListOf()
        attachments = mutableListOf()
        components = mutableListOf()
    }
}
