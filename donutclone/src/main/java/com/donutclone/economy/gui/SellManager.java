package com.donutclone.economy.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SellManager {

    public enum State { NONE, DEPOSIT_OPEN, TRANSITION, CONFIRM_OPEN, RESOLVED }

    private final Map<UUID, State> states = new HashMap<>();
    private final Map<UUID, Inventory> depositInventories = new HashMap<>();
    private final Map<UUID, List<ItemStack>> pendingItems = new HashMap<>();

    public State getState(UUID uuid) {
        return states.getOrDefault(uuid, State.NONE);
    }

    public void setState(UUID uuid, State state) {
        states.put(uuid, state);
    }

    public Inventory getDepositInventory(UUID uuid) {
        return depositInventories.get(uuid);
    }

    public void setDepositInventory(UUID uuid, Inventory inv) {
        depositInventories.put(uuid, inv);
    }

    public List<ItemStack> getPendingItems(UUID uuid) {
        return pendingItems.get(uuid);
    }

    public void setPendingItems(UUID uuid, List<ItemStack> items) {
        pendingItems.put(uuid, items);
    }

    public void reset(UUID uuid) {
        states.remove(uuid);
        depositInventories.remove(uuid);
        pendingItems.remove(uuid);
    }
}
