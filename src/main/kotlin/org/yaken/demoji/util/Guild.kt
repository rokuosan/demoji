package org.yaken.demoji.util

import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.createEmoji
import dev.kord.core.entity.GuildEmoji
import dev.kord.rest.Image
import java.awt.image.BufferedImage

suspend fun GuildBehavior.createEmoji(name: String, image: BufferedImage): GuildEmoji {
    val raw = Image.raw(image.toByteArray(), Image.Format.PNG)
    return this.createEmoji(name, raw)
}
