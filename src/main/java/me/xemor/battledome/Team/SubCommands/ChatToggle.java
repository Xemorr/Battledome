package me.xemor.battledome.Team.SubCommands;

import me.xemor.battledome.Team.TeamChat;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatToggle implements SubCommand {

    private TeamHandler teamHandler;
    private TeamChat teamChat;
    public ChatToggle(TeamHandler teamHandler, TeamChat teamChat) {
        this.teamHandler = teamHandler;
        this.teamChat = teamChat;
    }

    @Override
    public void run(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            teamChat.setTeamChat(player.getUniqueId(), !teamChat.isInTeamChat(player.getUniqueId()));
            teamHandler.sendMessage(player, "Team Chat toggled to " + teamChat.isInTeamChat(player.getUniqueId()) + " successfully!");
        }
        else {
            commandSender.sendMessage("You are not a player!");
        }
    }
}
