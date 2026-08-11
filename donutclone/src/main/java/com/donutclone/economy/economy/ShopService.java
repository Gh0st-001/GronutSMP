package com.donutclone.economy.economy;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.util.ActionBar;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopService {

    private final EconomyShopPlugin plugin;

    public ShopService(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void buy(Player player, Material material, int amount) {
        double unitPrice = plugin.getPriceManager().getPrice(material);
        if (!plugin.getPriceManager().hasPrice(material) || unitPrice <= 0) {
            msg(player, "&cCet item n'est pas disponible a l'achat.");
            return;
        }

        double total = unitPrice * amount;
        var economy = plugin.getEconomyManager();

        if (!economy.has(player.getUniqueId(), total)) {
            msg(player, "&cTu n'as pas assez d'argent. Il te faut " + NumberFormatter.formatMoney(total) + ".");
            return;
        }

        economy.withdraw(player.getUniqueId(), total);
        ItemStack stack = new ItemStack(material, amount);
        // addItem empile automatiquement sur les stacks existants; ce qui ne rentre pas
        // (inventaire plein) est simplement lache au sol pour ne jamais perdre l'achat.
        player.getInventory().addItem(stack).values().forEach(left -> player.getWorld().dropItem(player.getLocation(), left));

        msg(player, "&aAchete &f" + amount + "x " + prettyName(material) + " &apour " + NumberFormatter.formatMoney(total) + ".");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        plugin.getScoreboardManager().update(player);
    }

    public void sell(Player player, Material material, int amount) {
        double unitPrice = plugin.getPriceManager().getPrice(material);
        if (!plugin.getPriceManager().hasPrice(material) || unitPrice <= 0) {
            msg(player, "&cCet item ne peut pas etre vendu ici.");
            return;
        }

        int owned = countItems(player, material);
        int toSell = Math.min(owned, amount);

        if (toSell <= 0) {
            msg(player, "&cTu n'as pas cet item dans ton inventaire.");
            return;
        }

        removeItems(player, material, toSell);
        double total = unitPrice * toSell;
        plugin.getEconomyManager().deposit(player.getUniqueId(), total);

        ActionBar.send(player, "&aVendu pour " + NumberFormatter.formatMoney(total) + " !");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        plugin.getScoreboardManager().update(player);
    }

    public void sellAll(Player player, Material material) {
        int owned = countItems(player, material);
        if (owned <= 0) {
            msg(player, "&cTu n'as pas cet item dans ton inventaire.");
            return;
        }
        sell(player, material, owned);
    }

    private int countItems(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) count += item.getAmount();
        }
        return count;
    }

    private void removeItems(Player player, Material material, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == material) {
                int take = Math.min(remaining, item.getAmount());
                item.setAmount(item.getAmount() - take);
                remaining -= take;
                if (item.getAmount() <= 0) {
                    player.getInventory().setItem(i, null);
                } else {
                    player.getInventory().setItem(i, item);
                }
            }
        }
    }

    private String prettyName(Material mat) {
        String[] parts = mat.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        return sb.toString().trim();
    }

    private void msg(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
