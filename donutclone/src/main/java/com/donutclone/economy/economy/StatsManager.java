package com.donutclone.economy.economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class StatsManager {

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration data;

    public StatsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        this.file = new File(dataFolder, "stats.yml");
        load();
    }

    private void load() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossible de creer stats.yml: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder stats.yml: " + e.getMessage());
        }
    }

    public int getKills(UUID uuid) {
        return data.getInt(uuid.toString() + ".kills", 0);
    }

    public int getDeaths(UUID uuid) {
        return data.getInt(uuid.toString() + ".deaths", 0);
    }

    public void addKill(UUID uuid) {
        data.set(uuid.toString() + ".kills", getKills(uuid) + 1);
        save();
    }

    public void addDeath(UUID uuid) {
        data.set(uuid.toString() + ".deaths", getDeaths(uuid) + 1);
        save();
    }
}
