package org.yaken.demoji.config

import dev.kord.common.entity.Snowflake

object DiscordConfig {
    val discordBotToken: String = System.getenv("DISCORD_BOT_TOKEN")
        .let { it.ifBlank { null } }
        ?: throw IllegalStateException("DISCORD_BOT_TOKEN is not set.")

    val discordGuildID: Snowflake = System.getenv("DISCORD_GUILD_ID")
        ?.let { if (it.isBlank()) null else Snowflake(it) }
        ?: throw IllegalStateException("DISCORD_GUILD_ID is not set.")

}
