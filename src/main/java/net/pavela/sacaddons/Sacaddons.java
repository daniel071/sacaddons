package net.pavela.sacaddons;

import jdk.internal.misc.Unsafe;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public final class Sacaddons extends JavaPlugin {
    FileConfiguration config = this.getConfig();

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        config.addDefault("flagmsg", true);
        config.addDefault("replayhook", true);
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new saclistener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
