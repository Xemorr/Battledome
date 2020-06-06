package me.xemor.battledome.Team;

import me.xemor.battledome.Battledome;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeamHandler implements Listener {

    private HashMap<UUID, Team> uuidToTeam = new HashMap<>();
    private HashMap<UUID, Team> invite = new HashMap<>();
    private List<Team> teams = new ArrayList<>();
    private YamlConfiguration teamsFile;
    private File file;

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e) {
        if (!hasTeam(e.getPlayer())) {
            createTeam(e.getPlayer());
        }
    }

    public TeamHandler(Battledome battledome) {
        try {
            file = new File(battledome.getDataFolder(), "teams.yml");
            if (!file.exists()) {
                file.createNewFile();
            }
            teamsFile = YamlConfiguration.loadConfiguration(file);
            Map<String, Object> values = teamsFile.getValues(false);
            for (Map.Entry<String, Object> value : values.entrySet()) {
                Team team = createTeam(UUID.fromString(value.getKey()));
                for (String strMember : ((ConfigurationSection) value.getValue()).getStringList("members")) {
                    addPlayer(UUID.fromString(strMember), team);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                saveTeams();
            }
        }.runTaskTimer(battledome, 300L, 300L);
    }

    public void invitePlayer(Player player, Team team) {
        player.sendMessage("Use /team accept to accept the invitation from " + Bukkit.getOfflinePlayer(team.getMembers().get(0)).getName());
        player.sendMessage("Use /team decline to get rid of the invitation.");
        if (invite.containsKey(player.getUniqueId())) {
            invite.replace(player.getUniqueId(), team);
        }
        else {
            invite.put(player.getUniqueId(), team);
        }
    }

    public void removeInvite(Player player) {
        invite.remove(player.getUniqueId());
    }

    public Team acceptInvite(Player player) {
        Team team = invite.get(player.getUniqueId());
        removeInvite(player);
        addPlayer(player.getUniqueId(), team);
        return team;
    }

    public Team getTeam(Player player) {
        return uuidToTeam.get(player.getUniqueId());
    }

    public boolean hasTeam(Player player) {
        return uuidToTeam.containsKey(player.getUniqueId());
    }

    public Team createTeam(UUID teamLeader) {
        Team team = new Team(teamLeader);
        uuidToTeam.put(teamLeader, team);
        teams.add(team);
        return team;
    }

    public Team createTeam(Player teamLeader) {
        return createTeam(teamLeader.getUniqueId());
    }

    public void addPlayer(Player player, Team team) {
        addPlayer(player.getUniqueId(), team);
    }

    public void addPlayer(UUID player, Team team) {
        team.addPlayer(player);
        if (team.getMembers().contains(player)) {
            return;
        }
        if (team.getMembers().size() == 3) {
            return;
        }
        Team removed = uuidToTeam.remove(player);
        if (removed != null) {
            if (removed.getMembers().get(0).equals(player)) {
                teams.remove(removed);
                for (UUID uuid : removed.getMembers()) {
                    removed.removePlayer(player);
                }
            }
            else {
                removed.removePlayer(player);
            }
        }
        uuidToTeam.put(player, team);
    }

    public void removePlayer(UUID player, Team team) {
        team.removePlayer(player);
        uuidToTeam.remove(player, team);
        createTeam(player);
    }

    public void removePlayer(Player player, Team team) {
        removePlayer(player.getUniqueId(), team);
    }

    public void saveTeam(Team team) {
        ConfigurationSection configurationSection;
        String key = team.getTeamLeader().toString();
        if (teamsFile.isConfigurationSection(key)) {
            configurationSection = teamsFile.getConfigurationSection(key);
        }
        else {
            configurationSection = teamsFile.createSection(key);
        }
        ArrayList<String> strUUIDs = new ArrayList<>();
        for (UUID uuid : team.getMembers()) {
            strUUIDs.add(uuid.toString());
        }
        configurationSection.set("members", strUUIDs);
        try {
            teamsFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTeams() {
        for (Team team : teams) {
            saveTeam(team);
        }
    }

    public List<Team> getTeams() {
        return teams;
    }

}
