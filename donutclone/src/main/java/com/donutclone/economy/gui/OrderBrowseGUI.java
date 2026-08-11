package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.data.Order;
import com.donutclone.economy.util.ItemBuilder;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderBrowseGUI {

    public static final int PER_PAGE = 45;

    private final EconomyShopPlugin plugin;

    public OrderBrowseGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        session.setPage(0);
        session.setSearchQuery("");
        session.setSortMode(GuiSession.SortMode.HIGH_TO_LOW);
        render(player);
    }

    public void render(Player player) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        List<Order> orders = new ArrayList<>(plugin.getOrderManager().getBrowsableOrders());

        String query = session.getSearchQuery();
        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase();
            orders.removeIf(o -> !o.getMaterial().name().toLowerCase().replace("_", " ").contains(q));
        }

        Comparator<Order> cmp = Comparator.comparingDouble(Order::getRewardPerBlock);
        if (session.getSortMode() == GuiSession.SortMode.HIGH_TO_LOW) cmp = cmp.reversed();
        orders.sort(cmp);

        int totalPages = Math.max(1, (int) Math.ceil(orders.size() / (double) PER_PAGE));
        if (session.getPage() >= totalPages) session.setPage(totalPages - 1);
        if (session.getPage() < 0) session.setPage(0);

        Inventory inv = Bukkit.createInventory(null, 54, Menus.ORDER_BROWSE_TITLE);

        int start = session.getPage() * PER_PAGE;
        int end = Math.min(start + PER_PAGE, orders.size());
        List<java.util.UUID> pageOrders = new ArrayList<>();

        for (int i = start; i < end; i++) {
            Order o = orders.get(i);
            pageOrders.add(o.getId());
            inv.setItem(i - start, buildOrderIcon(o));
        }
        session.setCurrentPageOrders(pageOrders);

        inv.setItem(45, new ItemBuilder(Material.ARROW)
                .name("&e<< Page precedente")
                .lore("&7Page " + (session.getPage() + 1) + "/" + totalPages)
                .build());

        boolean desc = session.getSortMode() == GuiSession.SortMode.HIGH_TO_LOW;
        inv.setItem(48, new ItemBuilder(Material.HOPPER)
                .name("&6Trier: " + (desc ? "&fRecompense decroissante" : "&fRecompense croissante"))
                .lore("&7Clique pour inverser")
                .build());

        inv.setItem(49, new ItemBuilder(Material.OAK_SIGN)
                .name("&bRechercher")
                .lore(query == null || query.isBlank() ? "&7Aucune recherche active" : "&7Recherche: &f" + query,
                        "&7Clique et tape le nom d'un bloc")
                .build());

        inv.setItem(50, new ItemBuilder(Material.CHEST)
                .name("&6Ma commande")
                .lore("&7Creer une nouvelle commande", "&7ou gerer celle en cours")
                .build());

        inv.setItem(53, new ItemBuilder(Material.ARROW)
                .name("&ePage suivante >>")
                .lore("&7Page " + (session.getPage() + 1) + "/" + totalPages)
                .build());

        player.openInventory(inv);
    }

    private ItemStack buildOrderIcon(Order o) {
        return new ItemBuilder(o.getMaterial())
                .name("&f" + prettyName(o.getMaterial()))
                .lore(
                        "&7Bloc demande: &f" + prettyName(o.getMaterial()),
                        "&7Quantite restante: &f" + o.getRemainingQuantity(),
                        "&7Recompense totale: &6" + NumberFormatter.formatMoney(o.getRemainingPot()),
                        "&7Recompense par bloc: &6" + NumberFormatter.formatMoney(o.getRewardPerBlock()) + " / bloc",
                        "&7Cree par: &f" + o.getCreatorName(),
                        "",
                        "&aClique pour fournir des blocs"
                )
                .build();
    }

    private String prettyName(Material mat) {
        String[] parts = mat.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        return sb.toString().trim();
    }
}
