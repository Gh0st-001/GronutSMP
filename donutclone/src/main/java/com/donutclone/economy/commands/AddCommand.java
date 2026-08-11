package com.donutclone.economy.commands;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.util.ActionBar;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddCommand implements CommandExecutor {

    private final EconomyShopPlugin plugin;

    public AddCommand(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("economyshop.admin")) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas la permission d'utiliser cette commande.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /add <joueur> <montant>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        double amount = NumberFormatter.parse(args[1]);
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Montant invalide. Exemple: 1000, 1m, 2.5M");
            return true;
        }

        plugin.getEconomyManager().deposit(target.getUniqueId(), amount);
        sender.sendMessage(ChatColor.GREEN + "Ajoute " + NumberFormatter.formatMoney(amount) + " a " + args[0] + ".");

        if (target.isOnline()) {
            Player online = (Player) target;
            ActionBar.send(online, "&7Un admin vous a paye &a" + NumberFormatter.formatMoney(amount));
            plugin.getScoreboardManager().update(online);
        }
        return true;
    }
}
