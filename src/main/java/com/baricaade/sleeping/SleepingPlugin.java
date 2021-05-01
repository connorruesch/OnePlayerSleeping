package com.baricaade.sleeping;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SleepingPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new SleepListener(this), this);
    }

}
