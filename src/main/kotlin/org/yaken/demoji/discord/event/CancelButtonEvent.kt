package org.yaken.demoji.discord.event

import dev.kord.core.entity.interaction.ButtonInteraction

suspend fun onCancelButtonClicked(interaction: ButtonInteraction) {
    interaction.deferEphemeralMessageUpdate().delete()
}
