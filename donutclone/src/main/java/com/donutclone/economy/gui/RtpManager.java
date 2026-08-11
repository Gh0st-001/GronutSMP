package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class RtpManager {

    // zone de generation: 100 000 blocs x 100 000 blocs, centree sur 0,0
    private static final int BOUND = 50_000;
    private static final int COUNTDOWN_SECONDS = 5;
    private static final int MAX_ATTEMPTS = 25;

    private final EconomyShopPlugin plugin;
    private final Set<UUID> pending = new HashSet<>();
    private final Random random = new Random();

    public RtpManager(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isPending(UUID uuid) {
        return pending.contains(uuid);
    }

    public void start(Player player, World world) {
        UUID uuid = player.getUniqueId();
        if (pending.contains(uuid)) {
            msg(player, "&cUne teleportation est deja en cours...");
            return;
        }
        pending.add(uuid);

        new BukkitRunnable() {
            int secondsLeft = COUNTDOWN_SECONDS;

            @Override
            public void run() {
                if (!player.isOnline() || !pending.contains(uuid)) {
                    cancel();
                    return;
                }

                if (secondsLeft <= 0) {
                    pending.remove(uuid);
                    attemptTeleport(player, world, MAX_ATTEMPTS);
                    cancel();
                    return;
                }

                actionBar(player, "&bTeleportation dans " + secondsLeft + "s...");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void cancel(Player player, String reason) {
        if (pending.remove(player.getUniqueId())) {
            msg(player, "&cTeleportation annulee: " + reason);
        }
    }

    private void attemptTeleport(Player player, World world, int attemptsLeft) {
        if (!player.isOnline()) return;
        if (attemptsLeft <= 0) {
            msg(player, "&cAucun endroit sur trouve, reessaie.");
            return;
        }

        int x = random.nextInt(BOUND * 2) - BOUND;
        int z = random.nextInt(BOUND * 2) - BOUND;

        world.getChunkAtAsync(x >> 4, z >> 4).thenAccept(chunk ->
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Integer y = (world.getEnvironment() == World.Environment.NETHER)
                            ? findSafeYCaveStyle(world, x, z)
                            : findSurfaceY(world, x, z);

                    if (y == null) {
                        attemptTeleport(player, world, attemptsLeft - 1);
                        return;
                    }
                    Location loc = new Location(world, x + 0.5, y, z + 0.5);
                    player.teleport(loc);
                    player.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    msg(player, "&aTeleporte en &f" + x + ", " + y + ", " + z + "&a !");
                })
        );
    }

    /**
     * Overworld / End: utilise les heightmaps de Minecraft pour trouver le
     * vrai sol de surface. Si OCEAN_FLOOR != WORLD_SURFACE, ca veut dire
     * qu'il y a de l'eau/glace au dessus du sol a cet endroit (ocean, lac...)
     * donc on rejette cet endroit plutot que de plonger dessous.
     */
    private Integer findSurfaceY(World world, int x, int z) {
        int floorY = world.getHighestBlockYAt(x, z, org.bukkit.HeightMap.OCEAN_FLOOR);
        int surfaceY = world.getHighestBlockYAt(x, z, org.bukkit.HeightMap.WORLD_SURFACE);

        if (floorY != surfaceY) return null; // eau/glace au dessus -> on rejette (ocean/lac)
        if (floorY <= world.getMinHeight() + 1) return null; // rien trouve / vide

        Material ground = world.getBlockAt(x, floorY, z).getType();
        if (isDangerous(ground) || !ground.isSolid()) return null;

        return floorY + 1;
    }

    /**
     * Nether: pas de "surface" au sens overworld (il y a un plafond), donc on
     * scanne de haut en bas pour trouver le premier sol solide avec 2 blocs
     * d'air au dessus, en excluant lave/eau.
     */
    private Integer findSafeYCaveStyle(World world, int x, int z) {
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();

        for (int y = maxY - 3; y > minY; y--) {
            Block ground = world.getBlockAt(x, y, z);
            Block above1 = world.getBlockAt(x, y + 1, z);
            Block above2 = world.getBlockAt(x, y + 2, z);

            Material groundType = ground.getType();
            if (!groundType.isSolid()) continue;
            if (isDangerous(groundType)) continue;
            if (!isSafeAir(above1.getType()) || !isSafeAir(above2.getType())) continue;

            return y + 1;
        }
        return null;
    }

    private boolean isDangerous(Material m) {
        return m == Material.LAVA || m == Material.WATER || m == Material.MAGMA_BLOCK
                || m == Material.FIRE || m == Material.SOUL_FIRE || m == Material.CACTUS;
    }

    private boolean isSafeAir(Material m) {
        return m == Material.AIR || m == Material.CAVE_AIR || m == Material.VOID_AIR;
    }

    private void actionBar(Player player, String text) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(ChatColor.translateAlternateColorCodes('&', text)));
    }

    private void msg(Player player, String text) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }
}
