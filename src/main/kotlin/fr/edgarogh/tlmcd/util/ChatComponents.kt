package fr.edgarogh.tlmcd.util

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class ChatComponentBuilder(private val component: BaseComponent) {

    fun color(color: ChatColor) {
        component.color = color
    }

    fun italic() {
        component.isItalic = true
    }

    fun bold() {
        component.isBold = true
    }

    private fun onClick(action: ClickEvent.Action, value: String) {
        component.clickEvent = ClickEvent(action, value)
    }

    fun onClickUrl(url: String) {
        onClick(ClickEvent.Action.OPEN_URL, url)
    }

    fun onClickCommand(command: String) {
        onClick(ClickEvent.Action.RUN_COMMAND, command)
    }

    private fun onHover(action: HoverEvent.Action, value: String) {
        component.hoverEvent = HoverEvent(action, arrayOf(TextComponent(value)))
    }

    fun onHoverPlayer(player: OfflinePlayer) {
        val json = "{type:\"minecraft:player\",name:${player.name},id:${player.uniqueId}}"
        onHover(HoverEvent.Action.SHOW_ENTITY, json)
    }

}

class ChatComponentsBuilder(val baseComponent: BaseComponent) {

    inline fun component(component: BaseComponent, block: ChatComponentBuilder.() -> Unit) {
        ChatComponentBuilder(component).block()
        baseComponent.addExtra(component)
    }

    inline fun text(value: String, block: ChatComponentBuilder.() -> Unit = {}) {
        component(TextComponent(value), block)
    }

}

inline fun chatComponent(block: ChatComponentsBuilder.() -> Unit): BaseComponent {
    val baseComponent = TextComponent("")
    val builder = ChatComponentsBuilder(baseComponent)
    builder.block()
    return baseComponent
}

fun Player.tellRaw(component: BaseComponent) {
    this.server.dispatchCommand(
        this.server.consoleSender,
        "tellraw ${this.name} ${ComponentSerializer.toString(component)}"
    )
}