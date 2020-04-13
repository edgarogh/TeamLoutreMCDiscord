package fr.edgarogh.tlmcd.discord

import fr.edgarogh.tlmcd.RemoteMessage

sealed class DiscordEvent {

    data class IncomingMessage(
        val message: RemoteMessage
    ) : DiscordEvent()

    data class LinkAttempt(
        val discordUserId: String,
        val token: String
    ) : DiscordEvent()

}