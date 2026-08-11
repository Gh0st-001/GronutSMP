package com.donutclone.economy.listeners;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.gui.AnvilTextInputManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class AnvilInputListener implements Listener {

    private final EconomyShopPlugin plugin;

    public AnvilInputListener(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepare(PrepareAnvilEvent event) {
        if (!(event.getView().getPlayer() instanceof Player player)) return;
        if (!plugin.getAnvilInputManager().hasPending(player.getUniqueId())) return;

        event.setCost(0);
        if (event.getResult() == null) {
            ItemStack first = event.getInventory().getItem(0);
            if (first != null) event.setResult(first.clone());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        AnvilTextInputManager manager = plugin.getAnvilInputManager();
        if (!manager.hasPending(player.getUniqueId())) return;
        if (event.getSlot() != 2) return; // slot du resultat uniquement, on laisse le reste tranquille

        event.setCancelled(true);
        AnvilInventory anvilInv = (AnvilInventory) event.getInventory();
        String text = anvilInv.getRenameText();
        if (text == null || text.isBlank()) {
            ItemStack current = event.getCurrentItem();
            if (current != null && current.hasItemMeta() && current.getItemMeta().hasDisplayName()) {
                text = current.getItemMeta().getDisplayName();
            }
        }
        if (text == null || text.isBlank()) return;

        AnvilTextInputManager.Callback callback = manager.get(player.getUniqueId());
        manager.clear(player.getUniqueId());
        String finalText = text;
        player.closeInventory();
        callback.onSubmit(player, finalText);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        if (!(event.getPlayer() instanceof Player player)) return;
        plugin.getAnvilInputManager().clear(player.getUniqueId());
    }
}
