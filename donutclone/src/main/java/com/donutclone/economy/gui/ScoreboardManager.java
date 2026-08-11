package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Petit affichage lateral (scoreboard vanilla, deja fond noir semi-transparent
 * par defaut cote client) avec MONEY / KILL / DEATH.
 */
public class ScoreboardManager {

    private static final String OBJECTIVE_ID = "es_stats";

    private final EconomyShopPlugin plugin;

    public ScoreboardManager(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective(OBJECTIVE_ID, "dummy",
                ChatColor.translateAlternateColorCodes('&', "&8&lSTATS"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(board);
        update(player);
    }

    public void update(Player player) {
        Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective(OBJECTIVE_ID);
        if (objective == null) {
            setup(player);
            return;
        }

        for (String entry : board.getEntries()) {
            board.resetScores(entry);
        }

        double money = plugin.getEconomyManager().getBalance(player.getUniqueId());
        int kills = plugin.getStatsManager().getKills(player.getUniqueId());
        int deaths = plugin.getStatsManager().getDeaths(player.getUniqueId());

        String moneyLine = ChatColor.translateAlternateColorCodes('&', "&aMONEY: &f$" + NumberFormatter.format(money));
        String killLine = ChatColor.translateAlternateColorCodes('&', "&cKILL: &f" + kills);
        String deathLine = ChatColor.translateAlternateColorCodes('&', "&6DEATH: &f" + deaths);

        objective.getScore(moneyLine).setScore(3);
        objective.getScore(killLine).setScore(2);
        objective.getScore(deathLine).setScore(1);
    }
}
