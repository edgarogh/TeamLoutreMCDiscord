package fr.edgarogh.tlmcd.command

import fr.edgarogh.tlmcd.TLMCDPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class DiscordSayCommand(private val plugin: TLMCDPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val discordClient = plugin.discordClient

        if (discordClient != null) {
            val message = args.joinToString(" ")

            val username = when (sender) {
                is Player -> {
                    val discordUserId = plugin.userLookupTable.getDiscordId(sender)
                    if (discordUserId != null) "<@$discordUserId>" else sender.name
                }
                is ConsoleCommandSender -> "_console_"
                else -> "_unknown sender_"
            }

            val success = discordClient.sendPublicMessage("**<$username>** $message", true)

            if (success) {
                sender.server.broadcastMessage("§1§l*§r<${sender.name}> $message")
            }
            else {
                sender.sendMessage("§1§l[TLMCD]§r Error while sending message")
            }
        }
        else {
            sender.sendMessage("§1§l[TLMCD]§r Discord bot is offline")
        }

        return true
    }

}