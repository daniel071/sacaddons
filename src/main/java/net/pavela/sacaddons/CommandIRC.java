package net.pavela.sacaddons;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static org.apache.commons.lang3.StringUtils.join;

public class CommandIRC implements CommandExecutor {
    private Sacaddons instance;

    public CommandIRC(Sacaddons sacaddons) {
        instance = sacaddons;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) {
            String commandName = join(args, ' ');
            // Bukkit.getConsoleSender().sendMessage(commandName);

            Runnable myrunnable = new Runnable() {
                public void run() {
                    instance.irc.runCommand(commandName);
                }
            };

            new Thread(myrunnable).start();
            return false;
        } else {
            return true;
        }
    }
}