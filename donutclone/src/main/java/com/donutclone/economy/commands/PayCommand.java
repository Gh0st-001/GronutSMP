package com.donutclone.economy.commands;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.util.ActionBar;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final EconomyShopPlugin plugin;

    public PayCommand(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande est reservee aux joueurs.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /pay <joueur> <montant>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Joueur introuvable ou hors ligne.");
            return true;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Tu ne peux pas te payer toi-meme.");
            return true;
        }

        double amount = NumberFormatter.parse(args[1]);
        if (amount <= 0) {
            player.sendMessage(ChatColor.RED + "Montant invalide.");
            return true;
        }

        if (!plugin.getEconomyManager().withdraw(player.getUniqueId(), amount)) {
            player.sendMessage(ChatColor.RED + "Tu n'as pas assez d'argent.");
            return true;
        }
        plugin.getEconomyManager().deposit(target.getUniqueId(), amount);

        player.sendMessage(ChatColor.GREEN + "Tu as envoye " + NumberFormatter.formatMoney(amount) + " a " + target.getName() + ".");
        ActionBar.send(target, "&f" + player.getName() + " &7vous a paye &a" + NumberFormatter.formatMoney(amount));
        plugin.getScoreboardManager().update(player);
        plugin.getScoreboardManager().update(target);
        return true;
    }
}
