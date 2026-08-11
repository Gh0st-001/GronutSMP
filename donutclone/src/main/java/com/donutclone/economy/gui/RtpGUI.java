package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class RtpGUI {

    private final EconomyShopPlugin plugin;

    public RtpGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Menus.RTP_TITLE);

        inv.setItem(11, new ItemBuilder(Material.GRASS_BLOCK)
                .name("&aOverworld")
                .lore("&7Teleportation aleatoire", "&7dans le monde principal")
                .build());

        inv.setItem(13, new ItemBuilder(Material.NETHERRACK)
                .name("&cNether")
                .lore("&7Teleportation aleatoire", "&7dans le Nether")
                .build());

        inv.setItem(15, new ItemBuilder(Material.END_STONE)
                .name("&5End")
                .lore("&7Teleportation aleatoire", "&7dans l'End")
                .build());

        player.openInventory(inv);
    }
}
