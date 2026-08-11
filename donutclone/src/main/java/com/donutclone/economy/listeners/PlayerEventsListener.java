package com.donutclone.economy.listeners;

import com.donutclone.economy.EconomyShopPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerEventsListener implements Listener {

    private final EconomyShopPlugin plugin;

    public PlayerEventsListener(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getScoreboardManager().setup(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getRtpManager().isPending(player.getUniqueId())) return;

        // ignore les simples rotations de camera (meme bloc)
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        plugin.getRtpManager().cancel(player, "tu as bouge.");
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.getRtpManager().isPending(player.getUniqueId())) return;
        plugin.getRtpManager().cancel(player, "tu as pris des degats.");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        plugin.getStatsManager().addDeath(victim.getUniqueId());
        plugin.getScoreboardManager().update(victim);

        Player killer = victim.getKiller();
        if (killer != null) {
            plugin.getStatsManager().addKill(killer.getUniqueId());
            plugin.getScoreboardManager().update(killer);
        }
    }
}
