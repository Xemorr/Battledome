package me.xemor.battledome.Team.SubCommands;

import me.xemor.battledome.Team.Team;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Kick implements SubCommand {
    private TeamHandler teamHandler;
    public Kick(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    @Override
    public void run(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) {
            if (args.length < 2) {
                commandSender.sendMessage("You need to specify a player!");
                return;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                commandSender.sendMessage("This player is offline.");
                return;
            }
            Player sender = (Player) commandSender;
            Team team = teamHandler.getTeam(sender);
            if (team == null) {
                team = teamHandler.createTeam(sender);
            }
            if (!sender.getUniqueId().equals(team.getTeamLeader())) {
                commandSender.sendMessage("You're not the leader!");
                return;
            }
            if (player.getUniqueId().equals(team.getTeamLeader())) {
                commandSender.sendMessage("You cannot kick the leader!");
                return;
            }
            if (team.getMembers().isEmpty()) {
                commandSender.sendMessage("Your party is empty, you can't kick anyone!");
                return;
            }
            if (team.getMembers().contains(player.getUniqueId())) {
                commandSender.sendMessage("You have kicked " + player.getName() + " successfully!");
                teamHandler.removePlayer(player, team);
                player.sendMessage("You have been kicked from " + Bukkit.getPlayer(team.getTeamLeader()).getName() + "'s Party");
                return;
            }
            else {
                commandSender.sendMessage("They are not in your party!");
                return;
            }

        }
        else {
            commandSender.sendMessage("You must be a player.");
        }
    }
}
