package org.yaken.demoji.discord

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.createEmoji
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.application.GlobalChatInputCommand
import dev.kord.core.entity.interaction.GlobalChatInputCommandInteraction
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GlobalChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.Image
import dev.kord.rest.builder.message.AttachmentBuilder
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.addFile
import dev.kord.rest.builder.message.embed
import org.yaken.demoji.config.DiscordConfig
import org.yaken.demoji.discord.modal.openEmojiCreateModal
import org.slf4j.LoggerFactory
import org.yaken.demoji.discord.modal.handleEmojiCreateModalSubmit
import org.yaken.demoji.discord.modal.toByteArray
import org.yaken.demoji.emoji.EmojiBuilder
import org.yaken.demoji.emoji.emojiBuilder
import java.awt.Color
import kotlin.io.path.Path

class BotAgent {
    private lateinit var kord: Kord
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun start() {
        kord = Kord(DiscordConfig.discordBotToken)
        logger.info("Starting bot...")

        configureEvents()
        configureEmoCommand()
        login()
    }

    private suspend fun login() {
        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
            intents += Intent.Guilds
            logger.info("Logging in...")
        }
    }

    private suspend fun configureEmoCommand() {
        kord.createGuildChatInputCommand(DiscordConfig.discordGuildID, "emo","絵文字を生成します")
    }

    private fun configureEvents() {
        kord.on<GuildChatInputCommandInteractionCreateEvent>{
            when(interaction.invokedCommandName) {
                "emo" -> openEmojiCreateModal()
            }
        }

        kord.on<GuildModalSubmitInteractionCreateEvent> {
            when(interaction.modalId) {
                "emoji_generator" -> handleEmojiCreateModalSubmit(interaction)
            }
        }

        kord.on<SelectMenuInteractionCreateEvent> {
            when(interaction.componentId) {
                "font" -> {
                    val fields = interaction.message.data.embeds[0].fields.value!!
                    val font = interaction.values.first()
                    val name = fields[0].value
                    val text = fields[1].value
                    val color = fields[2].value
                    val bgColor = fields[3].value

                    val emoji = try {
                        emojiBuilder {
                            this.fontFile = java.io.File(font)
                            this.text = text
                            this.color = Color.decode(color)
                            this.bgColor = if (bgColor == "transparent") null else Color.decode(bgColor)
                        }
                    } catch (e: Exception) {
                        interaction.deferEphemeralResponse().respond {
                            content = "エラーが発生しました"
                        }
                        println(e.stackTraceToString())
                        return@on
                    }
                    // この規模でミリ秒単位で一致することはないのでこれで良い
                    val now = System.currentTimeMillis()
                    EmojiBuilder.saveImage(emoji, java.io.File("$now.png"))

                    interaction.deferEphemeralMessageUpdate().edit {
                        this.addFile(Path("$now.png"))
                        this.embed {
                            fields.forEach {
                                this.field {
                                    this.name = it.name
                                    this.value = it.value
                                    this.inline = it.inline.value
                                }
                            }
                            this.field {
                                this.name = "Font"
                                this.value = font
                                this.inline = true
                            }
                        }
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
            }
        }
        kord.on<ButtonInteractionCreateEvent> {
            when(interaction.componentId) {
                "accept" -> {
                    val fields = interaction.message.data.embeds[0].fields.value!!
                    val name = fields[0].value
                    val text = fields[1].value
                    val color = fields[2].value
                    val bgColor = fields[3].value
                    val font = fields[4].value

                    println("name: $name, text: $text, color: $color, bgColor: $bgColor, font: $font")

                    val emoji = try {
                        emojiBuilder {
                            this.fontFile = java.io.File(font)
                            this.text = text
                            this.color = Color.decode(color)
                            this.bgColor = if (bgColor == "transparent") null else Color.decode(bgColor)
                        }
                    } catch (e: Exception) {
                        interaction.deferEphemeralResponse().respond {
                            content = "エラーが発生しました"
                        }
                        println(e.stackTraceToString())
                        return@on
                    }

                    // ギルド情報を取得
                    val guild = interaction.message.getGuildOrNull() ?: run {
                        interaction.deferEphemeralResponse().respond {
                            content = "ギルド情報が取得できませんでした"
                        }
                        return@on
                    }

                    // 絵文字を登録
                    val image = Image.raw(emoji.toByteArray(), Image.Format.PNG)
                    val guildEmoji = guild.createEmoji(name, image)
                    val res = interaction.deferEphemeralMessageUpdate()
                    res.edit {
                        content = "絵文字を登録しました: ${guildEmoji.mention}"
                        embeds = mutableListOf()
                        attachments = mutableListOf()
                        components = mutableListOf()
                    }
                }
                "cancel" -> interaction.deferEphemeralMessageUpdate().delete()

            }
        }
    }
}
