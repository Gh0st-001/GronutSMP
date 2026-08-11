package com.donutclone.economy.commands;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private final EconomyShopPlugin plugin;

    public BalanceCommand(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Precise un joueur: /balance <joueur>");
                return true;
            }
            double bal = plugin.getEconomyManager().getBalance(player.getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7Ton solde: &6" + NumberFormatter.formatMoney(bal)));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        double bal = plugin.getEconomyManager().getBalance(target.getUniqueId());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7Solde de &f" + args[0] + "&7: &6" + NumberFormatter.formatMoney(bal)));
        return true;
    }
}
