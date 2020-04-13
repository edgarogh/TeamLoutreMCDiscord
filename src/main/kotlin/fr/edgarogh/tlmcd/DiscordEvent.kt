package fr.edgarogh.tlmcd

sealed class DiscordEvent {

    data class IncomingMessage(
        val message: RemoteMessage
    ) : DiscordEvent()

    data class LinkAttempt(
        val discordUserId: String,
        val token: String
    ) : DiscordEvent()

}