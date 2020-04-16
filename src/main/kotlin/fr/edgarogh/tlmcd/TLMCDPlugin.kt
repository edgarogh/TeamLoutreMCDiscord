package fr.edgarogh.tlmcd

import fr.edgarogh.tlmcd.command.DiscordSayCommand
import fr.edgarogh.tlmcd.command.PluginBaseCommand
import fr.edgarogh.tlmcd.discord.DiscordClient
import fr.edgarogh.tlmcd.images.ViewImageService
import org.bukkit.plugin.java.JavaPlugin

class TLMCDPlugin() : JavaPlugin() {

    val linkService = LinkService()
    val userLookupTable = UserLookupTable()
    var viewImageService: ViewImageService? = null
    val statusService = StatusService(this)
    var discordClient: DiscordClient? = null

    private val lookupTableFile by lazy {
        dataFolder.resolve("lookup.csv").apply {
            if (!exists()) createNewFile()
        }
    }

    fun loadConfig(): Boolean {
        val apiKey = config.getString(CONFIG_DISCORD_API_KEY) ?: return false
        val channelId = config.getString(CONFIG_DISCORD_CHANNEL_ID) ?: return false

        try {
            discordClient = DiscordClient(this, apiKey, channelId)
        }
        catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    override fun onEnable() {
        saveDefaultConfig()

        val protocolLib = server.pluginManager.getPlugin("ProtocolLib")
        if (protocolLib != null) {
            viewImageService = ViewImageService(this)
            server.pluginManager.registerEvents(viewImageService!!, this)
        }

        PluginListener(this).let {
            server.scheduler.scheduleSyncRepeatingTask(this, it::onTick, 0, 1)
        }

        val tldExecutor = PluginBaseCommand(this)
        getCommand("teamloutre-discord")!!.apply {
            setExecutor(tldExecutor)
            tabCompleter = tldExecutor
        }

        val discordSayExecutor = DiscordSayCommand(this)
        getCommand("dsay")!!.apply {
            setExecutor(discordSayExecutor)
        }

        loadConfig()

        server.pluginManager.registerEvents(statusService, this)
        server.scheduler.scheduleSyncRepeatingTask(this, statusService::updateStatus, 0, 5 * 20)

        userLookupTable.load(lookupTableFile.readText())
    }

    override fun onDisable() {
        discordClient?.dispose()
        lookupTableFile.writeText(userLookupTable.serialize())
    }

    companion object {
        const val CONFIG_DISCORD_API_KEY = "discord-api-key"
        const val CONFIG_DISCORD_CHANNEL_ID = "discord-channel-id"
    }

}
