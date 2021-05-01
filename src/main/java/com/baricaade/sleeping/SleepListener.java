package com.baricaade.sleeping;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.ArrayList;


public class SleepListener implements Listener {
    private final SleepingPlugin plugin;
    private final ArrayList<String> sleepingWorlds = new ArrayList<>();

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

        if(this.sleepingWorlds.contains(world.getName())) {
            event.setCancelled(true);
            return; // someone is already sleeping in this world, cancel
        }

        this.sleepingWorlds.add(world.getName());

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            world.setTime(0);
            Bukkit.broadcastMessage(ChatColor.GREEN + "Player " + player.getName() + "has slept in a bed in world " + world.getName() + "!");
            this.sleepingWorlds.remove(world.getName());
        }, 50L);
    }
}
