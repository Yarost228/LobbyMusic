package ru.joutak.lobby.music.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.watch.ReloadableConfig
import com.sksamuel.hoplite.watch.watchers.FileWatcher
import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.lobby.music.music.Music
import ru.joutak.lobby.music.utils.PluginManager
import ru.joutak.lobby.music.zone.Zone
import java.io.File
import java.io.IOException

@OptIn(ExperimentalHoplite::class)
object ConfigManager {
    private val configFile = File(PluginManager.dataFolder, "config.yml")
    private val playlistFile = File(PluginManager.dataFolder, "playlist.yml")
    private val musicZonesFile = File(PluginManager.dataFolder, "zones.yml")
    private val playersSettingsFile = File(PluginManager.dataFolder, "players_settings.yml")

    private val configWatcher = FileWatcher(configFile.parent)
    private val configLoader =
        ConfigLoaderBuilder
            .newBuilder()
            .withClassLoader(PluginManager.lobbyMusic.javaClass.classLoader)
            .addDefaultDecoders()
            .addDefaultPreprocessors()
            .addDefaultParamMappers()
            .addDefaultPropertySources()
            .addDefaultParsers()
            .withExplicitSealedTypes()
            .addFileSource(configFile)
            .build()
    private var configReloader: ReloadableConfig<Config>? = null

    fun loadConfig() {
        if (!configFile.exists()) {
            PluginManager.lobbyMusic.saveResource("config.yml", true)
            PluginManager.logger.warning(
                "Отсутствует файл с конфигурацией плагина (${playlistFile.path}), был создан файл со стандартными значениями!",
            )
        }

        getConfig()
    }

    fun getConfig(): Config {
        try {
            if (configReloader == null) {
                configReloader = ReloadableConfig(configLoader, Config::class).addWatcher(configWatcher)
            }
            return configReloader!!.getLatest()
        } catch (e: Exception) {
            PluginManager.logger.severe("Ошибка валидации конфига: ${e.message}")
            PluginManager.logger.warning("Использование стандартных значений из-за неверных значений в текущем конфиге.")
            configReloader = null
            return Config.default
        }
    }

    fun loadPlaylist(): Set<Music>? {
        if (!playlistFile.exists()) {
            PluginManager.logger.severe(
                "Отсутствует файл со списком доступной музыки (${playlistFile.path}), пожалуйста, проверьте и перезагрузите плагин!",
            )
            return null
        }

        try {
            val playlistYaml = YamlConfiguration.loadConfiguration(playlistFile)
            val playlist =
                playlistYaml.getList("playlist")
                    ?: throw NullPointerException("Не найден ключ tracks в файле с доступной музыкой")

            // PluginManager.logger.info(playlist.toString())
            PluginManager.logger.info("Список доступной музыки успешно загружен!")

            return playlist.toSet() as Set<Music>
        } catch (e: Exception) {
            PluginManager.logger.severe("Не удалось загрузить список доступной музыки: ${e.message}")
            return null
        }
    }

    fun savePlaylist() {
        // val playlistYaml = YamlConfiguration()
        // playlistYaml.set(
        //     "tracks",
        //     listOf(Track("minecraft.test", 20, "Author", "Title")),
        // )
        //
        // try {
        //     playlistYaml.save(playlistFile)
        // } catch (e: IOException) {
        //     PluginManager.logger.severe("Не удалось сохранить плейлист: ${e.message}")
        // }
    }

    fun loadMusicZones(): MutableMap<String, Zone>? {
        val musicZonesFile = File(PluginManager.dataFolder, "zones.yml")

        if (!musicZonesFile.exists()) {
            val musicZonesYaml = YamlConfiguration()
            musicZonesYaml.set("zones", emptyMap<String, Zone>())

            try {
                musicZonesYaml.save(ConfigManager.musicZonesFile)
            } catch (e: IOException) {
                PluginManager.logger.severe("Ошибка при сохранении пустого списка зон: ${e.message}")
                return null
            }

            PluginManager.logger.warning(
                "Отсутствует файл со списком зон (${musicZonesFile.path}), был создан файл с пустым списком.",
            )
            return null
        }

        try {
            val musicZoneYaml = YamlConfiguration.loadConfiguration(musicZonesFile)
            val musicZones = musicZoneYaml.getConfigurationSection("zones")?.getValues(false)

            // PluginManager.logger.info(musicZones.toString())
            PluginManager.logger.info("Список зон для проигрывания музыки успешно загружен!")

            return musicZones as HashMap<String, Zone>
        } catch (e: Exception) {
            PluginManager.logger.severe("Не удалось загрузить список зон для проигрывания музыки: ${e.message}")
            return null
        }
    }

    fun saveMusicZones(zones: Map<String, Zone>) {
        val musicZonesYaml = YamlConfiguration()
        musicZonesYaml.set("zones", zones)

        try {
            musicZonesYaml.save(musicZonesFile)
        } catch (e: IOException) {
            PluginManager.logger.severe("Ошибка при сохранении списка зон: ${e.message}")
        }
    }

    fun loadPlayersSettings(): MutableMap<String, Boolean>? {
        val playersSettingsFile = File(PluginManager.dataFolder, "players_settings.yml")

        if (!playersSettingsFile.exists()) {
            val playersSettingsYaml = YamlConfiguration()
            playersSettingsYaml.set("players_settings", emptyMap<String, Boolean>())

            try {
                playersSettingsYaml.save(ConfigManager.playersSettingsFile)
            } catch (e: IOException) {
                PluginManager.logger.severe("Ошибка при сохранении пустого списка настроек игроков: ${e.message}")
                return null
            }

            PluginManager.logger.warning(
                "Отсутствует файл со списком настроек игроков (${playersSettingsFile.path}), был создан файл с пустым списком.",
            )
            return null
        }

        try {
            val playersSettingsYaml = YamlConfiguration.loadConfiguration(playersSettingsFile)
            val playersSettings = playersSettingsYaml.getConfigurationSection("players_settings")?.getValues(false)

            PluginManager.logger.info("Список настроек игроков успешно загружен!")

            return playersSettings as HashMap<String, Boolean>?
        } catch (e: Exception) {
            PluginManager.logger.severe("Не удалось загрузить список настроек игроков: ${e.message}")
            return null
        }
    }

    fun savePlayerSettings(players_settings: Map<String, Boolean>) {
        val playersSettingsYaml = YamlConfiguration()
        playersSettingsYaml.set("players_settings", players_settings)

        try {
            playersSettingsYaml.save(playersSettingsFile)
        } catch (e: IOException) {
            PluginManager.logger.severe("Ошибка при сохранении настроек игроков: ${e.message}")
        }
    }
}
