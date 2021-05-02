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
import java.util.UUID;

public class SleepListener implements Listener {
    private final SleepingPlugin plugin;

    /** A list of worlds with players sleeping in them & their tasks */
    private final Map<UUID, BukkitTask> sleepTasks = new HashMap<>();

    public SleepListener(SleepingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        if(!e.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) {
            return;
        }

        World world = e.getBed().getWorld();

        this.sleepTasks.computeIfAbsent(world.getUID(), id -> Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            world.setTime(0);
            Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "A player has slept in bed! It is now morning.");
            this.sleepTasks.remove(world.getUID());
        }, 60L));
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent e) {
        World world = e.getBed().getWorld();
        BukkitTask task = this.sleepTasks.get(world.getUID());

        if(task == null) {
            return;
        }

        // cancel the sleep task if player leaves their bed
        task.cancel();
        this.sleepTasks.remove(world.getUID());
        Bukkit.broadcastMessage(ChatColor.RED + "A player has left their bed! The sleep has been canceled.");
    }
}
