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
import kotlin.io.path.Path

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

    // この規模でミリ秒単位で一致することはないのでこれで良い
    val now = System.currentTimeMillis()
    EmojiBuilder.saveImage(image, java.io.File("$now.png"))

    interaction.deferEphemeralMessageUpdate().edit {
        this.addFile(Path("$now.png"))
        this.embeds = mutableListOf(emoji.embed())
        this.actionRow {
            interactionButton(ButtonStyle.Primary, "accept"){
                this.label = "登録"
            }
            interactionButton(ButtonStyle.Danger, "cancel"){
                this.label = "キャンセル"
            }
        }
    }
}
