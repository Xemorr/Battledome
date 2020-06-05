package me.xemor.battledome.Team;

import org.bukkit.entity.Player;

import java.util.*;

public class Team {

    UUID teamLeader;
    List<UUID> members = new ArrayList<>();

    public Team(Player leader) {
        teamLeader = leader.getUniqueId();
    }

    public boolean hasPlayer(Player player) {
        return player.getUniqueId().equals(members)
                || members.contains(player.getUniqueId());
    }

    protected void addPlayer(Player player) {
        members.add(player.getUniqueId());
    }

    protected void removePlayer(Player player) {
        members.remove(player.getUniqueId());
    }

    public UUID getTeamLeader() {
        return teamLeader;
    }

    public final List<UUID> getMembers() {
        return members;
    }
}
