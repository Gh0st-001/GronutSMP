package com.donutclone.economy.commands;

import com.donutclone.economy.EconomyShopPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OrderCommand implements CommandExecutor {

    private final EconomyShopPlugin plugin;

    public OrderCommand(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande est reservee aux joueurs.");
            return true;
        }
        plugin.getOrderBrowseGUI().open(player);
        return true;
    }
}
