package ru.joutak.lobby.music.event

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import ru.joutak.lobby.music.players_settings.PlayersSettingsManager

object PlayerJoinEvent : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (PlayersSettingsManager.getPlayerSettings().containsKey(event.player.uniqueId.toString())) return
        PlayersSettingsManager.musicOn(event.player)
    }
}