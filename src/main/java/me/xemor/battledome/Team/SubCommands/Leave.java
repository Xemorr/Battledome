package me.xemor.battledome.Team.SubCommands;

import me.xemor.battledome.Team.Team;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Leave implements SubCommand {

    private TeamHandler teamHandler;
    public Leave(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    @Override
    public void run(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            Team team = teamHandler.getTeam(sender);
            if (team == null) {
                team = teamHandler.createTeam(sender);
            }
            if (sender.getUniqueId().equals(team.getTeamLeader())) {
                for (UUID uuid : team.getMembers()) {
                    teamHandler.removePlayer(uuid, team);
                }
                sender.sendMessage("Cleaned your team out.");
                return;
            }
            else {
                teamHandler.removePlayer(sender, team);
                sender.sendMessage("You have successfully left your party!");
            }
        }
        else {
            commandSender.sendMessage("You must be a player.");
        }
    }

}
