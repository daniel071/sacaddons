package net.pavela.sacaddons;

import org.bukkit.Bukkit;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.delay.Delay;
import org.pircbotx.delay.StaticDelay;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class irc extends ListenerAdapter implements Runnable {
    private Sacaddons instance;
    public static String sacaddonsversion = "0.0.0";
    public static String sacversion = "0.0.0";
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

    // @Override
    public void onFlag(String ImpostorName) {
        String msg = String.format("\u0002\u000304[!] MrRubberStruck\u000F\u000304 is sus", ImpostorName);
        bot.sendIRC().message(config.getString("irc.channel"), msg);
    }

    public void onReport(String ImpostorName) {
        String msg = String.format("\u0002\u000308[!] %s \u000Fwas reported", ImpostorName);
        bot.sendIRC().message(config.getString("irc.channel"), msg);
    }

    public static void main(Boolean startBot) throws Exception {
        //Configure what we want our bot to do
        if (startBot) {
            Configuration configuration = new Configuration.Builder()
                    .setName(config.getString("irc.username")) // Set the nick of the bot. CHANGE IN YOUR CODE
                    .addServer(config.getString("irc.server")) // Join the libera.chat network
                    .addAutoJoinChannel(config.getString("irc.channel")) // Join the test channel
                    .setMessageDelay( new StaticDelay(0) )
                    .addListener(new irc()) // Add our listener that will be called on Events
                    .buildConfiguration();

            //Create our bot with the configuration
            bot = new PircBotX(configuration);
            //Connect to the server
            bot.startBot();

            if (config.getBoolean("irc.nickserv.enabled")) {
                bot.sendIRC().identify(config.getString("irc.nickserv.password"));
            }
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