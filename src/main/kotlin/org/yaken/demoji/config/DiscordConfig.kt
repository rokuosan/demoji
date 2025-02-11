package org.yaken.demoji.config

import dev.kord.common.entity.Snowflake

object DiscordConfig {
    /**
     * Discord Botのトークン
     */
    val BotToken: String = System.getenv("DISCORD_BOT_TOKEN")
        .let { it.ifBlank { null } }
        ?: throw IllegalStateException("DISCORD_BOT_TOKEN is not set.")

    /**
     * Discord Botがコマンドを登録するGuildのID。
     */
    val GuildIDs: List<Snowflake> = System.getenv("DISCORD_GUILD_ID")
        ?.let { if (it.isBlank()) null else it.split(",")
            .filter { line -> line.isNotBlank() }
            .map { line -> Snowflake(line) } }
        ?: throw IllegalStateException("DISCORD_GUILD_ID is not set.")
}
