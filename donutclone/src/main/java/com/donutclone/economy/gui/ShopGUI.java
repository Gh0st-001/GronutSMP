package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.data.Category;
import com.donutclone.economy.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShopGUI {

    private final EconomyShopPlugin plugin;

    public ShopGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void openMain(Player player) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        session.setCurrentMenu(GuiSession.MenuType.SHOP);

        Inventory inv = Bukkit.createInventory(null, 45, Menus.SHOP_TITLE);

        // categories placees sur la 2eme ligne (slots 9-17), le reste reste vide
        for (Category category : plugin.getCategoryManager().getCategories()) {
            int slot = 9 + category.getSlot(); // 2eme ligne, relatif 0-8
            if (slot < 9 || slot > 17) continue;
            inv.setItem(slot, new ItemBuilder(category.getIcon())
                    .name("&e" + category.getDisplayName())
                    .lore("&7" + category.getItems().size() + " items",
                            "",
                            "&aClique pour ouvrir")
                    .build());
        }

        // recherche au centre (slot 40)
        inv.setItem(40, new ItemBuilder(Material.OAK_SIGN)
                .name("&bRechercher un item")
                .lore("&7Clique et tape le nom d'un item",
                        "&7Ouvrira l'hotel des ventes")
                .build());

        player.openInventory(inv);
    }

    public void openCategory(Player player, Category category) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        session.setCurrentMenu(GuiSession.MenuType.SHOP_CATEGORY);
        session.setCurrentCategoryId(category.getId());
        session.setPage(0);
        session.setSearchQuery("");

        plugin.getItemGridGUI().open(player, Menus.categoryTitle(category.getDisplayName()),
                category.getItems(), false);
    }
}
