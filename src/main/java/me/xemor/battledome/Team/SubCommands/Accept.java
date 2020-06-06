package me.xemor.battledome.Team.SubCommands;

import me.xemor.battledome.Team.Team;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Accept implements SubCommand {

    private TeamHandler teamHandler;
    public Accept(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    @Override
    public void run(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            Team team = teamHandler.acceptInvite(sender);
            if (team != null) {
                sender.sendMessage("You have joined " + Bukkit.getOfflinePlayer(team.getTeamLeader()).getName());
            }
            else {
                sender.sendMessage("You haven't had any invites!");
            }
        }
        else {
            commandSender.sendMessage("You must be a player.");
        }
    }

}
