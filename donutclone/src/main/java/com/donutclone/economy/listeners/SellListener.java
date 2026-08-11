package com.donutclone.economy.listeners;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.gui.Menus;
import com.donutclone.economy.gui.SellManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SellListener implements Listener {

    private final EconomyShopPlugin plugin;

    public SellListener(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(Menus.SELL_TITLE)) {
            handleDepositClick(event);
        } else if (title.equals(Menus.SELL_CONFIRM_TITLE)) {
            handleConfirmClick(event);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals(Menus.SELL_TITLE)) return;

        if (event.getRawSlots().contains(53)) {
            event.setCancelled(true);
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Bukkit.getScheduler().runTask(plugin, () -> plugin.getSellGUI().refreshDepositButton(player));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        String title = event.getView().getTitle();
        SellManager sm = plugin.getSellManager();

        if (title.equals(Menus.SELL_TITLE)) {
            if (sm.getState(player.getUniqueId()) == SellManager.State.DEPOSIT_OPEN) {
                // fermeture volontaire (Echap) -> on rend les objets deposes
                plugin.getSellGUI().giveBackDeposit(player, event.getInventory());
                sm.reset(player.getUniqueId());
            }
            // si TRANSITION: on ne touche a rien, le flux continue vers la confirmation
        } else if (title.equals(Menus.SELL_CONFIRM_TITLE)) {
            if (sm.getState(player.getUniqueId()) == SellManager.State.CONFIRM_OPEN) {
                // ferme par Echap sans cliquer sur confirmer/annuler -> on annule
                plugin.getSellGUI().giveBackPending(player);
                sm.reset(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SellManager sm = plugin.getSellManager();
        SellManager.State state = sm.getState(player.getUniqueId());

        if (state == SellManager.State.DEPOSIT_OPEN) {
            var inv = sm.getDepositInventory(player.getUniqueId());
            if (inv != null) plugin.getSellGUI().giveBackDeposit(player, inv);
        } else if (state == SellManager.State.CONFIRM_OPEN) {
            plugin.getSellGUI().giveBackPending(player);
        }
        sm.reset(player.getUniqueId());
    }

    private void handleDepositClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != null
                && event.getClickedInventory().equals(event.getView().getTopInventory())
                && event.getSlot() == 53) {
            event.setCancelled(true);
            plugin.getSellGUI().onConfirmButtonClicked(player);
            return;
        }

        // tous les autres slots: on laisse Bukkit deplacer l'item normalement
        // (deposer/retirer des objets), puis on recalcule le total au tick suivant.
        Bukkit.getScheduler().runTask(plugin, () -> plugin.getSellGUI().refreshDepositButton(player));
    }

    private void handleConfirmClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(event.getView().getTopInventory())) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == 15) {
            plugin.getSellGUI().confirmSell(player);
        } else if (slot == 11) {
            plugin.getSellGUI().cancelSell(player);
        }
    }
}
