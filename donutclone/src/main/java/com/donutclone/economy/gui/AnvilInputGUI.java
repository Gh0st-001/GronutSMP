package com.donutclone.economy.gui;

import com.donutclone.economy.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Ouvre une enclume virtuelle (non liee a un bloc reel) dont on utilise
 * uniquement le champ de renommage comme zone de texte. Aucune experience
 * n'est consommee puisqu'il n'y a pas de reparation/combinaison en jeu.
 */
public class AnvilInputGUI {

    public interface Callback {
        void onInput(Player player, String text);
    }

    private static final Map<UUID, Callback> pending = new HashMap<>();

    public static void open(Player player, String prompt, Callback callback) {
        AnvilInventory inv = (AnvilInventory) Bukkit.createInventory(null, InventoryType.ANVIL, "\u00A78" + prompt);
        inv.setItem(0, new ItemBuilder(Material.PAPER).name("&f" + prompt).build());
        pending.put(player.getUniqueId(), callback);
        player.openInventory(inv);
    }

    static boolean isPending(UUID uuid) {
        return pending.containsKey(uuid);
    }

    static Callback consume(UUID uuid) {
        return pending.remove(uuid);
    }
}
