package ru.joutak.lobby.music.players_settings

import org.bukkit.entity.Player
import ru.joutak.lobby.music.config.ConfigManager
import ru.joutak.lobby.music.zone.ZoneManager

object PlayersSettingsManager {
    private var playerSettings: MutableMap<String, Boolean> = mutableMapOf()

    fun load() {
        playerSettings = ConfigManager.loadPlayersSettings() ?: mutableMapOf()
    }

    fun save() {
        ConfigManager.savePlayerSettings(playerSettings)
    }

    fun getPlayerSettings() = playerSettings

    fun musicOn(player: Player) {
        playerSettings[player.uniqueId.toString()] = true
        ZoneManager
            .getMusicZones()
            .values
            .filter { zone ->
                zone.location.world.name == player.world.name &&
                        zone.isInRange(player.location)
            }.forEach { zone ->
                zone.playFor(player)
            }
    }

    fun musicOff(player: Player) {
        playerSettings[player.uniqueId.toString()] = false
        ZoneManager
            .getMusicZones()
            .values
            .forEach { zone -> zone.stopFor(player) }
    }

    fun canHearMusic(player : Player) : Boolean {
        return playerSettings[player.uniqueId.toString()] == true
    }
}