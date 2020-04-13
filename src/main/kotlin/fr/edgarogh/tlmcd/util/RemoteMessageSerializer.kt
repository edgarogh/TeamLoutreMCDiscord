package fr.edgarogh.tlmcd.util

import fr.edgarogh.tlmcd.RemoteMessage
import fr.edgarogh.tlmcd.UserLookupTable
import net.md_5.bungee.api.ChatColor

fun RemoteMessage.toBaseComponent(userLookupTable: UserLookupTable) =
    chatComponent {
        val minecraftPlayer = userLookupTable.getMinecraftPlayer(discordUserId)

        if (minecraftPlayer != null) {
            text("<${minecraftPlayer.name}> ") {
                color(ChatColor.BLUE)
                onHoverPlayer(minecraftPlayer)
            }
        }
        else {
            text("<@$discordUserName> ") {
                color(ChatColor.BLUE)
                onClickUrl("https://discordapp.com/channels/@me/$discordUserId")
            }
        }

        text(content)

        if (imageAttachments.isNotEmpty()) {
            text("\n ")
        }

        imageAttachments.forEach { (fileName, url) ->
            text("[$fileName] ") {
                color(ChatColor.YELLOW)
                bold()
                onClickCommand("/teamloutre-discord view-image $url")
            }
        }
    }