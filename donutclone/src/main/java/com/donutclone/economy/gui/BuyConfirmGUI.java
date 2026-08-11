package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.util.ItemBuilder;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BuyConfirmGUI {

    private final EconomyShopPlugin plugin;

    public BuyConfirmGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, Material material) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        session.setPendingMaterial(material);

        Inventory inv = Bukkit.createInventory(null, 27, Menus.BUY_CONFIRM_TITLE);
        double price = plugin.getPriceManager().getPrice(material);
        int stackSize = material.getMaxStackSize();

        inv.setItem(13, new ItemBuilder(material)
                .name("&f" + prettyName(material))
                .lore("&7Prix unitaire: &6" + NumberFormatter.formatMoney(price))
                .build());

        inv.setItem(11, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .name("&cAnnuler")
                .build());

        inv.setItem(15, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .name("&aAcheter 1")
                .lore("&7Prix: &6" + NumberFormatter.formatMoney(price))
                .build());

        inv.setItem(16, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .name("&aAcheter un stack (" + stackSize + ")")
                .lore("&7Prix: &6" + NumberFormatter.formatMoney(price * stackSize))
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
