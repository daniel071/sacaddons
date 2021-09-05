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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class saclistener implements Listener {
    FileConfiguration config = Sacaddons.getPlugin(Sacaddons.class).getConfig();
    @EventHandler
    public void onFlag(SoaromaFlagEvent event){
        Player p = event.getFlaggedPlayer();
        String Impostor = p.getDisplayName();
        Location ImpostorLocation = p.getLocation();

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
            ReplayAPI.getInstance().recordReplay(replayName, p, p);
        }
    }

    @EventHandler
    public void PlayerCommand(PlayerCommandPreprocessEvent event) {
        Player Crewmate = event.getPlayer();
        Location CrewmateLocation = Crewmate.getLocation();

        // spaces in report reason does get split up but currently we don't need that
        String command = event.getMessage();
        String[] commandArgs = command.split(" ");
        Bukkit.getConsoleSender().sendMessage(commandArgs);

        String ImpostorName = commandArgs[1];
        Player Impostor = Bukkit.getServer().getPlayer(ImpostorName);
        Location ImpostorLocation = Impostor.getLocation();

        if (command.startsWith("/sacreport")) {
            for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                if (all.isOp()) {
                    TextComponent msg = new TextComponent(ChatColor.YELLOW + "[!] " + ChatColor.YELLOW + ChatColor.BOLD + Impostor.getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + " was reported " + ChatColor.GRAY + " (click to teleport)");
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.format("%.0f %.0f %.0f", ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ()))));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/tp @p %.0f %.0f %.0f", ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ())));
                    all.spigot().sendMessage(msg);
                }
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("dHHmm");
        Date date = new Date();

        String currentDate = dateFormat.format(date);
        String replayName = String.format("Report-%s%s", Impostor, currentDate);
        ReplayAPI.getInstance().recordReplay(replayName, Impostor, Impostor);

    }

}
