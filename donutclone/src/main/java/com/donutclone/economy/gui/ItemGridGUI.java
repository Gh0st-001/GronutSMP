package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
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

/**
 * Moteur generique pour afficher une grille paginee d'items (45 cases,
 * 5 lignes) avec une ligne de navigation en bas (recherche, tri, pages).
 * Utilise a la fois par /ah, les categories du /shop et /config.
 */
public class ItemGridGUI {

    public static final int ITEMS_PER_PAGE = 45;

    private final EconomyShopPlugin plugin;

    public enum GridMode { BUY_SELL, SET_PRICE, TOGGLE_CATEGORY }

    public ItemGridGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, String title, List<Material> materials, boolean adminMode) {
        open(player, title, materials, adminMode ? GridMode.SET_PRICE : GridMode.BUY_SELL);
    }

    public void open(Player player, String title, List<Material> materials, GridMode mode) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());

        List<Material> filtered = new ArrayList<>(materials);

        // recherche
        String query = session.getSearchQuery();
        if (query != null && !query.isBlank()) {
            filtered.removeIf(m -> !m.name().toLowerCase().replace("_", " ")
                    .contains(query.toLowerCase()));
        }

        // tri: si une recherche est active, priorite a la meilleure correspondance
        // (nom exact, puis commence par, puis contient) et le prix departage.
        // sinon tri simple par prix.
        Comparator<Material> priceComparator = Comparator.comparingDouble(m -> plugin.getPriceManager().getPrice(m));
        if (session.getSortMode() == GuiSession.SortMode.HIGH_TO_LOW) {
            priceComparator = priceComparator.reversed();
        }

        if (query != null && !query.isBlank()) {
            final String q = query.toLowerCase();
            Comparator<Material> relevance = Comparator.comparingInt(m -> relevanceScore(m, q));
            filtered.sort(relevance.thenComparing(priceComparator));
        } else {
            filtered.sort(priceComparator);
        }

        int totalPages = Math.max(1, (int) Math.ceil(filtered.size() / (double) ITEMS_PER_PAGE));
        if (session.getPage() >= totalPages) session.setPage(totalPages - 1);
        if (session.getPage() < 0) session.setPage(0);

        Inventory inv = Bukkit.createInventory(null, 54, title);

        int start = session.getPage() * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filtered.size());

        List<Material> pageItems = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Material mat = filtered.get(i);
            pageItems.add(mat);
            inv.setItem(i - start, buildItemIcon(mat, mode, session.getCurrentCategoryId()));
        }
        session.setCurrentPageItems(pageItems);
        session.setCurrentGridMode(mode);
        session.setLastTitle(title);
        session.setLastMaterialsSource(materials);

        // ligne de navigation (slots 45-53), le reste de la ligne reste vide

        // fleche precedente
        inv.setItem(45, new ItemBuilder(Material.ARROW)
                .name("&e<< Page precedente")
                .lore("&7Page actuelle: &f" + (session.getPage() + 1) + "/" + totalPages)
                .build());

        // tri (hopper)
        String sortLabel = session.getSortMode() == GuiSession.SortMode.LOW_TO_HIGH
                ? "&aPrix croissant" : "&cPrix decroissant";
        inv.setItem(48, new ItemBuilder(Material.HOPPER)
                .name("&6Trier: " + sortLabel)
                .lore("&7Clique pour inverser le tri")
                .build());

        // recherche (panneau/sign)
        inv.setItem(49, new ItemBuilder(Material.OAK_SIGN)
                .name("&bRechercher")
                .lore(
                        query == null || query.isBlank() ? "&7Aucune recherche active" : "&7Recherche: &f" + query,
                        "&7Clique et tape le nom d'un item",
                        "&7dans le chat"
                )
                .build());

        // fleche suivante
        inv.setItem(53, new ItemBuilder(Material.ARROW)
                .name("&ePage suivante >>")
                .lore("&7Page actuelle: &f" + (session.getPage() + 1) + "/" + totalPages)
                .build());

        player.openInventory(inv);
    }

    /** Rouvre le meme menu avec les memes filtres (utile apres un achat/vente/edit) */
    public void refresh(Player player) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        if (session.getLastTitle() == null) return;
        open(player, session.getLastTitle(), session.getLastMaterialsSource(), session.getCurrentGridMode());
    }

    private ItemStack buildItemIcon(Material mat, GridMode mode, String categoryId) {
        double price = plugin.getPriceManager().getPrice(mat);
        boolean hasPrice = plugin.getPriceManager().hasPrice(mat);

        ItemBuilder builder = new ItemBuilder(mat)
                .name("&f" + prettyName(mat));

        switch (mode) {
            case SET_PRICE -> builder.lore(
                    "&7Prix actuel: &6" + (hasPrice ? NumberFormatter.formatMoney(price) : "non defini"),
                    "",
                    "&eClic gauche &7-> definir le prix",
                    "&cClic droit &7-> retirer le prix (retire du shop)"
            );
            case TOGGLE_CATEGORY -> {
                boolean inCategory = categoryId != null
                        && plugin.getCategoryManager().getCategory(categoryId) != null
                        && plugin.getCategoryManager().getCategory(categoryId).getItems().contains(mat);
                builder.lore(
                        inCategory ? "&aDans cette categorie" : "&7Pas dans cette categorie",
                        "",
                        "&eClic gauche &7-> ajouter/retirer de la categorie"
                );
            }
            default -> builder.lore(
                    hasPrice ? "&7Prix: &6" + NumberFormatter.formatMoney(price) : "&7Non disponible a l'achat",
                    "",
                    hasPrice ? "&aClic gauche &7-> acheter 1" : "",
                    hasPrice ? "&aShift+clic gauche &7-> acheter 64" : "",
                    hasPrice ? "&cClic droit &7-> vendre 1 (depuis ton inventaire)" : "",
                    hasPrice ? "&cShift+clic droit &7-> vendre tout ce que tu as" : ""
            );
        }
        return builder.build();
    }

    private String prettyName(Material mat) {
        String[] parts = mat.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    /** 0 = correspondance exacte, 1 = commence par, 2 = un mot commence par, 3 = contient juste */
    private int relevanceScore(Material m, String query) {
        String name = m.name().toLowerCase().replace("_", " ");
        if (name.equals(query)) return 0;
        if (name.startsWith(query)) return 1;
        for (String word : name.split(" ")) {
            if (word.startsWith(query)) return 2;
        }
        return 3;
    }
}
