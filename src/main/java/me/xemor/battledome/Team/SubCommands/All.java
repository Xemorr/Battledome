package me.xemor.battledome.Team.SubCommands;

import me.xemor.battledome.Battledome;
import me.xemor.battledome.Team.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class All implements SubCommand {
    @Override
    public void run(CommandSender commandSender, String[] args) {
        List<Team> teams = Battledome.getTeamHandler().getTeams();
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            commandSender.sendMessage("---- Team " + i + " -----");
            for (UUID uuid : team.getMembers()) {
                commandSender.sendMessage(Bukkit.getOfflinePlayer(uuid).getName());
            }
        }
    }
}
