package org.yaken.demoji.domain.service

import org.yaken.demoji.common.Result
import org.yaken.demoji.domain.entity.Emoji
import java.awt.image.BufferedImage
import java.nio.file.Path

interface EmojiGeneratorService {
    suspend fun generateImageFromEmoji(emoji: Emoji): Result<BufferedImage, Error>
    suspend fun generateImageToTempFile(emoji: Emoji): Result<Path, Error>
}
