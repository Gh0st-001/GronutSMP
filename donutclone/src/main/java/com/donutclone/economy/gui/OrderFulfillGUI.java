package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.data.Order;
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

import java.util.UUID;

public class OrderFulfillGUI {

    private final EconomyShopPlugin plugin;

    public OrderFulfillGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void openDeposit(Player player, Order order) {
        Inventory inv = Bukkit.createInventory(null, 54, Menus.ORDER_FULFILL_DEPOSIT_TITLE);
        inv.setItem(53, buildDepositButton(order, 0));
        plugin.getOrderManager().startFulfilling(player.getUniqueId(), order.getId(), inv);
        player.openInventory(inv);
    }

    public void refreshDepositButton(Player player) {
        OrderManager om = plugin.getOrderManager();
        UUID orderId = om.getFulfillingOrderId(player.getUniqueId());
        if (orderId == null) return;
        Order order = om.getOrder(orderId);
        if (order == null) return;

        Inventory inv = om.getFulfillDeposit(player.getUniqueId());
        if (inv == null) return;
        if (!player.getOpenInventory().getTitle().equals(Menus.ORDER_FULFILL_DEPOSIT_TITLE)) return;

        int matching = 0;
        for (int i = 0; i < 53; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() == order.getMaterial()) matching += item.getAmount();
        }
        inv.setItem(53, buildDepositButton(order, matching));
    }

    private ItemStack buildDepositButton(Order order, int matchingAmount) {
        int accepted = Math.min(matchingAmount, order.getRemainingQuantity());
        double reward = accepted * order.getRewardPerBlock();
        return new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .name("&aFournir")
                .lore(
                        "&7Bloc demande: &f" + prettyName(order.getMaterial()),
                        "&7Tu vas fournir: &f" + accepted,
                        "&7Tu vas gagner: &6" + NumberFormatter.formatMoney(reward),
                        "",
                        "&aClique pour confirmer"
                )
                .build();
    }

    public void onDepositConfirmClicked(Player player) {
        OrderManager om = plugin.getOrderManager();
        UUID orderId = om.getFulfillingOrderId(player.getUniqueId());
        if (orderId == null) return;
        Order order = om.getOrder(orderId);
        Inventory inv = om.getFulfillDeposit(player.getUniqueId());
        if (order == null || inv == null) return;

        int toGive = 0;
        for (int i = 0; i < 53 && toGive < order.getRemainingQuantity(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() != order.getMaterial()) continue;

            int room = order.getRemainingQuantity() - toGive;
            if (item.getAmount() <= room) {
                toGive += item.getAmount();
                inv.setItem(i, null);
            } else {
                item.setAmount(item.getAmount() - room);
                inv.setItem(i, item);
                toGive += room;
            }
        }

        if (toGive <= 0) {
            msg(player, "&cTu n'as pas depose le bon bloc, ou la commande est deja pleine.");
            return;
        }

        om.setFulfillPendingAmount(player.getUniqueId(), toGive);
        om.setFulfillState(player.getUniqueId(), OrderManager.FulfillState.TRANSITION);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

        openConfirm(player, order, toGive);
        om.setFulfillState(player.getUniqueId(), OrderManager.FulfillState.CONFIRM_OPEN);
    }

    private void openConfirm(Player player, Order order, int amount) {
        Inventory inv = Bukkit.createInventory(null, 27, Menus.ORDER_FULFILL_CONFIRM_TITLE);
        double reward = amount * order.getRewardPerBlock();

        inv.setItem(13, new ItemBuilder(order.getMaterial())
                .amount(Math.min(64, amount))
                .name("&fA fournir: " + amount)
                .lore("&7Recompense: &6" + NumberFormatter.formatMoney(reward))
                .build());

        inv.setItem(11, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .name("&cAnnuler")
                .lore("&7Recupere tes objets")
                .build());

        inv.setItem(15, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .name("&aConfirmer")
                .lore("&7Tu vas gagner: &6" + NumberFormatter.formatMoney(reward))
                .build());

        player.openInventory(inv);
    }

    public void confirmFulfill(Player player) {
        OrderManager om = plugin.getOrderManager();
        UUID orderId = om.getFulfillingOrderId(player.getUniqueId());
        Integer amount = om.getFulfillPendingAmount(player.getUniqueId());
        if (orderId == null || amount == null) return;
        Order order = om.getOrder(orderId);
        if (order == null) return;

        double reward = order.contribute(amount);
        plugin.getEconomyManager().deposit(player.getUniqueId(), reward);
        plugin.getScoreboardManager().update(player);
        om.removeIfResolved(order);

        om.setFulfillState(player.getUniqueId(), OrderManager.FulfillState.RESOLVED);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        player.closeInventory();
        ActionBar.send(player, "&aVendu pour " + NumberFormatter.formatMoney(reward) + " !");

        om.resetFulfilling(player.getUniqueId());
    }

    public void cancelFulfill(Player player) {
        OrderManager om = plugin.getOrderManager();
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        om.setFulfillState(player.getUniqueId(), OrderManager.FulfillState.RESOLVED);
        giveBackPending(player);
        player.closeInventory();
        msg(player, "&7Fourniture annulee, tes objets t'ont ete rendus.");
        om.resetFulfilling(player.getUniqueId());
    }

    public void giveBackPending(Player player) {
        OrderManager om = plugin.getOrderManager();
        UUID orderId = om.getFulfillingOrderId(player.getUniqueId());
        Integer amount = om.getFulfillPendingAmount(player.getUniqueId());
        if (orderId == null || amount == null || amount <= 0) return;
        Order order = om.getOrder(orderId);
        if (order == null) return;

        int remaining = amount;
        int maxStack = order.getMaterial().getMaxStackSize();
        while (remaining > 0) {
            int chunk = Math.min(remaining, maxStack);
            giveBack(player, new ItemStack(order.getMaterial(), chunk));
            remaining -= chunk;
        }
    }

    public void giveBackDeposit(Player player, Inventory inv) {
        for (int i = 0; i < 53; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null) giveBack(player, item);
        }
    }

    private void giveBack(Player player, ItemStack stack) {
        player.getInventory().addItem(stack).values()
                .forEach(left -> player.getWorld().dropItem(player.getLocation(), left));
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
