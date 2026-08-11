package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.util.ItemBuilder;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class OrderRecapGUI {

    private final EconomyShopPlugin plugin;

    public OrderRecapGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        OrderManager.Draft draft = plugin.getOrderManager().getDraft(player.getUniqueId());
        Inventory inv = Bukkit.createInventory(null, 27, Menus.ORDER_RECAP_TITLE);

        inv.setItem(10, new ItemBuilder(draft.material)
                .name("&fBloc: " + prettyName(draft.material))
                .lore("&7Clique pour changer")
                .build());

        inv.setItem(13, new ItemBuilder(Material.PAPER)
                .name("&eQuantite: " + draft.quantity)
                .lore("&7Clique pour changer")
                .build());

        inv.setItem(16, new ItemBuilder(Material.GOLD_NUGGET)
                .name("&6Prix/bloc: " + NumberFormatter.formatMoney(draft.reward))
                .lore("&7Clique pour changer")
                .build());

        double total = draft.quantity * draft.reward;
        inv.setItem(20, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .name("&cAnnuler")
                .build());
        inv.setItem(24, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .name("&aAccepter")
                .lore("&7Cout total: &6" + NumberFormatter.formatMoney(total))
                .build());

        player.openInventory(inv);
    }

    private String prettyName(Material mat) {
        String[] parts = mat.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        return sb.toString().trim();
    }
}
