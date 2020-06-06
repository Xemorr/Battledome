package me.xemor.battledome.Game;

import me.xemor.battledome.Battledome;
import me.xemor.battledome.Team.Team;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
        worldBorder.setSize(1250);
        worldBorder.setCenter(0, 0);
        worldBorder.setDamageAmount(1.0);
        worldBorder.setDamageBuffer(0);
        worldBorder.setWarningDistance(30);
        startTime = System.currentTimeMillis();
        gracePeriod = true;
        deathmatch = false;
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        gameStarted = true;
        Random random = new Random();
        this.teams = teamHandler.getTeams();
        for (Team team : teams) {
            int x = random.nextInt(1000) - 500;
            int z = random.nextInt(1000) - 500;
            Block block = world.getHighestBlockAt(x, z);
            Player leader = Bukkit.getPlayer(team.getTeamLeader());
            Location teleLocation = block.getLocation().add(0, 1, 0);
            if (leader != null) {
                leader.getInventory().addItem(new ItemStack(Material.NETHER_STAR, 4 - team.getMembers().size())); //compensation
            }
            for (UUID uuid : team.getMembers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.teleport(teleLocation);
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
/*
    public void groupTeams() {
        List<Team> teams = teamHandler.getTeams();
        List<Team> oneMember = new ArrayList<>();
        List<Team> twoMembers = new ArrayList<>();
        for (Team team : teams) {
            if (team.getMembers().size() == 2) {
                twoMembers.add(team);
            }
            else if (team.getMembers().size() == 1) {
                oneMember.add(team);
            }
        }
        for (Team team : twoMembers) {
            if (!oneMember.isEmpty()) {
                Team leaderOnlyTeam = oneMember.remove(0);
                teamHandler.addPlayer(leaderOnlyTeam.getTeamLeader(), team);
            }
        }
        while (oneMember.size() >= 2) {
            Team team = oneMember.remove(0);
            Team team2 = oneMember.remove(0);
            teamHandler.addPlayer(team2.getTeamLeader(), team);
            teamHandler.removePlayer(team2.getTeamLeader(), team2);
            if (oneMember.size() >= 1) {
                Team team3 = oneMember.remove(0);
                teamHandler.addPlayer(team3.getTeamLeader(), team);
                teamHandler.removePlayer(team3.getTeamLeader(), team3);
            }
        }
    }
*/
    @EventHandler
    public void onDeath(PlayerRespawnEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = e.getPlayer();
                Team team = teamHandler.getTeam(player);
                for (UUID uuid : team.getMembers()) {
                    Player otherPlayer = Bukkit.getPlayer(uuid);
                    if (otherPlayer != null && !otherPlayer.equals(player)) {
                        e.getPlayer().teleport(otherPlayer);
                        break;
                    }
                }
                if (!gracePeriod) {
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
        }.runTaskLater(JavaPlugin.getPlugin(Battledome.class), 5L);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && (gracePeriod || !gameStarted)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR && gameStarted) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW) //ensures it runs before the other join event inside teamHandler
    public void join(PlayerJoinEvent e) {
        Team team = teamHandler.getTeam(e.getPlayer());
        Player player = e.getPlayer();
        if (team == null || (gameStarted && !gracePeriod)) { //set them to spectator if the game's already started and this is first time running
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        else if (team != null && gameStarted) { //if they did already have a team and the grace period is over.
            for (UUID uuid : team.getMembers()) {
                Player otherPlayer = Bukkit.getPlayer(uuid);
                if (otherPlayer != null && !player.equals(otherPlayer)) {
                    player.teleport(otherPlayer);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL && gameStarted && !gracePeriod) {
            e.getPlayer().setHealth(0);
            Bukkit.broadcastMessage(e.getPlayer().getName() + " has disconnected, and died!");
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }
}
