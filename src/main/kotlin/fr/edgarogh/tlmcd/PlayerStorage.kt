package fr.edgarogh.tlmcd

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import kotlin.reflect.KProperty

private val plugin by lazy { Bukkit.getServer().pluginManager.getPlugin("tlmcd")!! }

class PersistentDataDelegate<T>(key: String, private val type: PersistentDataType<T, T>, private val defaultValue: T) {

    private val key by lazy { NamespacedKey(plugin, key) }

    operator fun getValue(that: PersistentDataHolder, property: KProperty<*>): T =
        that.persistentDataContainer[key, type] ?: defaultValue

    operator fun setValue(that: PersistentDataHolder, property: KProperty<*>, value: T) {
        that.persistentDataContainer[key, type] = value
    }

}

var Player.tlmcdReceiveOn: Byte by PersistentDataDelegate("receiveOn", PersistentDataType.BYTE, 1)
