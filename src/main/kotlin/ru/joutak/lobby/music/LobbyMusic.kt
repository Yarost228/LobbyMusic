package ru.joutak.lobby.music

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.java.JavaPlugin
import ru.joutak.lobby.music.commands.core.CoreCommandExecutor
import ru.joutak.lobby.music.config.ConfigManager
import ru.joutak.lobby.music.event.*
import ru.joutak.lobby.music.music.Music
import ru.joutak.lobby.music.music.MusicManager
import ru.joutak.lobby.music.players_settings.PlayersSettingsManager
import ru.joutak.lobby.music.zone.Zone
import ru.joutak.lobby.music.zone.ZoneManager


class LobbyMusic : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var instance: LobbyMusic

        val TITLE =
            LinearComponents.linear(
                Component.text("L", NamedTextColor.DARK_BLUE),
                Component.text("o", NamedTextColor.BLUE),
                Component.text("b", NamedTextColor.AQUA),
                Component.text("b", NamedTextColor.LIGHT_PURPLE),
                Component.text("y", NamedTextColor.DARK_PURPLE),
                Component.text("M", NamedTextColor.DARK_BLUE),
                Component.text("u", NamedTextColor.BLUE),
                Component.text("s", NamedTextColor.AQUA),
                Component.text("i", NamedTextColor.LIGHT_PURPLE),
                Component.text("c", NamedTextColor.DARK_PURPLE),
            )
    }

    /**
     * Plugin startup logic
     */
    override fun onEnable() {
        instance = this

        // Load config and necessary data
        loadConfig()
        loadData()

        // Register commands and events
        registerCommands()
        registerEvents()

        val skipPerm = Permission(
            "lobbymusic.skip",
            "Allows to skip music in zones",
            PermissionDefault.OP
        )
        Bukkit.getPluginManager().addPermission(skipPerm)

        logger.info("Плагин ${pluginMeta.name} версии ${pluginMeta.version} включен!")

        // Start playing music in all zones after delay
        Bukkit.getScheduler().runTaskLater(
            instance,
            Runnable {
                ZoneManager.startAllZones()
            },
            ConfigManager.getConfig().startDelay * 20L,
        )
    }

    /**
     * Plugin shutdown logic
     */
    override fun onDisable() {
        ZoneManager.stopAllZones()
        saveData()
    }

    private fun registerCommands() {
        getCommand("lm")?.setExecutor(CoreCommandExecutor)
    }

    private fun registerEvents() {
        Bukkit.getPluginManager().registerEvents(PlayerJoinZoneListener, instance)
        Bukkit.getPluginManager().registerEvents(ZoneNextTrackListener, instance)
        Bukkit.getPluginManager().registerEvents(PlayerChangedWorldListener, instance)
        Bukkit.getPluginManager().registerEvents(PlayerMoveListener, instance)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener, instance)
        Bukkit.getPluginManager().registerEvents(PlayerJoinEvent, instance)
    }

    private fun loadConfig() {
        ConfigManager.loadConfig()
    }

    private fun loadData() {
        ConfigurationSerialization.registerClass(Zone::class.java, "Zone")
        ConfigurationSerialization.registerClass(Music::class.java, "Music")
        MusicManager.load()
        ZoneManager.load()
        PlayersSettingsManager.load()
    }

    private fun saveData() {
        ZoneManager.save()
        MusicManager.save()
        PlayersSettingsManager.save()
    }
}
