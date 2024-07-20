package me.xemor.battledome.Game;

import io.papermc.paper.entity.TeleportFlag;
import me.xemor.battledome.Battledome;
import me.xemor.battledome.Team.TeamHandler;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GameHandler implements Listener {

    private WorldBorder worldBorder;
    private World world;
    private int numberOfPlayers;
    private boolean gracePeriod = false;

    private final TeamHandler teamHandler;
    private boolean gameStarted = false;
    private HashMap<UUID, Location> startLocations = new HashMap<>();

    public GameHandler(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    public void start(int graceSeconds) {
        //WorldCreator worldCreator = new WorldCreator("battledome" + ThreadLocalRandom.current().nextInt(10000));
        //FourCorners fourCorners = new FourCorners();
        //worldCreator.biomeProvider(fourCorners);
        //world = worldCreator.createWorld();
        world = Bukkit.getWorld("world");
        numberOfPlayers = Bukkit.getOnlinePlayers().size();
        gameStarted = true;
        gracePeriod = true;
        int diameter = (int) Math.round(361 * Math.sqrt(numberOfPlayers));
        configureStartingWorldBorder(diameter);
        world.setTime(0);
        int spawningRadius = (int) Math.round((diameter / 2D) * 0.95);
        double currentRadian = 0;
        double radianIncrement = (2 * Math.PI) / numberOfPlayers;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatColor.GREEN + "GRACE PERIOD", "Fighting others will result in a ban", 40, 100, 40);
            player.sendMessage(ChatColor.GREEN + "GRACE PERIOD: Fighting others will result in a ban");
            giveItems(player);
            giveEffects(player, graceSeconds);
            Location location = new Location(world, spawningRadius * Math.sin(currentRadian), 120, spawningRadius * Math.cos(currentRadian));
            startLocations.put(player.getUniqueId(), location);
            player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            currentRadian += radianIncrement;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                handlePostGracePeriod();
            }
        }.runTaskLater(JavaPlugin.getPlugin(Battledome.class), graceSeconds * 20L);
    }

    public void configureStartingWorldBorder(int diameter) {
        worldBorder = world.getWorldBorder();
        worldBorder.setSize(diameter);
        worldBorder.setCenter(0, 0);
        worldBorder.setDamageAmount(1.0);
        worldBorder.setDamageBuffer(0);
        worldBorder.setWarningDistance(30);
    }

    public void giveItems(Player player) {
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 6));
        player.getInventory().addItem(new ItemStack(Material.NETHER_STAR, 4));
        ItemStack stoneAxe = new ItemStack(Material.STONE_AXE, 1);
        stoneAxe.addEnchantment(Enchantment.EFFICIENCY, 1);
        stoneAxe.addEnchantment(Enchantment.UNBREAKING, 1);
        ItemStack stonePickaxe = new ItemStack(Material.STONE_PICKAXE, 1);
        stonePickaxe.addEnchantment(Enchantment.EFFICIENCY, 1);
        stonePickaxe.addEnchantment(Enchantment.UNBREAKING, 1);
        ItemStack stoneShovel = new ItemStack(Material.STONE_SHOVEL, 1);
        stoneShovel.addEnchantment(Enchantment.EFFICIENCY, 1);
        stoneShovel.addEnchantment(Enchantment.UNBREAKING, 1);
        player.getInventory().addItem(stoneAxe);
        player.getInventory().addItem(stonePickaxe);
        player.getInventory().addItem(stoneShovel);
    }

    public void giveEffects(Player player, int graceSeconds) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, graceSeconds * 20, 12));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, graceSeconds * 20, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60 * 20, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60 * 20, 5));
    }

    public void handlePostGracePeriod() {
        final long timeAtStart = System.currentTimeMillis();
        long seconds = 2100;
        worldBorder.setSize(25, seconds);
        gracePeriod = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatColor.RED + "GRACE PERIOD OVER", "You may fight other players!", 40, 100, 40);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                final long currentTimeMillis = System.currentTimeMillis();
                long playersAlive = Bukkit.getOnlinePlayers().stream().filter((player -> player.getGameMode() == GameMode.SURVIVAL)).count();
                long untilMillis = Math.round(Math.ceil(((timeAtStart + seconds * 1000) - currentTimeMillis) * (playersAlive / ((double) numberOfPlayers))));
                if (untilMillis > 0) {
                    worldBorder.setSize(25, untilMillis / 1000);
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
                }
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Battledome.class), 200 * 20, 200 * 20);

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            if (gracePeriod) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRespawn(PlayerRespawnEvent e) {
        if (!gracePeriod && gameStarted) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        else {
            Location start = startLocations.get(e.getPlayer().getUniqueId());
            if (start != null) {
                e.setRespawnLocation(start);
                e.getPlayer().teleport(start);
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player player && !gracePeriod && gameStarted) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        }
    }
}
