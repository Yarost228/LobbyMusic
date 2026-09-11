package ru.joutak.lobby.music.commands.music

import ru.joutak.lobby.music.commands.PluginCommandExecutor

object MusicCommandExecutor : PluginCommandExecutor("music") {
    init {
        registerCommand(ListCommand)
        registerCommand(OnCommand)
        registerCommand(OffCommand)
        registerCommand(ToggleCommand)
    }
}
