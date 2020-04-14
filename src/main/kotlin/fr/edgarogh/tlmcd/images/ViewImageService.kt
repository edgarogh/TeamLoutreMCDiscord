package fr.edgarogh.tlmcd.images

import fr.edgarogh.tlmcd.TLMCDPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class ViewImageService(private val plugin: TLMCDPlugin) : Listener {

    private val currentlyViewing = mutableMapOf<Player, ViewingMode>()

    fun showImage(player: Player, url: String) {
        val vm = ViewingMode(plugin, player, url)
        currentlyViewing[player] = vm
        vm.start()
    }

    private fun reset(player: Player) {
        val vm = currentlyViewing[player]
        if (vm != null) {
            vm.reset()
            currentlyViewing.remove(player)
        }
    }

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) = if (e.to != null && e.from.distance(e.to!!) > 0) reset(e.player) else Unit

    @EventHandler
    fun onHeldItemChange(e: PlayerItemHeldEvent) = reset(e.player)

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) = reset(e.player)

    @EventHandler
    fun onDisconnect(e: PlayerQuitEvent) = reset(e.player)

}