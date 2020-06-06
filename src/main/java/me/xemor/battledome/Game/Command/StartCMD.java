package me.xemor.battledome.Game.Command;

import me.xemor.battledome.Game.GameHandler;
import me.xemor.battledome.Team.Team;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class StartCMD implements CommandExecutor, TabExecutor {

    private TeamHandler teamHandler;
    private GameHandler gameHandler;
    public StartCMD(TeamHandler teamHandler, GameHandler gameHandler) {
        this.teamHandler = teamHandler;
        this.gameHandler = gameHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("battledome.start")) {
            groupTeams();
            gameHandler.start();
        }
        return true;
    }

    public void groupTeams() {
        List<Team> teams = teamHandler.getTeams();
        List<Team> leaderOnly = new ArrayList<>();
        List<Team> oneMember = new ArrayList<>();
        for (Team team : teams) {
            if (team.getMembers().size() == 1) {
                oneMember.add(team);
            }
            else if (team.getMembers().isEmpty()) {
                leaderOnly.add(team);
            }
        }
        for (Team team : oneMember) {
            if (!leaderOnly.isEmpty()) {
                Team leaderOnlyTeam = leaderOnly.remove(0);
                teamHandler.addPlayer(leaderOnlyTeam.getTeamLeader(), team);
            }
        }
        while (leaderOnly.size() >= 2) {
            Team team = leaderOnly.remove(0);
            Team team2 = leaderOnly.remove(0);
            teamHandler.addPlayer(team2.getTeamLeader(), team);
            if (leaderOnly.size() >= 1) {
                Team team3 = leaderOnly.remove(0);
                teamHandler.addPlayer(team3.getTeamLeader(), team);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
