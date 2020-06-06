package me.xemor.battledome.Game;

import me.xemor.battledome.Battledome;
import me.xemor.battledome.Team.Team;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GameHandler implements Listener {

    WorldBorder worldBorder;
    World world;
    long startTime;
    boolean gracePeriod;
    boolean deathmatch;
    List<Team> teams;
    TeamHandler teamHandler;
    int alivePlayers;
    boolean gameStarted = false;

    String deathmatchTitle = ChatColor.translateAlternateColorCodes('&', "&8&lDeathmatch");
    String deathmatchDescription = "Good Luck!";

    String gracePeriodTitle = ChatColor.translateAlternateColorCodes('&', "&8&lGrace Period Over");
    String gracePeriodDescription = "Death is now permanent!";

    public GameHandler(TeamHandler teamHandler) {
        this.teams = teamHandler.getTeams();
        this.teamHandler = teamHandler;
        world = Bukkit.getWorlds().get(0);
        worldBorder = world.getWorldBorder();
    }

    public void start() {
        groupTeams();
        worldBorder.setSize(1500);
        worldBorder.setCenter(0, 0);
        worldBorder.setDamageAmount(4.0);
        worldBorder.setDamageBuffer(0);
        worldBorder.setWarningDistance(30);
        startTime = System.currentTimeMillis();
        gracePeriod = true;
        deathmatch = false;
        alivePlayers = Bukkit.getOnlinePlayers().size();
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        gameStarted = true;
        Random random = new Random();
        this.teams = teamHandler.getTeams();
        for (Team team : teams) {
            int x = random.nextInt(1500) - 750;
            int z = random.nextInt(1500) - 750;
            Block block = world.getHighestBlockAt(x, z);
            Player leader = Bukkit.getPlayer(team.getTeamLeader());
            Location teleLocation = block.getLocation().add(0, 1, 0);
            if (leader != null) {
                leader.getInventory().addItem(new ItemStack(Material.NETHER_STAR, 3 - team.getMembers().size()));
                leader.teleport(teleLocation);
            }
            for (UUID uuid : team.getMembers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.teleport(block.getLocation().add(0, 1, 0));
                }
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis() - startTime;
                if (currentTime >= (10 * (60 * 1000)) && gracePeriod) {
                    worldBorder.setSize(50, (40 * 60));
                    gracePeriod = false;
                    world.setGameRule(GameRule.KEEP_INVENTORY, false);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle(gracePeriodTitle, gracePeriodDescription, 10, 60, 10);
                    }
                }
                if (currentTime >= (55 * (60 * 1000)) && (!deathmatch)) {
                    Block block = world.getHighestBlockAt(0, 0);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.teleport(block.getLocation());
                        player.sendTitle(deathmatchTitle, deathmatchDescription, 10, 60, 10);
                    }
                }
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Battledome.class), 0L, 20L);
    }

    public void groupTeams() {
        List<Team> teams = teamHandler.getTeams();
        List<Team> leaderOnly = new ArrayList<>();
        List<Team> oneMember = new ArrayList<>();
        for (Team team : teams) {
            if (team.getMembers().size() == 1) {
                oneMember.add(team);
            }
            else if (team.getMembers().isEmpty()) {
                leaderOnly.add(team);
            }
        }
        for (Team team : oneMember) {
            if (!leaderOnly.isEmpty()) {
                Team leaderOnlyTeam = leaderOnly.remove(0);
                teamHandler.addPlayer(leaderOnlyTeam.getTeamLeader(), team);
            }
        }
        while (leaderOnly.size() >= 2) {
            Team team = leaderOnly.remove(0);
            Team team2 = leaderOnly.remove(0);
            teamHandler.addPlayer(team2.getTeamLeader(), team);
            if (leaderOnly.size() >= 1) {
                Team team3 = leaderOnly.remove(0);
                teamHandler.addPlayer(team3.getTeamLeader(), team);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = e.getPlayer();
                Team team = teamHandler.getTeam(player);
                if (gracePeriod) {
                    if (player.getUniqueId().equals(team.getTeamLeader())) {
                        if (!team.getMembers().isEmpty()) {
                            Player otherPlayer = Bukkit.getPlayer(team.getMembers().get(0));
                            if (otherPlayer != null) {
                                e.getPlayer().teleport(otherPlayer);
                            }
                        }
                    }
                    else {
                        Player teamLeader = Bukkit.getPlayer(team.getTeamLeader());
                        if (teamLeader != null) {
                            player.teleport(teamLeader);
                        }
                    }
                }
                else {
                    Player leader = Bukkit.getPlayer(team.getTeamLeader());
                    if (leader == null || player.equals(leader)) {
                        player.setGameMode(GameMode.SPECTATOR);
                        return;
                    }
                    alivePlayers--;
                    player.teleport(leader);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
        }.runTaskLater(JavaPlugin.getPlugin(Battledome.class), 5L);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && gracePeriod || !gameStarted) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR && gameStarted) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (gameStarted) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL && gameStarted && !gracePeriod) {
            alivePlayers--;
            e.getPlayer().setHealth(0);
            Bukkit.broadcastMessage(e.getPlayer().getName() + " has disconnected, and died!");
        }
    }
}
