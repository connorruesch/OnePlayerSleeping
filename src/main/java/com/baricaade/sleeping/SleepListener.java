package com.baricaade.sleeping;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class SleepListener implements Listener {
    private final SleepingPlugin plugin;

    public SleepListener(SleepingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBedEnterEvent(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();

        if(event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            World world = Bukkit.getWorld("world");

            if(world == null) {
                player.sendMessage(ChatColor.RED + "You are not in the proper world for one-player sleeping!");
                return; // not proper world, exit task
            }

            world.setTime(0);
            Bukkit.broadcastMessage(ChatColor.GREEN + "Player " + player.getName() + " has slept in a bed!");
        }, 30L);
    }

}
