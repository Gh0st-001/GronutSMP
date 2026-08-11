package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.util.ActionBar;
import com.donutclone.economy.util.ItemBuilder;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SellGUI {

    private final EconomyShopPlugin plugin;

    public SellGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void openDeposit(Player player) {
        SellManager sm = plugin.getSellManager();
        Inventory inv = Bukkit.createInventory(null, 54, Menus.SELL_TITLE);
        inv.setItem(53, buildDepositButton(0));
        sm.setDepositInventory(player.getUniqueId(), inv);
        sm.setState(player.getUniqueId(), SellManager.State.DEPOSIT_OPEN);
        player.openInventory(inv);
    }

    /** Recalcule et met a jour le total affiche sur la vitre verte du panier */
    public void refreshDepositButton(Player player) {
        SellManager sm = plugin.getSellManager();
        Inventory inv = sm.getDepositInventory(player.getUniqueId());
        if (inv == null) return;
        if (!player.getOpenInventory().getTitle().equals(Menus.SELL_TITLE)) return;

        double total = 0;
        for (int i = 0; i < 53; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            total += plugin.getPriceManager().getPrice(item.getType()) * item.getAmount();
        }
        inv.setItem(53, buildDepositButton(total));
    }

    private ItemStack buildDepositButton(double total) {
        return new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .name("&aVendre tout")
                .lore("&7Tu vas gagner: &6" + NumberFormatter.formatMoney(total),
                        "",
                        "&aClique pour confirmer")
                .build();
    }

    public void onConfirmButtonClicked(Player player) {
        SellManager sm = plugin.getSellManager();
        Inventory inv = sm.getDepositInventory(player.getUniqueId());
        if (inv == null) return;

        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 53; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getAmount() > 0) {
                items.add(item.clone());
                inv.setItem(i, null);
            }
        }
        if (items.isEmpty()) {
            msg(player, "&cTon panier est vide.");
            return;
        }

        sm.setPendingItems(player.getUniqueId(), items);
        sm.setState(player.getUniqueId(), SellManager.State.TRANSITION);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

        openConfirm(player, items);
        sm.setState(player.getUniqueId(), SellManager.State.CONFIRM_OPEN);
    }

    private void openConfirm(Player player, List<ItemStack> items) {
        Inventory inv = Bukkit.createInventory(null, 27, Menus.SELL_CONFIRM_TITLE);

        LinkedHashMap<Material, Integer> grouped = new LinkedHashMap<>();
        double total = 0;
        for (ItemStack item : items) {
            grouped.merge(item.getType(), item.getAmount(), Integer::sum);
            total += plugin.getPriceManager().getPrice(item.getType()) * item.getAmount();
        }

        Material first = grouped.keySet().iterator().next();
        int firstAmount = grouped.get(first);

        List<String> breakdown = new ArrayList<>();
        int shown = 0;
        for (Map.Entry<Material, Integer> entry : grouped.entrySet()) {
            if (shown >= 6) {
                breakdown.add("&7... et " + (grouped.size() - shown) + " autre(s) type(s)");
                break;
            }
            breakdown.add("&7- " + entry.getValue() + "x " + prettyName(entry.getKey()));
            shown++;
        }

        ItemStack icon = new ItemBuilder(first)
                .amount(Math.min(64, firstAmount))
                .name("&fObjets a vendre")
                .lore(breakdown)
                .build();
        inv.setItem(13, icon);

        inv.setItem(11, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .name("&cAnnuler")
                .lore("&7Recupere tes objets")
                .build());

        inv.setItem(15, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .name("&aConfirmer la vente")
                .lore("&7Tu vas gagner: &6" + NumberFormatter.formatMoney(total))
                .build());

        player.openInventory(inv);
    }

    public void confirmSell(Player player) {
        SellManager sm = plugin.getSellManager();
        List<ItemStack> items = sm.getPendingItems(player.getUniqueId());
        if (items == null) return;

        double total = 0;
        for (ItemStack item : items) {
            total += plugin.getPriceManager().getPrice(item.getType()) * item.getAmount();
        }

        plugin.getEconomyManager().deposit(player.getUniqueId(), total);
        plugin.getScoreboardManager().update(player);

        sm.setState(player.getUniqueId(), SellManager.State.RESOLVED);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        player.closeInventory();
        ActionBar.send(player, "&aVendu pour " + NumberFormatter.formatMoney(total) + " !");

        sm.reset(player.getUniqueId());
    }

    public void cancelSell(Player player) {
        SellManager sm = plugin.getSellManager();
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        sm.setState(player.getUniqueId(), SellManager.State.RESOLVED);
        giveBackPending(player);
        player.closeInventory();
        msg(player, "&7Vente annulee, tes objets t'ont ete rendus.");
        sm.reset(player.getUniqueId());
    }

    public void giveBackPending(Player player) {
        SellManager sm = plugin.getSellManager();
        List<ItemStack> items = sm.getPendingItems(player.getUniqueId());
        if (items == null) return;
        for (ItemStack item : items) {
            player.getInventory().addItem(item).values()
                    .forEach(left -> player.getWorld().dropItem(player.getLocation(), left));
        }
    }

    public void giveBackDeposit(Player player, Inventory inv) {
        for (int i = 0; i < 53; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null) {
                player.getInventory().addItem(item).values()
                        .forEach(left -> player.getWorld().dropItem(player.getLocation(), left));
            }
        }
    }

    private String prettyName(Material mat) {
        String[] parts = mat.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        return sb.toString().trim();
    }

    private void msg(Player player, String text) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }
}
