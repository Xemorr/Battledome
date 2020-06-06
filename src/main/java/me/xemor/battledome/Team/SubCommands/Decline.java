package me.xemor.battledome.Team.SubCommands;

import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Decline implements SubCommand {
    private TeamHandler teamHandler;
    public Decline(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    @Override
    public void run(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            teamHandler.removeInvite(sender);
            sender.sendMessage("You have declined your invites");
        }
        else {
            commandSender.sendMessage("You must be a player.");
        }
    }
}
