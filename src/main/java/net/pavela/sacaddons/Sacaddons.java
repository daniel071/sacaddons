package net.pavela.sacaddons;

import jdk.internal.misc.Unsafe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.File;


public final class Sacaddons extends JavaPlugin {
    FileConfiguration config = this.getConfig();

    @Override
    public void onEnable() {
        // Plugin startup logic

        File sacConfigFile = new File("plugins/SoaromaSAC/main.yml");
        FileConfiguration sacConfig = YamlConfiguration.loadConfiguration(sacConfigFile);

        saveDefaultConfig();
        config.addDefault("flagmsg", true);
        config.addDefault("replayhook", true);
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new saclistener(), this);

        // check if API is disabled - plugin will not work if it's disabled
        if (!sacConfig.getBoolean("other.enableAPI")) {
            String msg1 = "[!] " + ChatColor.YELLOW + "enableAPI for SoaromaSAC is disabled!";
            String msg2 = "[!] " + ChatColor.YELLOW + "sacaddons " + ChatColor.BOLD + "will not work!" + ChatColor.RESET;
            String msg3 = ChatColor.GRAY + "(edit main.yml for SoaromaSAC and change enableAPI to " + ChatColor.ITALIC + "true" + ChatColor.RESET + ChatColor.GRAY + ")";

            Bukkit.getConsoleSender().sendMessage(msg1);
            Bukkit.getConsoleSender().sendMessage(msg2);
            Bukkit.getConsoleSender().sendMessage(msg3);
            Bukkit.broadcastMessage(msg1);
            Bukkit.broadcastMessage(msg2);
            Bukkit.broadcastMessage(msg3);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
