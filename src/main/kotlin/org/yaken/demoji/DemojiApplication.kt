package org.yaken.demoji

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.yaken.demoji.application.usecase.EmojiFontUseCaseImpl
import org.yaken.demoji.infrastructure.config.DiscordConfig
import org.yaken.demoji.infrastructure.discord.DiscordBotAdapter
import org.yaken.demoji.infrastructure.generator.EmojiGenerator
import org.yaken.demoji.infrastructure.config.OpenTelemetryConfig

fun main() = runBlocking {
    OpenTelemetryConfig.initialize(otlpEndpoint = "https://otlp-vaxila.mackerelio.com/v1/traces")
    val logger = LoggerFactory.getLogger("demoji")
    val config = DiscordConfig

    val emojiFontUseCase = EmojiFontUseCaseImpl()
    val emojiGeneratorService = EmojiGenerator()

    val agent = DiscordBotAdapter(
        logger = logger,
        config = config,
        tracer = OpenTelemetryConfig.tracer,
        generator = emojiGeneratorService,
        emojiFontUseCase = emojiFontUseCase,
    )

    agent.start()
}
