package me.xemor.battledome;

import me.xemor.battledome.Game.Command.StartCMD;
import me.xemor.battledome.Game.GameHandler;
import me.xemor.battledome.Team.TeamCMD;
import me.xemor.battledome.Team.TeamChat;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Battledome extends JavaPlugin {

    private static TeamHandler teamHandler;
    private static GameHandler gameHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        teamHandler = new TeamHandler(this);
        this.getServer().getPluginManager().registerEvents(teamHandler, this);
        PluginCommand team = this.getCommand("team");
        TeamChat teamChat = new TeamChat(teamHandler);
        this.getServer().getPluginManager().registerEvents(teamChat, this);
        TeamCMD teamCMD = new TeamCMD(teamHandler, teamChat);
        team.setExecutor(teamCMD);
        team.setTabCompleter(teamCMD);
        GameHandler gameHandler = new GameHandler(teamHandler);
        this.getServer().getPluginManager().registerEvents(gameHandler, this);
        StartCMD start = new StartCMD(gameHandler);
        PluginCommand startCMD = this.getCommand("start");
        startCMD.setExecutor(start);
    }

    public static TeamHandler getTeamHandler() {
        return teamHandler;
    }

    public static GameHandler getGameHandler() {
        return gameHandler;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
