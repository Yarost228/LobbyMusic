package ru.joutak.lobby.music.zone

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import ru.joutak.lobby.music.event.zone.ZoneNextTrackEvent
import ru.joutak.lobby.music.music.Music
import ru.joutak.lobby.music.music.MusicScheduler
import ru.joutak.lobby.music.utils.PluginManager

class ZoneMusicPlayer(
    private val zone: Zone,
) {
    private var musicScheduler: MusicScheduler = MusicScheduler()
    private var currentMusic: Music? = null
    private var musicTask: BukkitTask? = null

    fun getCurrentMusic(): Music? = currentMusic

    fun isMusicPlaying(): Boolean = currentMusic != null

    fun play() {
        if (currentMusic != null) return

        playNext(musicScheduler.getNextMusic())
    }

    fun skipCurrentMusic() {
        playNext(musicScheduler.getNextMusic())
    }

    private fun playNext(music: Music) {
        stop()
        currentMusic = music

        zone.getListeners().forEach { listener -> playFor(listener) }

        MusicScheduler.addPlayingMusic(music)

        musicTask =
            Bukkit.getScheduler().runTaskLater(
                PluginManager.lobbyMusic,
                Runnable {
                    Bukkit.getPluginManager().callEvent(ZoneNextTrackEvent(zone))
                },
                music.duration * 20L,
            )
    }

    fun playFor(player: Player) {
        if (currentMusic == null) return

        player.playSound(
            zone.getCurrentMusic()!!.toSound(zone.getVolume()),
            zone.location.x,
            zone.location.y,
            zone.location.z,
        )

        val task =
            Bukkit.getScheduler().runTaskTimer(
                PluginManager.lobbyMusic,
                Runnable {
                    val color = NamedTextColor.NAMES.values().random()

                    player.sendActionBar(
                        LinearComponents.linear(
                            Component.text("Сейчас играет: ", color, TextDecoration.BOLD),
                            Component.text(currentMusic!!.asAuthorTitleString, color),
                        ),
                    )
                },
                0L,
                5L,
            )

        Bukkit.getScheduler().runTaskLater(
            PluginManager.lobbyMusic,
            Runnable {
                Bukkit.getScheduler().cancelTask(task.taskId)
            },
            60L,
        )
    }

    fun stop() {
        musicTask?.cancel().also { musicTask = null }

        if (currentMusic == null) return

        MusicScheduler.removePlayingMusic(currentMusic!!)

        Audience
            .audience(
                zone.getListeners(),
            ).stopSound(currentMusic!!.toSoundStop())

        currentMusic = null
    }
}
