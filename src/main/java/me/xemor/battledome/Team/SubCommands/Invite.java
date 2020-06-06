package me.xemor.battledome.Team.SubCommands;

import me.xemor.battledome.Team.Team;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Invite implements SubCommand {

    private TeamHandler teamHandler;
    public Invite(TeamHandler teamHandler) {
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
                commandSender.sendMessage("This player does not exist.");
                return;
            }
            Player sender = (Player) commandSender;
            Team team = teamHandler.getTeam(sender);
            if (team == null) {
                team = teamHandler.createTeam(sender);
            }
            if (team.getMembers().size() >= 3) {
                commandSender.sendMessage("Your party is full!");
                return;
            }
            if (team.getMembers().contains(player)) {
                commandSender.sendMessage("They're already in your party!");
                return;
            }
            commandSender.sendMessage("You have invited " + player.getName() + " successfully!");
            teamHandler.invitePlayer(player, team);
        }
        else {
            commandSender.sendMessage("You must be a player.");
        }
    }
}
