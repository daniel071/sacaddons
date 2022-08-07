package net.pavela.sacaddons;

import me.jumper251.replay.api.ReplayAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.logging.log4j.message.MapMessage;
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
import java.util.HashMap;
import java.util.Map;

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
        String Impostor = p.getName();
        Location ImpostorLocation = p.getLocation();
        String FlagChecked = event.getCheckFlagged();
        FlagChecked = FlagChecked.replaceAll(" ", "");

        if (config.getBoolean("irc.enabled")) {
            Runnable myrunnable = new Runnable() {
                public void run() {
                    instance.irc.onFlag(Impostor);
                }
            };

            new Thread(myrunnable).start();
        }

        if (config.getBoolean("flagmsg")) {
            for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                if (all.isOp()) {
                    String flagmsgunformatted = config.getString("msg.flagtext");
                    Map map = new HashMap();
                    map.put("player", Impostor);
                    StrSubstitutor sub = new StrSubstitutor(map);
                    TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('§', sub.replace(flagmsgunformatted)));

                    if (config.getBoolean("tptocoords")) {
                        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.format("%.0f %.0f %.0f", ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ()))));
                        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/tp %.0f %.0f %.0f", ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ())));
                    } else {
                        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.format("/tp %s", Impostor))));
                        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/tp %s", Impostor)));
                    }

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

                            //TextComponent msg = new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + ":: " + ChatColor.RESET + "Replay " + ChatColor.ITALIC + replayName + ChatColor.RESET + " saved " + ChatColor.GRAY + "(click to watch)");
                            String msgunformatted = config.getString("msg.replaysavedtext");
                            Map map = new HashMap();
                            map.put("replay", replayName);
                            StrSubstitutor sub2 = new StrSubstitutor(map);
                            TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('§', sub2.replace(msgunformatted)));
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
            String CrewmateName = Crewmate.getName();

            String ImpostorName = commandArgs[1];
            Player Impostor = Bukkit.getServer().getPlayer(ImpostorName);

            if (config.getBoolean("irc.enabled")) {
                Runnable myrunnable = new Runnable() {
                    public void run() {
                        instance.irc.onReport(ImpostorName);
                    }
                };

                new Thread(myrunnable).start();
            }

            if (Impostor != null) {
                Location ImpostorLocation = Impostor.getLocation();

                if (config.getBoolean("reportreplayhook")) {
                    DateFormat dateFormat = new SimpleDateFormat("dHHmm");
                    Date date = new Date();

                    String currentDate = dateFormat.format(date);
                    replayName = String.format("Report-%s%s", Impostor.getName(), currentDate);
                    ReplayAPI.getInstance().recordReplay(replayName, Impostor, Impostor);
                }

                if (config.getBoolean("reportmsg")) {
                    for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                        if (all.isOp()) {
                            String reportmsgunformatted = config.getString("msg.reporttext");
                            Map reportmap = new HashMap();
                            reportmap.put("player", ImpostorName);
                            StrSubstitutor sub2 = new StrSubstitutor(reportmap);
                            TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('§', sub2.replace(reportmsgunformatted)));

                            if (config.getBoolean("tptocoords")) {
                                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.format("%.0f %.0f %.0f", ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ()))));
                                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/tp %.0f %.0f %.0f", ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ())));
                            } else {
                                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.format("/tp %s", ImpostorName))));
                                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/tp %s", ImpostorName)));
                            }
                            all.spigot().sendMessage(msg);

                            if (config.getBoolean("reportreplayhook")) {
                                String reportmsg2unformatted = config.getString("msg.reportreplaysavedtext");
                                Map reportmap2 = new HashMap();
                                reportmap2.put("replay", replayName);
                                StrSubstitutor sub3 = new StrSubstitutor(reportmap2);
                                TextComponent msg2 = new TextComponent(ChatColor.translateAlternateColorCodes('§', sub3.replace(reportmsg2unformatted)));
                                all.spigot().sendMessage(msg2);
                            }


                        }
                    }
                }


            } else {
                for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                    if (all.isOp()) {
                        TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('§', config.getString("msg.reportnotavailabletext")));
                        all.spigot().sendMessage(msg);
                    }
                }
            }

        }
    }

}
