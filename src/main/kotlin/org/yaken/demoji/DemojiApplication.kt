package org.yaken.demoji

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.yaken.demoji.application.usecase.EmojiCreationUseCaseImpl
import org.yaken.demoji.application.usecase.EmojiFontUseCaseImpl
import org.yaken.demoji.infrastructure.config.DiscordConfigLoader
import org.yaken.demoji.infrastructure.config.OpenTelemetryConfig
import org.yaken.demoji.infrastructure.discord.DiscordBotAdapter
import org.yaken.demoji.infrastructure.generator.EmojiGenerator

fun main() = runBlocking {
    val openTelemetry = OpenTelemetryConfig.initialize()
    val logger = LoggerFactory.getLogger("demoji")
    val config = DiscordConfigLoader.load()
    val tracer = openTelemetry.getTracer("org.yaken.demoji")

    val emojiFontUseCase = EmojiFontUseCaseImpl()
    val emojiGeneratorService = EmojiGenerator(tracer = tracer)
    val emojiCreationUseCase = EmojiCreationUseCaseImpl(
        generator = emojiGeneratorService,
        emojiFontUseCase = emojiFontUseCase,
    )

    val agent = DiscordBotAdapter(
        logger = logger,
        config = config,
        tracer = tracer,
        emojiCreationUseCase = emojiCreationUseCase,
    )

    agent.start()
}
