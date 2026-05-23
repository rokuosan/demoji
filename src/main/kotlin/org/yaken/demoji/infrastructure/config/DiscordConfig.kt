package org.yaken.demoji.infrastructure.config

import dev.kord.common.entity.Snowflake

data class DiscordConfig(
    val botToken: String,
    val guildIds: List<Snowflake>,
)

object DiscordConfigLoader {
    private const val botTokenEnv = "DISCORD_BOT_TOKEN"
    private const val guildIdEnv = "DISCORD_GUILD_ID"

    fun load(environment: Environment = SystemEnvironment): DiscordConfig {
        val botToken = environment.get(botTokenEnv)
            ?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("$botTokenEnv is not set.")

        val guildIds = environment.get(guildIdEnv)
            ?.takeIf { it.isNotBlank() }
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.map { Snowflake(it) }
            ?: throw IllegalStateException("$guildIdEnv is not set.")

        return DiscordConfig(
            botToken = botToken,
            guildIds = guildIds,
        )
    }
}
