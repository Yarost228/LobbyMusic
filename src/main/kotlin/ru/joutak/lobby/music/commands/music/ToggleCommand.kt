package ru.joutak.lobby.music.commands.music

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.joutak.lobby.music.commands.PluginCommand
import ru.joutak.lobby.music.players_settings.PlayersSettingsManager

object ToggleCommand : PluginCommand("toggle", emptyList(), "", null) {
    override fun execute(
        sender: CommandSender,
        args: Array<out String>
    ): Boolean {
        if (args.size != this.args.size) {
            return false
        }
        if (sender !is Player) {
            sender.sendMessage(Component.text("Данную команду можно использовать только в игре!", NamedTextColor.RED))
            return true
        }
        if (!PlayersSettingsManager.canHearMusic(sender)) {
            OnCommand.turnMusicOn(sender)
        }
        else {
            OffCommand.turnMusicOff(sender)
        }

        return true
    }

    override fun tabComplete(
        sender: CommandSender,
        args: Array<out String>
    ): List<String> {
        return emptyList()
    }
}