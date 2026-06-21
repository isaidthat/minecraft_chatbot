package com.github.isaidthat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class Model implements CommandExecutor {
    private final JavaPlugin plugin;
    
    public Model(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    
    @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        
        if (!(sender instanceof Player player)) {
            sender.sendMessage("only players can use this command");
            return true;
        }

        if (!player.hasPermission("bot.chat")) {
            player.sendMessage("no permission");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("usage: /model <model name>");
            return false;
        }

        plugin.getConfig().set("model", args[0]);
        plugin.saveConfig();
        return true;

    }

}