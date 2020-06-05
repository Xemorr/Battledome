package me.xemor.battledome.Team;

import me.xemor.battledome.Battledome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class TeamHandler {

    private HashMap<UUID, Team> uuidToTeam = new HashMap<>();
    private YamlConfiguration teamsFile;

    public TeamHandler(Battledome battledome) {
        try {
            File file = new File(battledome.getDataFolder(), "teams.yml");
            if (!file.exists()) {
                file.createNewFile();
            }
            teamsFile = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
        }
    }


    public Team getTeam(Player player) {
        return uuidToTeam.get(player.getUniqueId());
    }

    public Team createTeam(Player teamLeader) {
        Team team = new Team(teamLeader);
        uuidToTeam.put(teamLeader.getUniqueId(), team);
        return team;
    }

    public void addPlayer(Player player, Team team) {
        team.addPlayer(player);
        uuidToTeam.put(player.getUniqueId(), team);
    }

    public void removePlayer(Player player, Team team) {
        team.removePlayer(player);
        uuidToTeam.remove(player.getUniqueId(), team);
    }

    public void saveTeam(Team team) {
        ConfigurationSection configurationSection = teamsFile.getConfigurationSection(team.getTeamLeader().toString());
        if (configurationSection == null) {
            configurationSection = teamsFile.createSection(team.getTeamLeader().toString());
        }
        configurationSection.set("members", team.getMembers());
    }

}
