package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.data.Order;
import com.donutclone.economy.util.ItemBuilder;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class OrderCancelConfirmGUI {

    private final EconomyShopPlugin plugin;

    public OrderCancelConfirmGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, Order order) {
        Inventory inv = Bukkit.createInventory(null, 27, Menus.ORDER_CANCEL_CONFIRM_TITLE);

        inv.setItem(13, new ItemBuilder(order.getMaterial())
                .name("&fAnnuler cette commande ?")
                .lore("&7Rembourse: &6" + NumberFormatter.formatMoney(order.getRemainingPot()),
                        "&7Les blocs deja fournis restent",
                        "&7a recuperer dans le coffre")
                .build());

        inv.setItem(11, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .name("&cOui, annuler")
                .build());

        inv.setItem(15, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .name("&aNon, revenir")
                .build());

        player.openInventory(inv);
    }
}
