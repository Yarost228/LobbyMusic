package ru.joutak.lobby.music.zone

import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs
import org.bukkit.entity.Player
import ru.joutak.lobby.music.music.Music

@SerializableAs("Zone")
data class Zone(
    val location: Location,
    val range: Int,
) : ConfigurationSerializable {
    companion object {
        @JvmStatic
        fun deserialize(serializedZone: Map<String, Any>): Zone {
            val location = Location.deserialize(serializedZone["location"] as Map<String, Any>)
            val range = serializedZone["range"] as Int

            return Zone(location, range)
        }
    }

    private val playerManager: ZonePlayerManager = ZonePlayerManager(this)
    private val musicPlayer: ZoneMusicPlayer = ZoneMusicPlayer(this)

    fun getCurrentMusic(): Music? = musicPlayer.getCurrentMusic()

    fun getListeners(): Set<Player> = playerManager.getListeners()

    fun getVolume(): Float = range / 16f

    fun isMusicPlaying(): Boolean = musicPlayer.isMusicPlaying()

    fun isInRange(location: Location): Boolean {
        if (location.world.name != this.location.world.name) return false

        return this.location.distanceSquared(location) <= this.range.let { it * it }
    }

    fun addListener(player: Player) = playerManager.add(player)

    fun removeListener(player: Player) = playerManager.remove(player)

    fun play() = musicPlayer.play()

    fun skipCurrentMusic() = musicPlayer.skipCurrentMusic()

    fun playFor(player: Player) {
        addListener(player)
        musicPlayer.playFor(player)
    }

    fun stop() = musicPlayer.stop()

    fun stopFor(player: Player) = musicPlayer.stopFor(player)

    override fun serialize(): Map<String, Any> {
        val serializedZone = mutableMapOf<String, Any>()

        serializedZone["location"] = location.serialize()
        serializedZone["range"] = range
        return serializedZone
    }
}
