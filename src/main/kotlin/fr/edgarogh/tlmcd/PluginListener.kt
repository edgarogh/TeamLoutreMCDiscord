package fr.edgarogh.tlmcd

import fr.edgarogh.tlmcd.util.each
import fr.edgarogh.tlmcd.util.tellRaw
import fr.edgarogh.tlmcd.util.toBaseComponent
import org.bukkit.event.Listener

class PluginListener(private val plugin: TLMCDPlugin) : Listener {

    fun onTick() {
        val discordClient = plugin.discordClient

        discordClient?.apply {
            eventQueue.each { e ->
                when (e) {
                    is DiscordEvent.IncomingMessage -> {
                        plugin.server.onlinePlayers.forEach {
                            val message = e.message.toBaseComponent(plugin.userLookupTable)
                            it.tellRaw(message)
                        }
                    }
                    is DiscordEvent.LinkAttempt -> {
                        val player = plugin.linkService.findPlayer(e.token)
                        if (player != null) {
                            player.sendMessage("Ton compte Discord est relié !")
                            plugin.userLookupTable.put(e.discordUserId, player)
                        }
                        else {
                            plugin.logger.warning("Linking failed for <@${e.discordUserId}>")

                            discordClient.sendPrivateMessage(
                                e.discordUserId,
                                "Code de raccordement invalide ou expiré ;("
                            )
                        }
                    }
                }
            }
        }
    }

}