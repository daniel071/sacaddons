package net.pavela.sacaddons;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class Sacaddons extends JavaPlugin {
    FileConfiguration config = this.getConfig();

    String msg1 = null;
    String msg2 = null;
    String msg3 = null;
    net.md_5.bungee.api.chat.TextComponent updateMsg = null;

    boolean apiEnabled = true;
    boolean updateRequired = false;


    @Override
    public void onEnable() {
        // Plugin startup logic

        String msg1 = "[!] " + ChatColor.YELLOW + "enableAPI for SoaromaSAC is disabled!";
        String msg2 = "[!] " + ChatColor.YELLOW + "sacaddons " + ChatColor.BOLD + "will not work!" + ChatColor.RESET;
        String msg3 = ChatColor.GRAY + "(edit main.yml for SoaromaSAC and change enableAPI to " + ChatColor.ITALIC + "true" + ChatColor.RESET + ChatColor.GRAY + ")";

        File sacConfigFile = new File("plugins/SoaromaSAC/main.yml");
        FileConfiguration sacConfig = YamlConfiguration.loadConfiguration(sacConfigFile);

        saveDefaultConfig();
        config.addDefault("flagmsg", true);
        config.addDefault("replayhook", true);
        config.addDefault("bstats", true);
        config.addDefault("flagsound.enabled", true);
        config.addDefault("flagsound.sound", "BLOCK_NOTE_BLOCK_PLING");
        config.addDefault("flagsound.volume", 1);
        config.addDefault("flagsound.pitch", 1);

        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new saclistener(), this);
        getServer().getPluginManager().registerEvents(new playerlistener(), this);

        // check if API is disabled - plugin will not work if it's disabled
        if (!sacConfig.getBoolean("other.enableAPI")) {
            apiEnabled = false;
            Bukkit.getConsoleSender().sendMessage(msg1);
            Bukkit.getConsoleSender().sendMessage(msg2);
            Bukkit.getConsoleSender().sendMessage(msg3);

        }

        new UpdateChecker(this, 95930).getVersion(version -> {
            if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                updateRequired = true;
                updateMsg = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "[!] " + ChatColor.RESET + ChatColor.GREEN + "New sacaddons update available" + ChatColor.GRAY + " (click for link)");
                String cliMsg = ChatColor.GREEN + "" + ChatColor.BOLD + "[!] " + ChatColor.RESET + ChatColor.GREEN + "New sacaddons update available";
                updateMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("https://www.spigotmc.org/resources/sacaddons.95930/")));
                updateMsg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/sacaddons.95930/"));
                Bukkit.getConsoleSender().sendMessage(cliMsg);
            }
        });

        if (config.getBoolean("bstats")) {
            int pluginId = 12689;
            Metrics metrics = new Metrics(this, pluginId);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public class playerlistener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player p = event.getPlayer();
            if(p.isOp()) {
                if (!apiEnabled) {
                    // referencing the variables did not work for some reason.. so I had to do this
                    p.sendMessage("");
                    p.sendMessage("[!] " + ChatColor.YELLOW + "enableAPI for SoaromaSAC is disabled!");
                    p.sendMessage("[!] " + ChatColor.YELLOW + "sacaddons " + ChatColor.BOLD + "will not work!" + ChatColor.RESET);
                    p.sendMessage(ChatColor.GRAY + "(edit main.yml for SoaromaSAC and change enableAPI to " + ChatColor.ITALIC + "true" + ChatColor.RESET + ChatColor.GRAY + ")");
                    p.sendMessage("");
                }
                if (updateRequired) {
                    p.spigot().sendMessage(updateMsg);
                }

            }
        }
    }

}
