package ru.joutak.lobby.music.commands.zone

import ru.joutak.lobby.music.commands.PluginCommandExecutor

object ZoneCommandExecutor : PluginCommandExecutor("zone") {
    init {
        registerCommand(AddCommand)
        registerCommand(InfoCommand)
        registerCommand(ListCommand)
        registerCommand(PlayCommand)
        registerCommand(RemoveCommand)
        registerCommand(StopCommand)
        registerCommand(SkipCommand)
    }
}
