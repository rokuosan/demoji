package org.yaken.demoji.infrastructure.config

import dev.kord.common.entity.Snowflake
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DiscordConfigTest : FunSpec({
    test("loads discord config from environment") {
        val config = DiscordConfigLoader.load(
            MapEnvironment(
                "DISCORD_BOT_TOKEN" to "token",
                "DISCORD_GUILD_ID" to "111, 222",
            ),
        )

        config.botToken shouldBe "token"
        config.guildIds shouldBe listOf(Snowflake("111"), Snowflake("222"))
    }

    test("rejects blank bot token") {
        val error = shouldThrow<IllegalStateException> {
            DiscordConfigLoader.load(
                MapEnvironment(
                    "DISCORD_BOT_TOKEN" to "",
                    "DISCORD_GUILD_ID" to "111",
                ),
            )
        }

        error.message shouldBe "DISCORD_BOT_TOKEN is not set."
    }

    test("rejects blank guild id") {
        val error = shouldThrow<IllegalStateException> {
            DiscordConfigLoader.load(
                MapEnvironment(
                    "DISCORD_BOT_TOKEN" to "token",
                    "DISCORD_GUILD_ID" to "",
                ),
            )
        }

        error.message shouldBe "DISCORD_GUILD_ID is not set."
    }
})
