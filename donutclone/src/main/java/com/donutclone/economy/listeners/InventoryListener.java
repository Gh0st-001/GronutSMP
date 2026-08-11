package com.donutclone.economy.listeners;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.data.Category;
import com.donutclone.economy.gui.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryListener implements Listener {

    private final EconomyShopPlugin plugin;

    public InventoryListener(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (isOurMenu(title)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!isOurMenu(title)) return;

        event.setCancelled(true);

        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(event.getView().getTopInventory())) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());

        if (title.equals(Menus.SHOP_TITLE)) {
            handleShopMain(player, slot);
        } else if (title.equals(ConfigGUI.MAIN_TITLE)) {
            handleConfigMain(player, slot);
        } else if (title.equals(ConfigGUI.CATEGORIES_LIST_TITLE)) {
            handleCategoriesList(player, slot, event.getClick());
        } else if (title.equals(Menus.BUY_CONFIRM_TITLE)) {
            handleBuyConfirm(player, slot);
        } else if (title.equals(Menus.RTP_TITLE)) {
            handleRtpChoice(player, slot);
        } else {
            // tous les menus grille (Boutique > X, Hotel des ventes, Config - Prix, Config > X)
            handleItemGrid(player, session, slot, event.getClick());
        }
    }

    private boolean isOurMenu(String title) {
        return title.equals(Menus.SHOP_TITLE)
                || title.equals(Menus.AH_TITLE)
                || title.equals(Menus.CONFIG_TITLE)
                || title.equals(ConfigGUI.MAIN_TITLE)
                || title.equals(ConfigGUI.CATEGORIES_LIST_TITLE)
                || title.equals(Menus.BUY_CONFIRM_TITLE)
                || title.equals(Menus.RTP_TITLE)
                || title.startsWith("\u00A78Boutique > ")
                || title.startsWith("\u00A78Config > ");
    }

    // ---------- /shop menu principal ----------
    private void handleShopMain(Player player, int slot) {
        if (slot == 40) {
            // recherche -> ouvre l'hotel des ventes filtre
            GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
            session.setAwaitingInput(GuiSession.AwaitingInput.SEARCH_GENERIC);
            session.setSearchFromMainShop(true);
            player.closeInventory();
            msg(player, "&bTape dans le chat le nom de l'item que tu cherches :");
            return;
        }
        if (slot < 9 || slot > 17) return;
        int relative = slot - 9;
        for (Category category : plugin.getCategoryManager().getCategories()) {
            if (category.getSlot() == relative) {
                plugin.getShopGUI().openCategory(player, category);
                return;
            }
        }
    }

    // ---------- /config menu principal ----------
    private void handleConfigMain(Player player, int slot) {
        if (slot == 11) {
            plugin.getConfigGUI().openPrices(player);
        } else if (slot == 15) {
            plugin.getConfigGUI().openCategoriesList(player);
        }
    }

    // ---------- /config -> liste des categories ----------
    private void handleCategoriesList(Player player, int slot, ClickType click) {
        if (slot == 53) {
            GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
            session.setAwaitingInput(GuiSession.AwaitingInput.NEW_CATEGORY_NAME);
            player.closeInventory();
            msg(player, "&bTape dans le chat le nom de la nouvelle categorie :");
            return;
        }

        List<Category> categories = new ArrayList<>(plugin.getCategoryManager().getCategories());
        if (slot < 0 || slot >= categories.size()) return;
        Category category = categories.get(slot);

        if (click.isRightClick()) {
            GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
            session.setAwaitingInput(GuiSession.AwaitingInput.SET_CATEGORY_SLOT);
            session.setPendingCategoryId(category.getId());
            player.closeInventory();
            msg(player, "&bTape dans le chat le nouvel emplacement (0 a 8) pour &f" + category.getDisplayName());
        } else {
            plugin.getConfigGUI().openCategoryItems(player, category);
        }
    }

    // ---------- menus grille : /shop categorie, /ah, /config prix, /config categorie items ----------
    private void handleItemGrid(Player player, GuiSession session, int slot, ClickType click) {
        if (slot == 45) {
            session.setPage(session.getPage() - 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            plugin.getItemGridGUI().refresh(player);
            return;
        }
        if (slot == 48) {
            session.toggleSort();
            plugin.getItemGridGUI().refresh(player);
            return;
        }
        if (slot == 49) {
            session.setAwaitingInput(GuiSession.AwaitingInput.SEARCH_GENERIC);
            session.setSearchFromMainShop(false);
            player.closeInventory();
            msg(player, "&bTape dans le chat le nom de l'item que tu cherches :");
            return;
        }
        if (slot == 53) {
            session.setPage(session.getPage() + 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            plugin.getItemGridGUI().refresh(player);
            return;
        }
        if (slot >= 45) return; // autre case de la barre de nav (filler)

        List<Material> pageItems = session.getCurrentPageItems();
        if (slot < 0 || slot >= pageItems.size()) return;
        Material material = pageItems.get(slot);

        switch (session.getCurrentGridMode()) {
            case BUY_SELL -> handleBuySell(player, material, click);
            case SET_PRICE -> handleSetPrice(player, session, material, click);
            case TOGGLE_CATEGORY -> handleToggleCategory(player, session, material);
        }
    }

    private void handleBuySell(Player player, Material material, ClickType click) {
        if (click == ClickType.LEFT || click == ClickType.SHIFT_LEFT) {
            plugin.getBuyConfirmGUI().open(player, material);
        } else if (click == ClickType.RIGHT) {
            plugin.getShopService().sell(player, material, 1);
            plugin.getItemGridGUI().refresh(player);
        } else if (click == ClickType.SHIFT_RIGHT) {
            plugin.getShopService().sellAll(player, material);
            plugin.getItemGridGUI().refresh(player);
        }
    }

    // ---------- confirmation d'achat ----------
    private void handleBuyConfirm(Player player, int slot) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        Material material = session.getPendingMaterial();
        if (material == null) return;

        if (slot == 11) {
            player.closeInventory();
        } else if (slot == 15) {
            plugin.getShopService().buy(player, material, 1);
        } else if (slot == 16) {
            plugin.getShopService().buy(player, material, material.getMaxStackSize());
        }
        // le menu ne se ferme pas tout seul: on peut spam-cliquer, sauf Annuler/Echap
    }

    // ---------- choix de destination /rtp ----------
    private void handleRtpChoice(Player player, int slot) {
        World.Environment env;
        if (slot == 11) env = World.Environment.NORMAL;
        else if (slot == 13) env = World.Environment.NETHER;
        else if (slot == 15) env = World.Environment.THE_END;
        else return;

        World world = null;
        for (World w : Bukkit.getServer().getWorlds()) {
            if (w.getEnvironment() == env) { world = w; break; }
        }
        if (world == null) {
            msg(player, "&cCe monde n'est pas disponible sur le serveur.");
            return;
        }
        player.closeInventory();
        plugin.getRtpManager().start(player, world);
    }

    private void handleSetPrice(Player player, GuiSession session, Material material, ClickType click) {
        if (click.isLeftClick()) {
            session.setAwaitingInput(GuiSession.AwaitingInput.SET_PRICE);
            session.setPendingMaterial(material);
            player.closeInventory();
            msg(player, "&bTape dans le chat le nouveau prix pour &f" + material.name()
                    + " &b(ex: 40K, 2.5M, 1000) :");
        } else if (click.isRightClick()) {
            plugin.getPriceManager().removePrice(material);
            msg(player, "&cPrix retire pour " + material.name() + ".");
            plugin.getItemGridGUI().refresh(player);
        }
    }

    private void handleToggleCategory(Player player, GuiSession session, Material material) {
        Category category = plugin.getCategoryManager().getCategory(session.getCurrentCategoryId());
        if (category == null) return;

        if (category.getItems().contains(material)) {
            category.removeItem(material);
            msg(player, "&c" + material.name() + " retire de la categorie " + category.getDisplayName());
        } else {
            category.addItem(material);
            msg(player, "&a" + material.name() + " ajoute a la categorie " + category.getDisplayName());
        }
        plugin.getCategoryManager().save();
        plugin.getItemGridGUI().refresh(player);
    }

    private void msg(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
