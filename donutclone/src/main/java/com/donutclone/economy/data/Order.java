package com.donutclone.economy.data;

import org.bukkit.Material;

import java.util.UUID;

public class Order {

    public enum Status { ACTIVE, COMPLETED, CANCELLED }

    private final UUID id;
    private final UUID creator;
    private final String creatorName;
    private final Material material;
    private final int totalQuantity;
    private final double rewardPerBlock;

    private int remainingQuantity;   // encore a fournir par des joueurs
    private int collectedQuantity;   // deja fourni, en attente que le createur le recupere
    private double remainingPot;     // argent encore disponible pour payer les joueurs
    private Status status;

    public Order(UUID creator, String creatorName, Material material, int totalQuantity, double rewardPerBlock) {
        this.id = UUID.randomUUID();
        this.creator = creator;
        this.creatorName = creatorName;
        this.material = material;
        this.totalQuantity = totalQuantity;
        this.rewardPerBlock = rewardPerBlock;
        this.remainingQuantity = totalQuantity;
        this.collectedQuantity = 0;
        this.remainingPot = totalQuantity * rewardPerBlock;
        this.status = Status.ACTIVE;
    }

    public UUID getId() { return id; }
    public UUID getCreator() { return creator; }
    public String getCreatorName() { return creatorName; }
    public Material getMaterial() { return material; }
    public int getTotalQuantity() { return totalQuantity; }
    public double getRewardPerBlock() { return rewardPerBlock; }

    public int getRemainingQuantity() { return remainingQuantity; }
    public int getCollectedQuantity() { return collectedQuantity; }
    public double getRemainingPot() { return remainingPot; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    /** Un joueur fournit "amount" blocs. Retourne la recompense a lui verser. */
    public double contribute(int amount) {
        int actual = Math.min(amount, remainingQuantity);
        double reward = actual * rewardPerBlock;
        remainingQuantity -= actual;
        collectedQuantity += actual;
        remainingPot -= reward;
        if (remainingQuantity <= 0 && status == Status.ACTIVE) {
            status = Status.COMPLETED;
        }
        return reward;
    }

    /** Le createur retire "amount" blocs deja collectes */
    public void withdraw(int amount) {
        collectedQuantity = Math.max(0, collectedQuantity - amount);
    }

    public boolean isBrowsable() {
        return status == Status.ACTIVE && remainingQuantity > 0;
    }

    /** Une fois annulee/terminee ET entierement recuperee, l'order peut etre supprimee */
    public boolean isFullyResolved() {
        return status != Status.ACTIVE && collectedQuantity <= 0;
    }
}
