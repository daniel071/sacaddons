package net.pavela.sacaddons;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class Sacaddons extends JavaPlugin {
    private Sacaddons instance = this;

    FileConfiguration config = this.getConfig();
    public irc irc;
    public Thread botThread;
    public String sacaddonsversion = "0.0.0";
    public String sacversion = "0.0.0";

    String msg1 = null;
    String msg2 = null;
    String msg3 = null;
    net.md_5.bungee.api.chat.TextComponent updateMsg = null;

    boolean apiEnabled = true;
    boolean updateRequired = false;

    public Sacaddons getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        sacaddonsversion = this.getDescription().getVersion();
        sacversion = Bukkit.getServer().getPluginManager().getPlugin("SoaromaSAC").getDescription().getVersion();

        String msg1 = ChatColor.translateAlternateColorCodes('§', config.getString("msg.enableapiDisabledText1"));
        String msg2 = ChatColor.translateAlternateColorCodes('§', config.getString("msg.enableapiDisabledText2"));
        String msg3 = ChatColor.translateAlternateColorCodes('§', config.getString("msg.enableapiDisabledText3"));

        File sacConfigFile = new File("plugins/SoaromaSAC/main.yml");
        FileConfiguration sacConfig = YamlConfiguration.loadConfiguration(sacConfigFile);

        // TODO: make config file better because yaml is special
        config.options().copyDefaults(true);
        saveDefaultConfig();

        new saclistener(this, this);

        if (config.getBoolean("irc.enabled")) {
            if (!config.getBoolean("irc.verbose")) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "::" + ChatColor.WHITE + " Verbose disabled, hiding IRC logs");
                ((Logger) LogManager.getRootLogger()).addFilter(new LogFilter());
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "::" + ChatColor.WHITE + " Verbose enabled, set irc.verbose in sacaddons config to disable.");
            }

            irc = new irc(this, false);
            botThread = new Thread(irc);
            botThread.setName("irchook");
            botThread.start();
            this.getCommand("irc").setExecutor(new CommandIRC(this));
        }


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
                updateMsg = new TextComponent( ChatColor.translateAlternateColorCodes('§', config.getString("msg.updatetext")));
                String cliMsg = ChatColor.GREEN + "" + ChatColor.BOLD + "[!]" + ChatColor.RESET + ChatColor.GREEN + " New sacaddons update available";
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
        if (config.getBoolean("irc.enabled")) {
            irc.shutdown();
        }
    }

    public class playerlistener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player p = event.getPlayer();
            if(p.isOp()) {
                if (!apiEnabled) {
                    // referencing the variables did not work for some reason.. so I had to do this
                    String msg1 = ChatColor.translateAlternateColorCodes('§', config.getString("msg.enableapiDisabledText1"));
                    String msg2 = ChatColor.translateAlternateColorCodes('§', config.getString("msg.enableapiDisabledText2"));
                    String msg3 = ChatColor.translateAlternateColorCodes('§', config.getString("msg.enableapiDisabledText3"));

                    p.sendMessage("");
                    p.sendMessage(msg1);
                    p.sendMessage(msg2);
                    p.sendMessage(msg3);
                    p.sendMessage("");
                }
                if (updateRequired) {
                    p.spigot().sendMessage(updateMsg);
                }

            }
        }
    }

}
