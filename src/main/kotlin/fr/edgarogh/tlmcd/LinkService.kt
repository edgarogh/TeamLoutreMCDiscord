package fr.edgarogh.tlmcd

import fr.edgarogh.tlmcd.util.getRandomPronounceableString
import org.apache.commons.collections4.map.PassiveExpiringMap
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.TimeUnit

class LinkService : Listener {

    private val tokenToPlayer = PassiveExpiringMap(5, TimeUnit.MINUTES, HashMap<String, Player>())

    private fun findToken(player: OfflinePlayer) = tokenToPlayer
        .filterValues { it.uniqueId == player.uniqueId }
        .keys
        .firstOrNull()

    @EventHandler
    fun onDisconnect(e: PlayerQuitEvent) {
        val playerToken = findToken(e.player)
        playerToken?.let(tokenToPlayer::remove)
    }

    fun requestTokenFor(player: Player): String {
        val existingToken = findToken(player)
        val token = existingToken ?: getRandomPronounceableString(8)
        tokenToPlayer[token] = player
        return token
    }

    fun findPlayer(token: String) = tokenToPlayer[token]

}