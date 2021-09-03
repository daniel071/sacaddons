package net.pavela.sacaddons;

import me.jumper251.replay.api.ReplayAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.korbsti.soaromaac.api.SoaromaFlagEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class saclistener implements Listener {
    @EventHandler
    public void onFlag(SoaromaFlagEvent event){
        Player p = event.getFlaggedPlayer();
        String Impostor = p.getDisplayName();
        Location ImpostorLocation = p.getLocation();
        for (Player all : Bukkit.getServer().getOnlinePlayers()) {
            if (all.isOp()) {
                TextComponent msg = new TextComponent(ChatColor.YELLOW + "[!] " + ChatColor.RED  + ChatColor.BOLD + Impostor + ChatColor.RESET + ChatColor.RED + " may be the Impostor" + ChatColor.GRAY + " (click to teleport)");
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.format("%.0f %.0f %.0f",ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ()))));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/tp @p %.0f %.0f %.0f",ImpostorLocation.getX(), ImpostorLocation.getY(), ImpostorLocation.getZ())));
                all.spigot().sendMessage(msg);
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("HHmmss");
        Date date = new Date();

        String currentDate = dateFormat.format(date);
        String replayName = String.format("%s%s", Impostor, currentDate);
        ReplayAPI.getInstance().recordReplay(replayName, p, p);

    }
}
