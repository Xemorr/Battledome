package me.xemor.battledome.Game.Command;

import me.xemor.battledome.Game.GameHandler;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

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
            gameHandler.start();
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
