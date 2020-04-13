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

    fun getMinecraftPlayer(discordUserId: String): OfflinePlayer? {
        return map[discordUserId]
    }

    fun serialize() = map.entries.joinToString("\n") { (discordId, player) -> "$discordId,${player.uniqueId}" }

    override fun toString() = map.entries.joinToString { (discordId, player) -> "<@$discordId> -> ${player.name}" }

}