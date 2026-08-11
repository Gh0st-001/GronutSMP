package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.util.ItemBuilder;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderMaterialPickerGUI {

    public static final int PER_PAGE = 36; // slots 9 a 44

    private final EconomyShopPlugin plugin;

    public OrderMaterialPickerGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        session.setPage(0);
        session.setSearchQuery("");
        session.setSortMode(GuiSession.SortMode.LOW_TO_HIGH); // = A -> Z par defaut
        render(player);
    }

    public void render(Player player) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        List<Material> materials = new ArrayList<>(plugin.getPriceManager().getAllShopableMaterials());

        String query = session.getSearchQuery();
        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase();
            materials.removeIf(m -> !m.name().toLowerCase().replace("_", " ").contains(q));
        }

        Comparator<Material> cmp = Comparator.comparing(this::prettyName, String.CASE_INSENSITIVE_ORDER);
        if (session.getSortMode() == GuiSession.SortMode.HIGH_TO_LOW) cmp = cmp.reversed();
        materials.sort(cmp);

        int totalPages = Math.max(1, (int) Math.ceil(materials.size() / (double) PER_PAGE));
        if (session.getPage() >= totalPages) session.setPage(totalPages - 1);
        if (session.getPage() < 0) session.setPage(0);

        Inventory inv = Bukkit.createInventory(null, 54, Menus.ORDER_MATERIAL_PICKER_TITLE);

        inv.setItem(0, new ItemBuilder(Material.OAK_SIGN)
                .name("&bRechercher")
                .lore(query == null || query.isBlank() ? "&7Aucune recherche active" : "&7Recherche: &f" + query,
                        "&7Clique et tape le nom d'un bloc")
                .build());

        boolean az = session.getSortMode() == GuiSession.SortMode.LOW_TO_HIGH;
        inv.setItem(1, new ItemBuilder(Material.HOPPER)
                .name("&6Tri: " + (az ? "&fA -> Z" : "&fZ -> A"))
                .lore("&7Clique pour inverser")
                .build());

        int start = session.getPage() * PER_PAGE;
        int end = Math.min(start + PER_PAGE, materials.size());
        List<Material> pageItems = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Material mat = materials.get(i);
            pageItems.add(mat);
            double price = plugin.getPriceManager().getPrice(mat);
            inv.setItem(9 + (i - start), new ItemBuilder(mat)
                    .name("&f" + prettyName(mat))
                    .lore("&7Prix boutique: &6" + NumberFormatter.formatMoney(price),
                            "",
                            "&aClique pour choisir ce bloc")
                    .build());
        }
        session.setCurrentPageItems(pageItems);

        inv.setItem(45, new ItemBuilder(Material.ARROW)
                .name("&e<< Page precedente")
                .lore("&7Page " + (session.getPage() + 1) + "/" + totalPages)
                .build());
        inv.setItem(53, new ItemBuilder(Material.ARROW)
                .name("&ePage suivante >>")
                .lore("&7Page " + (session.getPage() + 1) + "/" + totalPages)
                .build());

        player.openInventory(inv);
    }

    private String prettyName(Material mat) {
        String[] parts = mat.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        return sb.toString().trim();
    }
}
