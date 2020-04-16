package fr.edgarogh.tlmcd

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class StatusService(private val plugin: TLMCDPlugin) : Listener {

    var cachedPlayerCount = -1

    fun updateStatus() {
        val players = plugin.server.onlinePlayers.filter { it.isOnline }
        val playerCount = players.size

        if (cachedPlayerCount != playerCount) {
            cachedPlayerCount = playerCount

            plugin.discordClient?.status = when (playerCount) {
                0 -> "Minecraft seul"
                1 -> "Minecraft avec ${players.parallelStream().findFirst().get().name}"
                else -> "Minecraft avec $playerCount pelos"
            }
        }
    }

    @EventHandler
    fun onDiscordReady(e: DiscordReadyEvent) = updateStatus()

}
