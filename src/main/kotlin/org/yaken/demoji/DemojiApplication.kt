package org.yaken.demoji

import kotlinx.coroutines.runBlocking
import org.yaken.demoji.discord.BotAgent

fun main() = runBlocking {
    val bot = BotAgent()
    bot.start()
}
