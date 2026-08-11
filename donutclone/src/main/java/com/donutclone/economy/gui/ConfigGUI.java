package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.data.Category;
import com.donutclone.economy.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ConfigGUI {

    public static final String MAIN_TITLE = "\u00A78Config - Panel admin";
    public static final String CATEGORIES_LIST_TITLE = "\u00A78Config - Liste categories";

    private final EconomyShopPlugin plugin;

    public ConfigGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void openMain(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, MAIN_TITLE);

        inv.setItem(11, new ItemBuilder(Material.GOLD_INGOT)
                .name("&6Gerer les prix")
                .lore("&7Modifier le prix de n'importe quel item",
                        "&7Clique pour ouvrir")
                .build());

        inv.setItem(15, new ItemBuilder(Material.CHEST)
                .name("&eGerer les categories")
                .lore("&7Creer/modifier les categories du /shop",
                        "&7et leur contenu",
                        "&7Clique pour ouvrir")
                .build());

        player.openInventory(inv);
    }

    public void openPrices(Player player) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        session.setCurrentMenu(GuiSession.MenuType.CONFIG_PRICES);
        session.setPage(0);
        session.setSearchQuery("");
        session.setCurrentCategoryId(null);

        plugin.getItemGridGUI().open(player, Menus.CONFIG_TITLE,
                plugin.getPriceManager().getAllShopableMaterials(), ItemGridGUI.GridMode.SET_PRICE);
    }

    public void openCategoriesList(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, CATEGORIES_LIST_TITLE);

        int slot = 0;
        for (Category category : plugin.getCategoryManager().getCategories()) {
            inv.setItem(slot++, new ItemBuilder(category.getIcon())
                    .name("&e" + category.getDisplayName())
                    .lore("&7ID: &f" + category.getId(),
                            "&7Slot dans /shop: &f" + category.getSlot(),
                            "&7Items: &f" + category.getItems().size(),
                            "",
                            "&aClic gauche &7-> gerer les items",
                            "&bClic droit &7-> changer le slot (chat)")
                    .build());
        }

        inv.setItem(53, new ItemBuilder(Material.LIME_DYE)
                .name("&aCreer une nouvelle categorie")
                .lore("&7Clique et tape le nom (id) de la categorie")
                .build());

        player.openInventory(inv);
    }

    public void openCategoryItems(Player player, Category category) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        session.setCurrentMenu(GuiSession.MenuType.CONFIG_CATEGORY_ITEMS);
        session.setCurrentCategoryId(category.getId());
        session.setPage(0);
        session.setSearchQuery("");

        plugin.getItemGridGUI().open(player, Menus.configCategoryItemsTitle(category.getDisplayName()),
                plugin.getPriceManager().getAllShopableMaterials(), ItemGridGUI.GridMode.TOGGLE_CATEGORY);
    }
}
