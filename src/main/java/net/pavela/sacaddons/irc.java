package net.pavela.sacaddons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.delay.StaticDelay;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;

public class irc extends ListenerAdapter implements Runnable {
    private Sacaddons instance;
    public static String sacaddonsversion = "0.0.0";
    public static String sacversion = "0.0.0";
    public static StaticDelay currentDelay;
    public static PircBotX bot;
    public static org.bukkit.configuration.Configuration config;


    public irc(Sacaddons instance, boolean startBot){
        // Bukkit.getConsoleSender().sendMessage(":: constructor loading");

        this.instance = instance;

        sacversion = instance.sacversion;
        sacaddonsversion = instance.sacaddonsversion;
        config = instance.config;

        if (instance.botThread != null) {
            if (instance.botThread.isAlive()) {
                try {
                    main(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public irc() {
        // Bukkit.getConsoleSender().sendMessage(":: constructor loading 2");
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.BOLD + "" + ChatColor.YELLOW + "[IRC] Got disconnected, retrying...");
//        try {
//            bot.startBot();
//        } catch(Exception e) {
//            Bukkit.getConsoleSender().sendMessage(ChatColor.BOLD + "" + ChatColor.YELLOW + "[IRC] IRC Exception ", String.valueOf(e));
//        }
    }

    @Override
    public void onConnect(ConnectEvent event){
        if (config.getBoolean("irc.nickserv.enabled")) {
            bot.sendIRC().identify(config.getString("irc.nickserv.password"));
        }
        bot.sendIRC().joinChannel(config.getString("irc.channel"));
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        // Bukkit.getConsoleSender().sendMessage(event.getMessage());

        if (event.getMessage().startsWith("?info")) {
            event.respond("----------------------------------------------");
            event.respond("Minecraft server with SoaromaSAC and sacaddons");
            event.respond(String.format("\u0002Minecraft version:\u000F %s %s", Bukkit.getVersion(), Bukkit.getBukkitVersion()));
            event.respond(String.format("\u0002SoaromaSAC version:\u000F %s", sacversion));
            event.respond(String.format("\u0002sacaddons version:\u000F %s", sacaddonsversion));
            event.respond("----------------------------------------------");
        }
        if (event.getMessage().startsWith("?help")) {
            event.respond("Type ?info to show system info. Flags and reports will be sent to this channel.");
        }
    }

    public void onFlag(String ImpostorName) {
        String msg = String.format("\u0002\u000304[!] MrRubberStruck\u000F\u000304 was flagged", ImpostorName);
        bot.sendIRC().message(config.getString("irc.channel"), msg);
    }

    public void onReport(String ImpostorName) {
        String msg = String.format("\u0002\u000308[!] %s \u000Fwas reported", ImpostorName);
        bot.sendIRC().message(config.getString("irc.channel"), msg);
    }

    public void runCommand(String command) {
        bot.sendRaw().rawLine(command);
    }

    public void shutdown() {
        bot.stopBotReconnect();
    }

    public static void main(Boolean startBot) throws Exception {
        //Configure what we want our bot to do
        if (startBot) {
            currentDelay = new StaticDelay(Long.parseLong(config.getString("irc.msgdelay")));

            Configuration configuration = new Configuration.Builder()
                    .setName(config.getString("irc.username")) // Set the nick of the bot.
                    .addServer(config.getString("irc.server")) // Join the Libera.chat network by default.
                    .addAutoJoinChannel(config.getString("irc.channel")) // Join the test channel.
                    .setMessageDelay(currentDelay) // half a second delay
                    .addListener(new irc()) // Add our listener that will be called on Events
                    .setAutoReconnect(true)
                    .buildConfiguration();

            //Create our bot with the configuration
            bot = new PircBotX(configuration);
            //Connect to the server
            bot.startBot();
        }

    }

    @Override
    public void run() {
        try {
            main(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}