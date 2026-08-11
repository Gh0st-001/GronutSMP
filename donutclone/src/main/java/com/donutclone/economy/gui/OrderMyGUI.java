package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.data.Order;
import com.donutclone.economy.util.ItemBuilder;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Gestion de sa propre commande: coffre de collecte des blocs deja fournis
 * par d'autres joueurs (toujours rempli depuis la 1ere page), et bouton
 * d'annulation en bas a gauche.
 */
public class OrderMyGUI {

    public static final int PER_PAGE = 45;
    public static final int STACK_SIZE = 64;

    private final EconomyShopPlugin plugin;

    public OrderMyGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, Order order) {
        plugin.getSessionManager().get(player.getUniqueId()).setPage(0);
        render(player, order);
    }

    public void render(Player player, Order order) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        int collected = order.getCollectedQuantity();

        int totalStacks = (int) Math.ceil(collected / (double) STACK_SIZE);
        int totalPages = Math.max(1, (int) Math.ceil(totalStacks / (double) PER_PAGE));
        if (session.getPage() >= totalPages) session.setPage(totalPages - 1);
        if (session.getPage() < 0) session.setPage(0);

        Inventory inv = Bukkit.createInventory(null, 54, Menus.ORDER_MY_TITLE);

        // remplit toujours depuis la case 0 -> jamais besoin de scroller pour trouver ses blocs
        int remainingForThisPage = collected - session.getPage() * PER_PAGE * STACK_SIZE;
        for (int slot = 0; slot < PER_PAGE && remainingForThisPage > 0; slot++) {
            int amount = Math.min(STACK_SIZE, remainingForThisPage);
            inv.setItem(slot, new ItemBuilder(order.getMaterial())
                    .amount(amount)
                    .name("&fClique pour recuperer")
                    .build());
            remainingForThisPage -= amount;
        }

        inv.setItem(45, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .name("&cAnnuler la commande")
                .lore("&7Rembourse la partie non utilisee", "&7de la recompense")
                .build());

        inv.setItem(49, new ItemBuilder(order.getMaterial())
                .name("&f" + prettyName(order.getMaterial()))
                .lore(
                        "&7Quantite restante a fournir: &f" + order.getRemainingQuantity(),
                        "&7En attente de recuperation: &f" + order.getCollectedQuantity(),
                        "&7Pot restant: &6" + NumberFormatter.formatMoney(order.getRemainingPot()),
                        "&7Statut: &f" + statusLabel(order)
                )
                .build());

        if (totalPages > 1) {
            inv.setItem(48, new ItemBuilder(Material.ARROW).name("&e<< Page precedente").build());
            inv.setItem(50, new ItemBuilder(Material.ARROW).name("&ePage suivante >>").build());
        }

        player.openInventory(inv);
    }

    private String statusLabel(Order order) {
        return switch (order.getStatus()) {
            case ACTIVE -> "&aEn cours";
            case COMPLETED -> "&6Terminee (a recuperer)";
            case CANCELLED -> "&cAnnulee (a recuperer)";
        };
    }

    private String prettyName(Material mat) {
        String[] parts = mat.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        return sb.toString().trim();
    }
}
