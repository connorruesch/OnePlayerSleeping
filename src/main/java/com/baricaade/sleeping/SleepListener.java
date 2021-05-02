package com.baricaade.sleeping;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class SleepListener implements Listener {
    private final SleepingPlugin plugin;

    /** A map of worlds with players sleeping in them, and their respective sleep tasks */
    private final Map<String, BukkitTask> sleepingWorlds = new HashMap<>();

    public SleepListener(SleepingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if(!event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) {
            return;
        }

        Player player = event.getPlayer();
        World world = event.getBed().getWorld();

        if(this.sleepingWorlds.containsKey(world.getName())) {
            event.setCancelled(true);
            return; // someone is already sleeping in this world, cancel
        }

        // schedule sleep task
        BukkitTask task = Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            world.setTime(0);
            Bukkit.broadcastMessage(ChatColor.GREEN + "Player " + player.getName() + " has slept in a bed in world \"" + world.getName() + "\"!");
            this.sleepingWorlds.remove(world.getName());
        }, 50L);

        // add the task & world name to sleeping worlds
        this.sleepingWorlds.put(world.getName(), task);
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        String worldName = event.getBed().getWorld().getName();

        // Cancel task and remove from sleeping worlds
        this.sleepingWorlds.get(worldName).cancel();
        this.sleepingWorlds.remove(worldName);
    }
}
