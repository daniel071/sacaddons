package net.pavela.sacaddons;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public final class Sacaddons extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // ReplayAPI.getInstance().registerReplaySaver(new IReplaySaver() {
        getServer().getPluginManager().registerEvents(new saclistener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
