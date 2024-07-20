package me.xemor.battledome.Team;

import me.xemor.battledome.Team.SubCommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamCMD implements CommandExecutor, TabExecutor {

    private TeamHandler teamHandler;
    public TeamCMD(TeamHandler teamHandler, TeamChat teamChat) {
        this.teamHandler = teamHandler;
        subcommands.put("accept", new Accept(teamHandler));
        subcommands.put("invite", new Invite(teamHandler));
        subcommands.put("decline", new Decline(teamHandler));
        subcommands.put("list", new me.xemor.battledome.Team.SubCommands.List(teamHandler));
        subcommands.put("kick", new Kick(teamHandler));
        subcommands.put("leave", new Leave(teamHandler));
        subcommands.put("chat", new ChatToggle(teamHandler, teamChat));
        subcommands.put("all", new All());
    }

    private HashMap<String, SubCommand> subcommands = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("You need to specify a subcommand!");
        }
        SubCommand subCommand = subcommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage("That subcommand does not exist!");
            sender.sendMessage("The possible commands are: ");
            for (String str : subcommands.keySet()) {
                sender.sendMessage(str);
            }
        }
        subCommand.run(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabComplete = new ArrayList<>();
        if (args.length == 1) {
            for (String subCommand : subcommands.keySet()) {
                if (subCommand.startsWith(args[0])) {
                    tabComplete.add(subCommand);
                }
            }
        }
        return tabComplete;
    }

}
