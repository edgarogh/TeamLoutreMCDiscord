package fr.edgarogh.tlmcd

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.io.File
import java.util.*

class UserLookupTable(private val file: File) {

    private val map = run {
        val lines = if (file.exists()) file.readLines() else listOf()

        lines
            .map { it.split(',') }
            .filter { it.size == 2 }
            .map { (discordId, uuid) -> discordId to Bukkit.getOfflinePlayer(UUID.fromString(uuid)) }
            .toMap()
            .toMutableMap()
    }

    private var dead = false

    fun put(discordUserId: String, minecraftPlayer: OfflinePlayer) {
        if (dead) {
            throw IllegalStateException("Cannot write on dead ${javaClass.name}")
        }

        map[discordUserId] = minecraftPlayer
    }

    fun getMinecraftPlayer(discordUserId: String) = map[discordUserId]

    fun getDiscordId(player: OfflinePlayer) = map
        .filterValues { it.uniqueId == player.uniqueId }
        .keys
        .firstOrNull()

    private fun serialize() =
        map.entries.joinToString("\n", postfix = "\n") { (userId, player) -> "$userId,${player.uniqueId}" }

    fun saveAndDestroy() {
        file.writeText(serialize())
        dead = true
    }

    override fun toString() = map.entries.joinToString { (discordId, player) -> "<@$discordId> -> ${player.name}" }

    protected fun finalize() {
        if (!dead) {
            throw IllegalStateException("Dirty ${javaClass.name} is being finalized")
        }
    }

}
