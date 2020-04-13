package fr.edgarogh.tlmcd.command

import fr.edgarogh.tlmcd.TLMCDPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class PluginBaseCommand(private val plugin: TLMCDPlugin) : CommandExecutor, TabCompleter {

    private val prefix = "§1§l[TLMCD]§r"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return if (args.isEmpty()) {
            false
        }
        else {
            when (args[0]) {
                "status" -> {
                    val isConnected = plugin.discordClient != null
                    sender.sendMessage("$prefix Plugin connected to Discord: $isConnected")
                    sender.sendMessage("$prefix Links: ${plugin.userLookupTable}")
                    true
                }
                "link" -> {
                    if (sender is Player) {
                        val token = plugin.linkService.requestTokenFor(sender)
                        sender.sendMessage(
                            arrayOf(
                                "$prefix Voici ton code de raccordement pelo: §o$token",
                                "$prefix Envoie le moi en PV sur Discord"
                            )
                        )
                        true
                    }
                    else {
                        sender.sendMessage("Ideally you should be a player to link your account...")
                        false
                    }
                }
                "reload" -> {
                    val wasSuccessful = plugin.loadConfig()
                    val message = if (wasSuccessful) "Reloaded successfully" else "Something wrong happened"
                    sender.sendMessage("$prefix $message")
                    true
                }
                "view-image" -> {
                    sender.sendMessage("$prefix Not implemented. URL: ${args[1]}")
                    true
                }
                else -> false
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return if (args.size == 1) {
            mutableListOf("status", "link", "reload", "view-image")
        }
        else when (args[0]) {
            "link" -> {
                if (sender is Player && args.size == 2) mutableListOf(plugin.linkService.requestTokenFor(sender))
                else mutableListOf()
            }
            else -> mutableListOf()
        }
    }

}