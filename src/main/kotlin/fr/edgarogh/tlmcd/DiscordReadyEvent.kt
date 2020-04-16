package fr.edgarogh.tlmcd

import fr.edgarogh.tlmcd.discord.DiscordClient
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class DiscordReadyEvent(private val discordClient: DiscordClient) : Event() {

    fun getDiscordClient() = discordClient

    override fun getHandlers() = getHandlerList()

    companion object {

        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlers

    }

}
