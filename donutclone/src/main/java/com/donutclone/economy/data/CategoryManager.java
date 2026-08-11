package com.donutclone.economy.data;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CategoryManager {

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration data;
    private final LinkedHashMap<String, Category> categories = new LinkedHashMap<>();

    public CategoryManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "categories.yml");
        if (!file.exists()) {
            plugin.saveResource("categories.yml", false);
        }
        load();
    }

    public void load() {
        categories.clear();
        data = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = data.getConfigurationSection("categories");
        if (section == null) return;

        for (String id : section.getKeys(false)) {
            ConfigurationSection catSec = section.getConfigurationSection(id);
            if (catSec == null) continue;

            String displayName = catSec.getString("display-name", id);
            Material icon;
            try {
                icon = Material.valueOf(catSec.getString("icon", "CHEST").toUpperCase());
            } catch (IllegalArgumentException e) {
                icon = Material.CHEST;
            }
            int slot = catSec.getInt("slot", 0);

            Category category = new Category(id, displayName, icon, slot);
            List<String> items = catSec.getStringList("items");
            for (String itemName : items) {
                try {
                    category.addItem(Material.valueOf(itemName.toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }
            categories.put(id, category);
        }
    }

    public void save() {
        data.set("categories", null);
        for (Category cat : categories.values()) {
            String path = "categories." + cat.getId();
            data.set(path + ".display-name", cat.getDisplayName());
            data.set(path + ".icon", cat.getIcon().name());
            data.set(path + ".slot", cat.getSlot());
            List<String> items = new ArrayList<>();
            for (Material m : cat.getItems()) items.add(m.name());
            data.set(path + ".items", items);
        }
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur sauvegarde categories.yml: " + e.getMessage());
        }
    }

    public Collection<Category> getCategories() {
        return categories.values();
    }

    public Category getCategory(String id) {
        return categories.get(id);
    }

    public void addCategory(Category category) {
        categories.put(category.getId(), category);
        save();
    }

    public void removeCategory(String id) {
        categories.remove(id);
        save();
    }
}
