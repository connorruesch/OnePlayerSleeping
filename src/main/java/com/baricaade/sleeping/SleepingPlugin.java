package com.baricaade.sleeping;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple one player sleeping plugin for 1.16.5 servers.
 *
 * @author Baricaade (Connor)
 */
public class SleepingPlugin extends JavaPlugin implements Listener {
    /** A map of the different sleep tasks, key being the world UUID */
    private final Map<UUID, BukkitTask> tasks = new HashMap<>();

    @Override
    public void onEnable() {
        // load configuration
        saveDefaultConfig();

        // register event listeners
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        if(!e.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) {
            return;
        }

        if(Bukkit.getOnlinePlayers().size() <= 1) {
            return; // there's only one player online, do nothing
        }

        World world = e.getBed().getWorld();
        Player player = e.getPlayer();

        this.tasks.putIfAbsent(world.getUID(), Bukkit.getScheduler().runTaskLater(this, () -> {
            if(!isDay(world)) {
                world.setTime(0L);
            }
            world.setStorm(false);
            this.tasks.remove(world.getUID());
        }, 80L));

        // broadcast the sleep message to players in the world & log in console
        getLogger().info("Player " + player.getName() + " is sleeping in " + world.getName() + ".");
        world.getPlayers().forEach(p -> p.sendMessage(color(getConfig().getString("sleep-message").replace("{player}", player.getName()))));
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent e) {
        if(Bukkit.getOnlinePlayers().size() <= 1) {
            return; // there's only one player online, do nothing
        }

        World world = e.getBed().getWorld();
        Player player = e.getPlayer();
        BukkitTask task = this.tasks.get(world.getUID());

        if(task == null) {
            return;
        }

        this.tasks.remove(world.getUID());
        task.cancel();

        // only broadcast if it is nighttime or if there is an ongoing storm
        if(!isDay(world) || !world.isClearWeather()) {
            world.getPlayers().forEach(p -> p.sendMessage(color(getConfig().getString("sleep-cancel-message").replace("{player}", player.getName()))));
        }
    }

    /**
     * Colorizes the given string using color codes.
     *
     * @param string the string to colorize
     * @return the colorized string
     */
    private static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Checks if it is currently daytime in the given world.
     *
     * @param world the world to check
     * @return if it is daytime or not
     */
    private static boolean isDay(World world) {
        return world.getTime() < 12300 || world.getTime() > 23850;
    }
}
