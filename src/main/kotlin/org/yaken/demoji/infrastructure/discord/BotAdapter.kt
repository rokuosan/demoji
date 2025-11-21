package org.yaken.demoji.infrastructure.discord

import dev.kord.core.Kord
import dev.kord.core.event.interaction.ActionInteractionCreateEvent
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.opentelemetry.api.common.Attributes
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

    fun getRequestAttributes(event: ActionInteractionCreateEvent): Attributes {
        return Attributes.builder().apply {
            put("interaction.id", event.interaction.id.toString())
            put("interaction.type", event.interaction.type.toString())
            put("user.id", event.interaction.user.id.toString())
        }.build()
    }

    suspend fun start() = with(Kord(config.BotToken)) {
        bot = this
        on<GuildChatInputCommandInteractionCreateEvent> {
            tracer.withSpan("onGuildChatInputCommandInteractionCreateEvent") {
                it.setAllAttributes(getRequestAttributes(this))
                when (interaction.invokedCommandName) {
                    "emo" -> withSpan("handleEmoCommand") {
                        handler.handleEmoCommand(interaction)
                    }
                }
            }
        }
        on<GuildModalSubmitInteractionCreateEvent> {
            tracer.withSpan("onGuildModalSubmitInteractionCreateEvent") {
                it.setAllAttributes(getRequestAttributes(this))
                when (interaction.modalId) {
                    "emoji_generator" -> withSpan("handleEmojiCreateModalSubmit") {
                        handler.handleEmojiCreateModalSubmit(interaction)
                    }
                }
            }
        }
        on<SelectMenuInteractionCreateEvent> {
            tracer.withSpan("onSelectMenuInteractionCreateEvent") {
                it.setAllAttributes(getRequestAttributes(this))
                when (interaction.componentId) {
                    "font" -> withSpan("handleEmojiFontSelectionEvent") {
                        handler.handleEmojiFontSelectionEvent(interaction)
                    }
                }
            }
        }
        on<ButtonInteractionCreateEvent> {
            tracer.withSpan("onButtonInteractionCreateEvent") {
                it.setAllAttributes(getRequestAttributes(this))
                when (interaction.componentId) {
                    "accept" -> withSpan("handleConfirmButtonClickAction") {
                        handler.handleConfirmButtonClickAction(interaction)
                    }

                    "cancel" -> withSpan("handleCancelButtonClickAction") {
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

