package fr.edgarogh.tlmcd

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.ConcurrentLinkedQueue

class DiscordClient(apiKey: String, private val watchedChannelId: String) : Disposable, EventListener {

    val eventQueue = ConcurrentLinkedQueue<DiscordEvent>()

    private val jda = JDABuilder.createDefault(apiKey)
        .setAutoReconnect(true)
        .addEventListeners(this)
        .build()

    private fun handleLinkRequest(discordUser: User, token: String) {
        eventQueue += DiscordEvent.LinkAttempt(discordUser.id, token)
    }

    private fun handleTransfer(discordMessage: Message) {
        eventQueue += DiscordEvent.IncomingMessage(RemoteMessage(
            discordMessage.author.id,
            discordMessage.author.name + "#" + discordMessage.author.discriminator,
            discordMessage.contentRaw,
            discordMessage.attachments.filter { it.isImage }.map { it.fileName to it.url }.toSet()
        )
        )
    }

    override fun onEvent(e: GenericEvent) {
        when (e) {
            is GuildJoinEvent -> {
                if (jda.guilds.isNotEmpty()) {
                    e.guild.defaultChannel?.sendMessage("Je ne peux pas être sur plusieurs serveurs à la fois")
                    e.guild.leave()
                }
            }
            is MessageReceivedEvent -> {
                val discordMessage = e.message
                when (discordMessage.channelType) {
                    ChannelType.PRIVATE -> {
                        handleLinkRequest(discordMessage.author, discordMessage.contentRaw.trim())
                    }
                    ChannelType.TEXT -> {
                        if (discordMessage.channel.id == watchedChannelId) {
                            handleTransfer(discordMessage)
                        }
                    }
                    else -> {
                        discordMessage.channel.sendMessage("Je ne gère pas les messages dans ce type de salon")
                    }
                }
            }
        }
    }

    fun sendPrivateMessage(discordUserId: String, message: String) {
        jda.retrieveUserById(discordUserId)
            .flatMap { it.openPrivateChannel() }
            .queue { it.sendMessage(message) }
    }

    override fun dispose() = jda.shutdown()

}
