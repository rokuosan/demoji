package org.yaken.demoji

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.yaken.demoji.application.usecase.EmojiFontUseCaseImpl
import org.yaken.demoji.infrastructure.config.DiscordConfig
import org.yaken.demoji.infrastructure.config.OpenTelemetryConfig
import org.yaken.demoji.infrastructure.discord.DiscordBotAdapter
import org.yaken.demoji.infrastructure.generator.EmojiGenerator

fun main() = runBlocking {
    val openTelemetry = OpenTelemetryConfig.initialize(otlpEndpoint = "https://otlp-vaxila.mackerelio.com/v1/traces")
    val logger = LoggerFactory.getLogger("demoji")
    val config = DiscordConfig
    val tracer = openTelemetry.getTracer("org.yaken.demoji")

    val emojiFontUseCase = EmojiFontUseCaseImpl()
    val emojiGeneratorService = EmojiGenerator(tracer = tracer)

    val agent = DiscordBotAdapter(
        logger = logger,
        config = config,
        tracer = tracer,
        generator = emojiGeneratorService,
        emojiFontUseCase = emojiFontUseCase,
    )

    agent.start()
}
