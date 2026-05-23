package org.yaken.demoji.infrastructure.discord

import dev.kord.core.Kord
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.opentelemetry.api.trace.Tracer
import org.slf4j.Logger
import org.yaken.demoji.application.usecase.EmojiFontUseCase
import org.yaken.demoji.domain.service.EmojiGeneratorService
import org.yaken.demoji.infrastructure.config.DiscordConfig
import org.yaken.demoji.infrastructure.otel.inSpan

class DiscordBotAdapter(
    private val logger: Logger,
    private val config: DiscordConfig,
    private val tracer: Tracer,
    private val generator: EmojiGeneratorService,
    private val emojiFontUseCase: EmojiFontUseCase,
) {
    private val handler = Handler(
        tracer = tracer,
        generator = generator,
        emojiFontUseCase = emojiFontUseCase,
    )
    private lateinit var bot: Kord

    suspend fun start() = with(Kord(config.BotToken)) {
        bot = this
        on<GuildChatInputCommandInteractionCreateEvent> {
            tracer.inSpan("discord.command.interaction") {
                setAttribute("discord.command.name", interaction.invokedCommandName)
                setAttribute("discord.interaction.type", interaction.type.toString())
                when (interaction.invokedCommandName) {
                    "emo" -> tracer.inSpan("discord.command.emo") {
                        handler.handleEmoCommand(interaction)
                    }
                }
            }
        }
        on<GuildModalSubmitInteractionCreateEvent> {
            tracer.inSpan("discord.modal_submit.interaction") {
                setAttribute("discord.modal.id", interaction.modalId)
                setAttribute("discord.interaction.type", interaction.type.toString())
                when (interaction.modalId) {
                    "emoji_generator" -> tracer.inSpan("discord.modal_submit.emoji_generator") {
                        handler.handleEmojiCreateModalSubmit(interaction)
                    }
                }
            }
        }
        on<SelectMenuInteractionCreateEvent> {
            tracer.inSpan("discord.select_menu.interaction") {
                setAttribute("discord.component.id", interaction.componentId)
                setAttribute("discord.interaction.type", interaction.type.toString())
                when (interaction.componentId) {
                    "font" -> tracer.inSpan("discord.select_menu.font") {
                        handler.handleEmojiFontSelectionEvent(interaction)
                    }
                }
            }
        }
        on<ButtonInteractionCreateEvent> {
            tracer.inSpan("discord.button.interaction") {
                setAttribute("discord.component.id", interaction.componentId)
                setAttribute("discord.interaction.type", interaction.type.toString())
                when (interaction.componentId) {
                    "accept" -> tracer.inSpan("discord.button.accept") {
                        handler.handleConfirmButtonClickAction(interaction)
                    }

                    "cancel" -> tracer.inSpan("discord.button.cancel") {
                        handler.handleCancelButtonClickAction(interaction)
                    }
                }
            }
        }

        config.GuildIDs.forEach {
            createGuildChatInputCommand(it, "emo", "絵文字を生成します")
        }

        login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
            intents += Intent.Guilds
            logger.info("Bot is running...")
        }
    }
}
