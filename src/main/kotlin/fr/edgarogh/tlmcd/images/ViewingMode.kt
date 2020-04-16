package fr.edgarogh.tlmcd.images

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.map.MapPalette
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.max

class ViewingMode(private val plugin: Plugin, private val player: Player, private val url: String) {

    private var changedSlot = -1
    private var task: BukkitTask? = null

    fun start() {
        changedSlot = player.inventory.heldItemSlot

        player.sendSlotChange(player.inventory.heldItemSlot, MAP_STACK)
        player.sendSlotChange(45 /* off-hand slot */, AIR_STACK)

        plugin.server.scheduler.runTaskAsynchronously(plugin) { task ->
            this@ViewingMode.task = task

            // Load image from internet
            val image = ImageIO.read(URL(url))

            // Handle cancellation
            if (task.isCancelled) return@runTaskAsynchronously

            // Resize / scale / center image
            val maxSize = image.run { max(width, height) }
            val newImage = BufferedImage(maxSize, maxSize, image.type)

            newImage.graphics.run {
                val offsetX = (maxSize - image.width) / 2
                val offsetY = (maxSize - image.height) / 2
                drawImage(image, offsetX, offsetY, null)
                dispose()
            }

            // Handle cancellation
            if (task.isCancelled) return@runTaskAsynchronously

            // Send image to client
            @Suppress("DEPRECATION") val data = MapPalette.imageToBytes(MapPalette.resizeImage(newImage))
            player.sendMapChange(0, data)
        }
    }

    fun reset() {
        task?.cancel()
        player.sendMapChange(0, NULL_MAP_DATA)
        player.sendSlotChange(changedSlot, player.inventory.getItem(changedSlot) ?: AIR_STACK)
        player.sendSlotChange(45, player.inventory.itemInOffHand)
    }

    companion object {

        val MAP_STACK = ItemStack(Material.FILLED_MAP)
        val AIR_STACK = ItemStack(Material.AIR)

        val NULL_MAP_DATA = ByteArray(128 * 128) { 0 }

    }

}
