package ru.joutak.lobby.music.commands.music

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.joutak.lobby.music.commands.PluginCommand
import ru.joutak.lobby.music.players_settings.PlayersSettingsManager

object OffCommand : PluginCommand("off", emptyList(), "", null) {
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
        turnMusicOff(sender)
        return true

    }

    fun turnMusicOff(sender: Player) {
        PlayersSettingsManager.musicOff(sender)
        sender.sendMessage(
            LinearComponents.linear(
                Component.text("Музыка выключена")
            ),
        )
    }

    override fun tabComplete(
        sender: CommandSender,
        args: Array<out String>
    ): List<String> {
        return emptyList()
    }
}