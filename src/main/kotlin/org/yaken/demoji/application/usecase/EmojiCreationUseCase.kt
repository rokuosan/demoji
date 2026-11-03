package org.yaken.demoji.application.usecase

import org.yaken.demoji.common.Result
import org.yaken.demoji.common.err
import org.yaken.demoji.common.ok
import org.yaken.demoji.domain.entity.Emoji
import org.yaken.demoji.domain.entity.EmojiFont
import org.yaken.demoji.domain.service.EmojiGeneratorService
import java.awt.image.BufferedImage
import java.nio.file.Path

data class EmojiDraftInput(
    val name: String?,
    val text: String?,
    val color: String?,
    val bgColor: String?,
)

interface EmojiCreationUseCase {
    fun createDraft(input: EmojiDraftInput): Result<Emoji, String>
    fun getAvailableFonts(): List<EmojiFont>
    suspend fun generatePreviewFile(emoji: Emoji): Result<Path, Error>
    suspend fun generateImage(emoji: Emoji): Result<BufferedImage, Error>
}

class EmojiCreationUseCaseImpl(
    private val generator: EmojiGeneratorService,
    private val emojiFontUseCase: EmojiFontUseCase,
) : EmojiCreationUseCase {
    override fun createDraft(input: EmojiDraftInput): Result<Emoji, String> {
        val emoji = Emoji(
            name = input.name,
            text = input.text,
            color = input.color ?: "#EC71A1",
            bgColor = input.bgColor ?: "transparent",
        )

        val result = emoji.validate()
        if (result is Result.Err) {
            return err(result.error)
        }

        return ok(emoji)
    }

    override fun getAvailableFonts(): List<EmojiFont> {
        return emojiFontUseCase.getAvailableFonts()
    }

    override suspend fun generatePreviewFile(emoji: Emoji): Result<Path, Error> {
        return generator.generateImageToTempFile(emoji)
    }

    override suspend fun generateImage(emoji: Emoji): Result<BufferedImage, Error> {
        return generator.generateImageFromEmoji(emoji)
    }
}
