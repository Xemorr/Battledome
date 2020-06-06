package me.xemor.battledome.Team;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {

    List<UUID> members = new ArrayList<>();

    public Team(UUID leader) {
        members.add(leader);
    }

    public boolean hasPlayer(Player player) {
        return player.getUniqueId().equals(members)
                || members.contains(player.getUniqueId());
    }

    protected void addPlayer(UUID player) {
        members.add(player);
    }

    protected void removePlayer(UUID player) {
        members.remove(player);
    }

    public UUID getTeamLeader() {
        return members.get(0);
    }

    public List<UUID> getMembers() {
        return members;
    }
}
