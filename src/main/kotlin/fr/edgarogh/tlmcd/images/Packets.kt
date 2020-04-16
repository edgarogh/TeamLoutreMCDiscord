package fr.edgarogh.tlmcd.images

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

private val protocolManager by lazy { ProtocolLibrary.getProtocolManager() }

fun Player.sendSlotChange(slotId: Int, itemStack: ItemStack, window: Int = -2) {
    val setSlotPacket = protocolManager.createPacket(PacketType.Play.Server.SET_SLOT).apply {
        integers.write(0, window)
        integers.write(1, slotId)
        itemModifier.write(0, itemStack)
    }

    protocolManager.sendServerPacket(this, setSlotPacket)
}

fun Player.sendMapChange(mapId: Int, data: ByteArray) {
    // int , byte , boolean , boolean , MapIcon[] , int , int , int , int , byte[]
    val mapDataPacket = protocolManager.createPacket(PacketType.Play.Server.MAP).apply {
        integers.write(0, mapId)
        bytes.write(0, 0) // Zoom
        booleans.write(0, true) // Tracking position (cursors)
        booleans.write(1, false) // Locked
        // - Ignore map icons/cursors
        integers.write(1, 0) // X offset
        integers.write(2, 0) // Y offset
        integers.write(3, 128) // Columns
        integers.write(4, 128) // Rows
        byteArrays.write(0, data)
    }

    protocolManager.sendServerPacket(this, mapDataPacket)
}
