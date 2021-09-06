package net.pavela.sacaddons;

import org.bukkit.Bukkit;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class irc extends ListenerAdapter implements Runnable {
    private Sacaddons instance;
    static String sacaddonsversion = "0.0.0";
    static String sacversion = "0.0.0";
    public static PircBotX bot;


    public irc(Sacaddons instance, boolean startBot){
        Bukkit.getConsoleSender().sendMessage(":: constructor loading");

        this.instance = instance;

        sacversion = instance.sacversion;
        sacaddonsversion = instance.sacaddonsversion;

        if (instance.botThread.isAlive()) {
            try {
                main(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public irc() {
        Bukkit.getConsoleSender().sendMessage(":: constructor loading 2");
    }


    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        Bukkit.getConsoleSender().sendMessage(event.getMessage());
        if (event.getMessage().startsWith("?info")) {
            event.respond("----------------------------------------------");
            event.respond("Minecraft server with SoaromaSAC and sacaddons");
            event.respond(String.format("\u0002Minecraft version:\u000F %s %s", Bukkit.getVersion(), Bukkit.getBukkitVersion()));
            event.respond(String.format("\u0002SoaromaSAC version:\u000F %s", sacversion));
            event.respond(String.format("\u0002sacaddons version:\u000F %s", sacaddonsversion));
            event.respond("----------------------------------------------");
        }
    }

    // @Override
    public void onFlag() {
        Bukkit.getConsoleSender().sendMessage("!! GAMER MOMENT - IT WORKED!");
        //bot.sendIRC().message("#SoaromaSAC", "UH OH WARNING FLAG STINKY MOMENT");
    }

    public static void main(Boolean startBot) throws Exception {
        //Configure what we want our bot to do
        if (startBot) {
            Configuration configuration = new Configuration.Builder()
                    .setName("cumbot") // Set the nick of the bot. CHANGE IN YOUR CODE
                    .addServer("irc.libera.chat") // Join the libera.chat network
                    .addAutoJoinChannel("#SoaromaSAC") // Join the test channel
                    .addListener(new irc()) // Add our listener that will be called on Events
                    .buildConfiguration();

            //Create our bot with the configuration
            bot = new PircBotX(configuration);
            //Connect to the server
            bot.startBot();
            Bukkit.getConsoleSender().sendMessage(":: bot started !");
        }

    }

    @Override
    public void run() {

    }
}