package org.yaken.demoji.infrastructure.discord

import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class DiscordChannelCompatibilityTest : FunSpec({
    test("decodes channel with null application id") {
        val channel = Json.decodeFromString<DiscordChannel>(
            """{"id":"123","type":0,"application_id":null}""",
        )

        channel.applicationId shouldBe null
    }

    test("decodes channel with missing application id") {
        val channel = Json.decodeFromString<DiscordChannel>("""{"id":"123","type":0}""")

        channel.applicationId shouldBe OptionalSnowflake.Missing
    }

    test("decodes channel with application id") {
        val channel = Json.decodeFromString<DiscordChannel>(
            """{"id":"123","type":0,"application_id":"456"}""",
        )

        channel.applicationId shouldBe OptionalSnowflake.Value(Snowflake(456uL))
    }
})
