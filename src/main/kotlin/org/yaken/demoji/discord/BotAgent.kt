package org.yaken.demoji.discord

import dev.kord.core.Kord
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import org.slf4j.LoggerFactory
import org.yaken.demoji.config.DiscordConfig
import org.yaken.demoji.discord.event.handleEmojiCreateModalSubmit
import org.yaken.demoji.discord.event.handleEmojiFontSelectionEvent
import org.yaken.demoji.discord.event.onCancelButtonClicked
import org.yaken.demoji.discord.event.onEmojiConfirmButtonClicked
import org.yaken.demoji.discord.modal.openEmojiCreateModal

class BotAgent {
    private lateinit var kord: Kord
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun start() {
        kord = Kord(DiscordConfig.BotToken)
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
        DiscordConfig.GuildIDs.forEach {
            kord.createGuildChatInputCommand(it, "emo", "絵文字を生成します")
        }
    }

    private fun configureEvents() {
        // スラッシュコマンド
        kord.on<GuildChatInputCommandInteractionCreateEvent>{
            when(interaction.invokedCommandName) {
                "emo" -> openEmojiCreateModal()
            }
        }

        // モーダルからデータが送信された時に発火するイベント
        kord.on<GuildModalSubmitInteractionCreateEvent> {
            when(interaction.modalId) {
                "emoji_generator" -> handleEmojiCreateModalSubmit(interaction)
            }
        }

        // セレクトメニューに変更があった時に発火するイベント
        kord.on<SelectMenuInteractionCreateEvent> {
            when(interaction.componentId) {
                "font" -> handleEmojiFontSelectionEvent(interaction)
            }
        }

        // ボタンが押された時に発火するイベント
        kord.on<ButtonInteractionCreateEvent> {
            when(interaction.componentId) {
                "accept" -> onEmojiConfirmButtonClicked(interaction)
                "cancel" -> onCancelButtonClicked(interaction)
            }
        }
    }
}
