package com.donutclone.economy.economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Systeme d'economie interne (pas besoin de Vault).
 * Les soldes sont stockes dans data/balances.yml
 */
public class EconomyManager {

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration data;
    private double startingBalance;

    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        this.file = new File(dataFolder, "balances.yml");
        this.startingBalance = plugin.getConfig().getDouble("starting-balance", 1000);
        load();
    }

    private void load() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossible de creer balances.yml: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder balances.yml: " + e.getMessage());
        }
    }

    public double getBalance(UUID uuid) {
        if (!data.contains(uuid.toString())) {
            data.set(uuid.toString(), startingBalance);
            save();
        }
        return data.getDouble(uuid.toString());
    }

    public void setBalance(UUID uuid, double amount) {
        data.set(uuid.toString(), Math.max(0, amount));
        save();
    }

    public void deposit(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public boolean withdraw(UUID uuid, double amount) {
        double bal = getBalance(uuid);
        if (bal < amount) return false;
        setBalance(uuid, bal - amount);
        return true;
    }

    public boolean has(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }
}
