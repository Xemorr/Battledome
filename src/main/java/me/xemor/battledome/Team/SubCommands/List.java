package me.xemor.battledome.Team.SubCommands;

import me.xemor.battledome.Team.Team;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class List implements SubCommand {

    private TeamHandler teamHandler;
    public List(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    @Override
    public void run(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            Team team = teamHandler.getTeam(sender);
            if (team == null) {
                sender.sendMessage("You are not in a team!");
            }
            sender.sendMessage("Your team leader is: " + Bukkit.getOfflinePlayer(team.getTeamLeader()).getName());
            sender.sendMessage("Your members are: ");
            for (UUID uuid : team.getMembers()) {
                sender.sendMessage(Bukkit.getOfflinePlayer(uuid).getName());
            }
        }
        else {
            commandSender.sendMessage("You must be a player.");
        }
    }
}
