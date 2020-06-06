package me.xemor.battledome.Team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.UUID;

public class TeamChat implements Listener {

    private HashSet<UUID> teamChatOn = new HashSet<>();
    private TeamHandler teamHandler;
    private String teamChatFormat = ChatColor.translateAlternateColorCodes('&', "&8&l[&e&l%s&8&l]&7 ");

    public TeamChat(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (teamChatOn.contains(e.getPlayer().getUniqueId())) {
            Team team = teamHandler.getTeam(e.getPlayer());
            if (team == null) {
                return;
            }
            sendMessage(team, e.getPlayer(), e.getMessage());
            e.setCancelled(true);
        }
    }

    public boolean isInTeamChat(UUID uuid) {
        return teamChatOn.contains(uuid);
    }

    public void setTeamChat(UUID uuid, boolean shouldTeamChat) {
        if (shouldTeamChat) {
            teamChatOn.add(uuid);
        }
        else {
            teamChatOn.remove(uuid);
        }
    }

    public void sendMessage(Team team, Player asPlayer, String message) {
        for (UUID uuid : team.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            player.sendMessage(String.format(teamChatFormat + message, asPlayer.getName()));
        }
    }
}
