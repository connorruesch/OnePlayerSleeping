package com.baricaade.sleeping;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class SleepListener implements Listener {
    private final SleepingPlugin plugin;

    /** A list of worlds with players sleeping in them & their tasks */
    private final Map<String, BukkitTask> sleepingWorlds = new HashMap<>();

    public SleepListener(SleepingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        if(!e.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) {
            return;
        }

        World world = e.getBed().getWorld();

        this.sleepingWorlds.computeIfAbsent(world.getName(), id -> Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            world.setTime(0);
            Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "A player has slept in bed! It is now morning.");
            this.sleepingWorlds.remove(world.getName());
        }, 60L));
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent e) {
        World world = e.getBed().getWorld();
        BukkitTask task = this.sleepingWorlds.get(world.getName());

        if(task == null) {
            return;
        }

        // cancel the sleep task if player leaves their bed
        task.cancel();
    }
}
