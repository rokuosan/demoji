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
import org.yaken.demoji.infrastructure.otel.withSpan

class DiscordBotAdapter(
    private val logger: Logger,
    private val config: DiscordConfig,
    private val tracer: Tracer,
    private val generator: EmojiGeneratorService,
    private val emojiFontUseCase: EmojiFontUseCase,
) {
    private val handler = Handler(
        generator = generator,
        emojiFontUseCase = emojiFontUseCase,
    )
    private lateinit var bot: Kord

    suspend fun start() = with(Kord(config.BotToken)) {
        bot = this
        on<GuildChatInputCommandInteractionCreateEvent> {
            tracer.withSpan("onGuildChatInputCommandInteractionCreateEvent") {
                it.makeCurrent().use {
                    when (interaction.invokedCommandName) {
                        "emo" -> {
                            handler.handleEmoCommand(interaction)
                        }
                    }
                }
            }
        }
        on<GuildModalSubmitInteractionCreateEvent> {
            when (interaction.modalId) {
                "emoji_generator" -> handler.handleEmojiCreateModalSubmit(interaction)
            }
        }
        on<SelectMenuInteractionCreateEvent> {
            when (interaction.componentId) {
                "font" -> handler.handleEmojiFontSelectionEvent(interaction)
            }
        }
        on<ButtonInteractionCreateEvent> {
            when (interaction.componentId) {
                "accept" -> handler.handleConfirmButtonClickAction(interaction)
                "cancel" -> handler.handleCancelButtonClickAction(interaction)
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

