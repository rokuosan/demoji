package org.yaken.demoji.discord.event

import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.SelectMenuInteraction
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.addFile
import org.yaken.demoji.emoji.Emoji
import org.yaken.demoji.emoji.EmojiBuilder
import org.yaken.demoji.emoji.emojiBuilder
import org.yaken.demoji.util.withCleanup
import java.nio.file.Files

suspend fun handleEmojiFontSelectionEvent(interaction: SelectMenuInteraction) {
    val fields = interaction.message.data.embeds.first()
    val emoji = Emoji.fromEmbed(fields).copy(font = interaction.values.first())

    emoji.validate(strict = true)?.let { message ->
        interaction.deferEphemeralResponse().respond {
            content = message
        }
        return
    }

    val image = try {
        emojiBuilder {
            this.fontFile = emoji.fontFile()
            this.text = emoji.text!!
            this.color = emoji.colorInAwt()
            this.bgColor = emoji.bgColorInAwtOrNull()
        }
    } catch (e: Exception) {
        interaction.deferEphemeralResponse().respond {
            content = "エラーが発生しました"
        }
        println(e.stackTraceToString())
        return
    }

    // For Preview. Create a temp file to attach
    Files.createTempFile("demoji-", ".png").withCleanup(cleanup = { it ->
        Files.deleteIfExists(it)
    }) { tmp ->
        EmojiBuilder.saveImage(image, tmp.toFile())

        interaction.deferEphemeralMessageUpdate().edit {
            this.addFile(tmp)
            this.embeds = mutableListOf(emoji.embed())
            this.actionRow {
                interactionButton(ButtonStyle.Primary, "accept") {
                    this.label = "登録"
                }
                interactionButton(ButtonStyle.Danger, "cancel") {
                    this.label = "キャンセル"
                }
            }
        }
    }
}
