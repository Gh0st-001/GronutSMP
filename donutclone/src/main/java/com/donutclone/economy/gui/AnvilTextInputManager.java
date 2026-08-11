package com.donutclone.economy.gui;

import com.donutclone.economy.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Permet d'ouvrir une enclume comme "zone de texte": le joueur tape dans le
 * champ de renommage, puis clique sur le resultat pour valider. On recupere
 * alors le texte tape sans jamais passer par le chat.
 */
public class AnvilTextInputManager {

    public interface Callback {
        void onSubmit(Player player, String text);
    }

    private final Map<UUID, Callback> callbacks = new HashMap<>();

    public void open(Player player, String title, String placeholderText) {
        // le callback doit avoir ete enregistre juste avant via register()
        Inventory anvil = Bukkit.createInventory(null, InventoryType.ANVIL, title);
        anvil.setItem(0, new ItemBuilder(Material.PAPER).name(placeholderText).build());
        player.openInventory(anvil);
    }

    public void register(UUID uuid, Callback callback) {
        callbacks.put(uuid, callback);
    }

    public boolean hasPending(UUID uuid) {
        return callbacks.containsKey(uuid);
    }

    public Callback get(UUID uuid) {
        return callbacks.get(uuid);
    }

    public void clear(UUID uuid) {
        callbacks.remove(uuid);
    }
}
