package org.yaken.demoji.infrastructure.discord

import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.PopupInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.entity.interaction.GuildModalSubmitInteraction
import dev.kord.core.entity.interaction.SelectMenuInteraction
import dev.kord.rest.builder.component.option
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.addFile
import org.yaken.demoji.application.usecase.EmojiFontUseCase
import org.yaken.demoji.common.Result
import org.yaken.demoji.domain.entity.Emoji
import org.yaken.demoji.domain.service.EmojiGeneratorService
import org.yaken.demoji.infrastructure.discord.modal.createEmojiModal
import io.opentelemetry.api.trace.Tracer
import org.yaken.demoji.infrastructure.otel.inSpan
import org.yaken.demoji.util.createEmoji
import java.nio.file.Files

class Handler(
    private val tracer: Tracer,
    private val generator: EmojiGeneratorService,
    private val emojiFontUseCase: EmojiFontUseCase,
) {
    /**
     * /emo コマンドが実行された時のハンドラー。
     * 絵文字作成モーダルを表示する。
     *
     * @param interaction コマンドインタラクション
     * @return モーダル表示のレスポンス
     */
    suspend fun handleEmoCommand(interaction: GuildChatInputCommandInteraction): PopupInteractionResponseBehavior {
        return interaction.modal(
            title = "絵文字ジェネレータ",
            customId = "emoji_generator",
            builder = createEmojiModal(),
        )
    }

    /**
     * 絵文字作成モーダルで送信ボタンが押された時のハンドラー。
     * 内容の検証を行い、問題がなければそのままフォントを選択させる。
     *
     * @param interaction モーダルの送信インタラクション
     */
    suspend fun handleEmojiCreateModalSubmit(interaction: GuildModalSubmitInteraction) =
        tracer.inSpan("discord.modal_submit.handle_create_emoji") {
            val emoji = Emoji.fromInteraction(interaction)

            val result = emoji.validate()
            if (result is Result.Err) {
                interaction.deferEphemeralResponse().respond {
                    content = result.error
                }
                setAttribute("validation.failed", true)
                return@inSpan
            }

            val fonts = emojiFontUseCase.getAvailableFonts()

            interaction.deferEphemeralResponse().respond {
                this.content = "フォントを選択してください"
                this.embeds = mutableListOf(emoji.toEmbed())
                this.actionRow {
                    this.stringSelect("font") {
                        this.placeholder = "フォントを選択してください"
                        for (font in fonts) {
                            this.option(font.name, font.filename)
                        }
                    }
                }
            }
        }

    /**
     * フォント選択セレクトメニューが操作された時のハンドラー。
     * 選択されたフォントで絵文字プレビューを生成し、メッセージを更新する。
     *
     * @param interaction セレクトメニューのインタラクション
     */
    suspend fun handleEmojiFontSelectionEvent(interaction: SelectMenuInteraction) {
        val fields = interaction.message.data.embeds.first()
        val emoji = Emoji.fromEmbed(fields).copy(font = interaction.values.first())
        with(emoji.validate()) {
            if (this is Result.Err) {
                interaction.deferEphemeralResponse().respond {
                    content = this@with.error
                }
                return
            }
        }

        val result = generator.generateImageToTempFile(emoji)
        when (result) {
            is Result.Err -> {
                interaction.deferEphemeralResponse().respond {
                    content = "プレビュー用絵文字の作成中にエラーが発生しました"
                }
                result.error.printStackTrace()
                return
            }

            is Result.Ok -> {
                try {
                    interaction.deferEphemeralMessageUpdate().edit {
                        this.addFile(result.value)
                        this.embeds = mutableListOf(emoji.toEmbed())
                        this.actionRow {
                            interactionButton(ButtonStyle.Primary, "accept") {
                                this.label = "登録"
                            }
                            interactionButton(ButtonStyle.Danger, "cancel") {
                                this.label = "キャンセル"
                            }
                        }
                    }
                } finally {
                    Files.deleteIfExists(result.value)
                }
            }
        }
    }

    /**
     * 絵文字登録ボタンが押された時に一時メッセージを削除するだけのハンドラー。
     * 汎用的に使用する。
     *
     * @param interaction ボタンのインタラクション
     */
    suspend fun handleCancelButtonClickAction(interaction: ButtonInteraction) {
        interaction.deferEphemeralMessageUpdate().delete()
    }

    suspend fun handleConfirmButtonClickAction(interaction: ButtonInteraction) {
        val field = interaction.message.data.embeds.first()
        val emoji = Emoji.fromEmbed(field)
        with(emoji.validate()) {
            if (this is Result.Err) {
                interaction.deferEphemeralResponse().respond {
                    content = this@with.error
                }
                return
            }
        }

        val guild = interaction.message.getGuild()
        val result = generator.generateImageFromEmoji(emoji)
        when (result) {
            is Result.Err -> {
                interaction.deferEphemeralResponse().respond {
                    content = "エラーが発生しました"
                }
                result.error.printStackTrace()
                return
            }

            is Result.Ok -> {
                val guildEmoji = guild.createEmoji(
                    name = emoji.name!!,
                    image = result.value,
                )
                interaction.deferEphemeralMessageUpdate().edit {
                    content = "絵文字を登録しました: ${guildEmoji.mention}"
                    embeds = mutableListOf()
                    attachments = mutableListOf()
                    components = mutableListOf()
                }
            }
        }
    }
}
