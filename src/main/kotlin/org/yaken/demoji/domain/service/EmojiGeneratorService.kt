package org.yaken.demoji.domain.service

import org.yaken.demoji.common.Result
import org.yaken.demoji.domain.entity.Emoji
import java.awt.image.BufferedImage
import java.nio.file.Path

interface EmojiGeneratorService {
    fun generateImageFromEmoji(emoji: Emoji): Result<BufferedImage, Error>
    fun generateImageToTempFile(emoji: Emoji): Result<Path, Error>
}
