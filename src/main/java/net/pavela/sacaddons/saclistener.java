package net.pavela.sacaddons;

import me.jumper251.replay.api.ReplayAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.korbsti.soaromaac.api.SoaromaFlagEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class saclistener implements Listener {
    private Sacaddons instance;
    FileConfiguration config = Sacaddons.getPlugin(Sacaddons.class).getConfig();
    ArrayList<String> currentRecordings = new ArrayList<String>();

    public saclistener(Sacaddons plugin, Sacaddons instance){

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.instance = instance;

    }

    @EventHandler
    public void onFlag(SoaromaFlagEvent event){
        Player p = event.getFlaggedPlayer();
        String Impostor = p.getDisplayName();
        Location ImpostorLocation = p.getLocation();

        // instance.botThread.onFlag();

        if (config.getBoolean("flagmsg")) {
            for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                if (all.isOp()) {
                    TextComponent msg = new TextComponent(ChatColor.YELLOW + "[!] " + ChatColor.RED + ChatColor.BOLD + Impostor + ChatColor.RESET + ChatColor.RED + " may be the Impostor" + ChatColor.GRAY + " (click to teleport)");
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.format("%.0f %.0f %.0f", ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ()))));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/tp @p %.0f %.0f %.0f", ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ())));
                    all.spigot().sendMessage(msg);

                    if (config.getBoolean("flagsound.enabled")) {
                        all.playSound(all.getLocation(), Sound.valueOf(config.getString("flagsound.sound")), (float) config.getDouble("flagsound.volume"), (float) config.getDouble("flagsound.pitch"));
                    }
                }
            }
        }

        if (config.getBoolean("replayhook")) {

            // format: day in month , 24h time and minutes
            // e.g. 150800 = 15th of the month at 8 AM
            DateFormat dateFormat = new SimpleDateFormat("dHHmm");
            Date date = new Date();
            String currentDate = dateFormat.format(date);
            String replayName = String.format("%s%s", Impostor, currentDate);

            // Bukkit.getConsoleSender().sendMessage(":: Checking replay");
            if (!currentRecordings.contains(replayName)) {
                // Bukkit.getConsoleSender().sendMessage(":: Replay started: ", replayName);
                currentRecordings.add(replayName);
                ReplayAPI.getInstance().recordReplay(replayName, p, p);

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
                    public void run() {
                        // Bukkit.getConsoleSender().sendMessage(":: Replay stopping ", replayName);
                        ReplayAPI.getInstance().stopReplay(replayName, true);
                        currentRecordings.remove(replayName);

                        if (config.getBoolean("flagmsg")) {

                            TextComponent msg = new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + ":: " + ChatColor.RESET + "Replay " + ChatColor.ITALIC + replayName + ChatColor.RESET + " saved " + ChatColor.GRAY + "(click to watch)");
                            String replayCommand = String.format("/replay play %s", replayName);

                            for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                                if (all.isOp()) {

                                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(replayCommand)));
                                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, replayCommand));
                                    all.spigot().sendMessage(msg);
                                }
                            }
                        }

                    }
                }, config.getInt("replaylength") * 10L);
            }
        }
    }

    @EventHandler
    public void PlayerCommand(PlayerCommandPreprocessEvent event) {
        // spaces in report reason does get split up but currently we don't need that
        String command = event.getMessage();
        String[] commandArgs = command.split(" ");

        if (commandArgs[0].equals("/sacreport")) {
            String replayName = null;

            Player Crewmate = event.getPlayer();
            Location CrewmateLocation = Crewmate.getLocation();
            String CrewmateName = Crewmate.getDisplayName();

            String ImpostorName = commandArgs[1];
            Player Impostor = Bukkit.getServer().getPlayer(ImpostorName);

            if (Impostor != null) {
                Location ImpostorLocation = Impostor.getLocation();

                if (config.getBoolean("reportreplayhook")) {
                    DateFormat dateFormat = new SimpleDateFormat("dHHmm");
                    Date date = new Date();

                    String currentDate = dateFormat.format(date);
                    replayName = String.format("Report-%s%s", Impostor.getDisplayName(), currentDate);
                    ReplayAPI.getInstance().recordReplay(replayName, Impostor, Impostor);
                }

                if (config.getBoolean("reportmsg")) {
                    for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                        if (all.isOp()) {
                            TextComponent msg = new TextComponent(ChatColor.YELLOW + "[!] " + ChatColor.YELLOW + ChatColor.BOLD + Impostor.getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + " was reported " + ChatColor.GRAY + " (click to teleport)");
                            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.format("%.0f %.0f %.0f", ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ()))));
                            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/tp @p %.0f %.0f %.0f", ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ())));
                            all.spigot().sendMessage(msg);

                            // TODO: Show this message after report is saved and make message clickable to play report
                            if (config.getBoolean("reportreplayhook")) {
                                TextComponent msg2 = new TextComponent(ChatColor.YELLOW + "[!] " + ChatColor.YELLOW + "Replay will be saved as " + ChatColor.ITALIC + replayName);
                                all.spigot().sendMessage(msg2);
                            }


                        }
                    }
                }


            } else {
                for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                    if (all.isOp()) {
                        TextComponent msg = new TextComponent(ChatColor.YELLOW + "[!] " + ChatColor.RESET + "Reported username is not currently online");
                        all.spigot().sendMessage(msg);
                    }
                }
            }

        }
    }

}
