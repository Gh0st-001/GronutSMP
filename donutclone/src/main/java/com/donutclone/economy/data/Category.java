package com.donutclone.economy.data;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private String id;
    private String displayName;
    private Material icon;
    private int slot;
    private final List<Material> items = new ArrayList<>();

    public Category(String id, String displayName, Material icon, int slot) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.slot = slot;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Material getIcon() { return icon; }
    public void setIcon(Material icon) { this.icon = icon; }
    public int getSlot() { return slot; }
    public void setSlot(int slot) { this.slot = slot; }
    public List<Material> getItems() { return items; }

    public void addItem(Material m) {
        if (!items.contains(m)) items.add(m);
    }

    public void removeItem(Material m) {
        items.remove(m);
    }
}
