package com.donutclone.economy.gui;

import com.donutclone.economy.data.Order;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderManager {

    /** Brouillon en cours de configuration (avant confirmation de creation) */
    public static class Draft {
        public Material material;
        public int quantity = 0;
        public double reward = 0;
    }

    public enum FulfillState { NONE, DEPOSIT_OPEN, TRANSITION, CONFIRM_OPEN, RESOLVED }

    private final LinkedHashMap<UUID, Order> orders = new LinkedHashMap<>();
    private final Map<UUID, Draft> drafts = new java.util.HashMap<>();

    // pour le remplissage d'une order (deposer des blocs)
    private final Map<UUID, UUID> fulfillingOrderId = new java.util.HashMap<>();
    private final Map<UUID, FulfillState> fulfillState = new java.util.HashMap<>();
    private final Map<UUID, Inventory> fulfillDepositInventories = new java.util.HashMap<>();
    private final Map<UUID, Integer> fulfillPendingAmount = new java.util.HashMap<>();

    // -------- orders --------
    public void addOrder(Order order) {
        orders.put(order.getId(), order);
    }

    public Order getOrder(UUID id) {
        return orders.get(id);
    }

    public List<Order> getBrowsableOrders() {
        List<Order> list = new ArrayList<>();
        for (Order o : orders.values()) {
            if (o.isBrowsable()) list.add(o);
        }
        return list;
    }

    public Order getOrderByCreator(UUID creator) {
        for (Order o : orders.values()) {
            if (o.getCreator().equals(creator) && !o.isFullyResolved()) return o;
        }
        return null;
    }

    public void removeIfResolved(Order order) {
        if (order.isFullyResolved()) {
            orders.remove(order.getId());
        }
    }

    // -------- brouillon de creation --------
    public Draft getDraft(UUID uuid) {
        return drafts.computeIfAbsent(uuid, u -> new Draft());
    }

    public void clearDraft(UUID uuid) {
        drafts.remove(uuid);
    }

    // -------- remplissage (deposer des blocs) --------
    public void startFulfilling(UUID player, UUID orderId, Inventory depositInv) {
        fulfillingOrderId.put(player, orderId);
        fulfillState.put(player, FulfillState.DEPOSIT_OPEN);
        fulfillDepositInventories.put(player, depositInv);
    }

    public UUID getFulfillingOrderId(UUID player) { return fulfillingOrderId.get(player); }
    public FulfillState getFulfillState(UUID player) { return fulfillState.getOrDefault(player, FulfillState.NONE); }
    public void setFulfillState(UUID player, FulfillState state) { fulfillState.put(player, state); }
    public Inventory getFulfillDeposit(UUID player) { return fulfillDepositInventories.get(player); }
    public Integer getFulfillPendingAmount(UUID player) { return fulfillPendingAmount.get(player); }
    public void setFulfillPendingAmount(UUID player, int amount) { fulfillPendingAmount.put(player, amount); }

    public void resetFulfilling(UUID player) {
        fulfillingOrderId.remove(player);
        fulfillState.remove(player);
        fulfillDepositInventories.remove(player);
        fulfillPendingAmount.remove(player);
    }
}
