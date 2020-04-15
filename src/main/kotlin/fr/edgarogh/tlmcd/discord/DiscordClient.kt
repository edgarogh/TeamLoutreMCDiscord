package fr.edgarogh.tlmcd.discord

import fr.edgarogh.tlmcd.DiscordReadyEvent
import fr.edgarogh.tlmcd.Disposable
import fr.edgarogh.tlmcd.RemoteMessage
import fr.edgarogh.tlmcd.TLMCDPlugin
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.bukkit.Bukkit
import java.util.concurrent.ConcurrentLinkedQueue

class DiscordClient(
    private val plugin: TLMCDPlugin,
    apiKey: String,
    private val channelId: String
) : Disposable, EventListener {

    val eventQueue = ConcurrentLinkedQueue<DiscordEvent>()

    private val jda = JDABuilder.createDefault(apiKey)
        .setAutoReconnect(true)
        .addEventListeners(this)
        .build()

    private fun handleLinkRequest(discordUser: User, token: String) {
        eventQueue += DiscordEvent.LinkAttempt(discordUser.id, token)
    }

    private fun handleTransfer(discordMessage: Message) {
        eventQueue += DiscordEvent.IncomingMessage(
            RemoteMessage(
                discordMessage.author.id,
                discordMessage.author.name + "#" + discordMessage.author.discriminator,
                discordMessage.contentRaw,
                discordMessage.attachments.filter { it.isImage }.map { it.fileName to it.url }.toSet()
            )
        )
    }

    override fun onEvent(e: GenericEvent) {
        when (e) {
            is ReadyEvent -> plugin.server.scheduler.runTask(plugin) { ->
                plugin.server.pluginManager.callEvent(DiscordReadyEvent(this))
            }
            is GuildJoinEvent -> {
                if (jda.guilds.isNotEmpty()) {
                    e.guild.defaultChannel?.sendMessage("Je ne peux pas être sur plusieurs serveurs à la fois")
                    e.guild.leave()
                }
            }
            is MessageReceivedEvent -> {
                if (e.message.author.idLong == jda.selfUser.idLong) return

                val discordMessage = e.message
                when (discordMessage.channelType) {
                    ChannelType.PRIVATE -> {
                        handleLinkRequest(discordMessage.author, discordMessage.contentRaw.trim())
                    }
                    ChannelType.TEXT -> {
                        if (discordMessage.channel.id == channelId) {
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

    var status: String
        get() = jda.presence.activity?.name ?: ""
        set(value) {
            jda.presence.activity = Activity.playing(value)
        }

    /**
     * Sends a message in the configured text channel
     * @param noMention When `true`, sends a message, then edits it, in order not to ping mentioned user(s)
     */
    fun sendPublicMessage(content: String, noMention: Boolean = false): Boolean {
        val channel = jda.guilds.firstOrNull()?.getTextChannelById(channelId)

        if (channel == null) {
            Bukkit.getLogger().warning("Bot not connected or bad Discord channel ID provided")
            return false
        }

        if (!noMention || '@' !in content) {
            channel
                .sendMessage(content)
                .queue()
        }
        else {
            channel
                .sendMessage(content.replace("@", "@ "))
                .flatMap { it.editMessage(content) }
                .queue()
        }

        return true
    }

    fun sendPrivateMessage(discordUserId: String, message: String) {
        jda.retrieveUserById(discordUserId)
            .flatMap { it.openPrivateChannel() }
            .flatMap { it.sendMessage(message) }
            .queue()
    }

    override fun dispose() = jda.shutdown()

}
