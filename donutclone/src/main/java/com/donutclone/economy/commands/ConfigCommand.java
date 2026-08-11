package com.donutclone.economy.commands;

import com.donutclone.economy.EconomyShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigCommand implements CommandExecutor {

    private final EconomyShopPlugin plugin;

    public ConfigCommand(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande est reservee aux joueurs.");
            return true;
        }
        if (!player.hasPermission("economyshop.admin")) {
            player.sendMessage(ChatColor.RED + "Tu n'as pas la permission d'utiliser cette commande.");
            return true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.getPriceManager().load();
            plugin.getCategoryManager().load();
            player.sendMessage(ChatColor.GREEN + "Configuration rechargee.");
            return true;
        }
        plugin.getConfigGUI().openMain(player);
        return true;
    }
}
