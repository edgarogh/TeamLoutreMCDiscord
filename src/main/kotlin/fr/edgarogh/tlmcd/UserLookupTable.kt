package fr.edgarogh.tlmcd

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*
import kotlin.collections.HashMap

class UserLookupTable() {

    private val map = HashMap<String, OfflinePlayer>()

    fun load(saved: String) {
        map.clear()
        saved.split('\n')
            .map { it.split(',') }
            .filter { it.size == 2 }
            .forEach { map[it[0]] = Bukkit.getOfflinePlayer(UUID.fromString(it[1])) }
    }

    fun put(discordUserId: String, minecraftPlayer: OfflinePlayer) {
        map[discordUserId] = minecraftPlayer
    }

    fun getMinecraftPlayer(discordUserId: String) = map[discordUserId]

    fun getDiscordId(player: OfflinePlayer) = map
        .filterValues { it.uniqueId == player.uniqueId }
        .keys
        .firstOrNull()

    fun serialize() = map.entries.joinToString("\n") { (discordId, player) -> "$discordId,${player.uniqueId}" }

    override fun toString() = map.entries.joinToString { (discordId, player) -> "<@$discordId> -> ${player.name}" }

}
