package me.xemor.battledome;

import me.xemor.battledome.Game.Command.StartCMD;
import me.xemor.battledome.Game.GameHandler;
import me.xemor.battledome.Team.TeamCMD;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Battledome extends JavaPlugin {

    private TeamHandler teamHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        teamHandler = new TeamHandler(this);
        this.getServer().getPluginManager().registerEvents(teamHandler, this);
        PluginCommand team = this.getCommand("team");
        TeamCMD teamCMD = new TeamCMD(teamHandler);
        team.setExecutor(teamCMD);
        team.setTabCompleter(teamCMD);
        GameHandler gameHandler = new GameHandler(teamHandler);
        StartCMD start = new StartCMD(teamHandler, gameHandler);
        PluginCommand startCMD = this.getCommand("start");
        startCMD.setExecutor(start);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
