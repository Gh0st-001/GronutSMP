package com.donutclone.economy.commands;

import com.donutclone.economy.EconomyShopPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AHCommand implements CommandExecutor {

    private final EconomyShopPlugin plugin;

    public AHCommand(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande est reservee aux joueurs.");
            return true;
        }
        var session = plugin.getSessionManager().get(player.getUniqueId());
        session.setPage(0);
        if (args.length > 0) {
            session.setSearchQuery(String.join(" ", args));
        } else {
            session.setSearchQuery("");
        }
        plugin.getAhGUI().open(player);
        return true;
    }
}
