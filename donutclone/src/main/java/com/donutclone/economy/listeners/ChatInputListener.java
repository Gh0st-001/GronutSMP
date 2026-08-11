package com.donutclone.economy.listeners;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.data.Category;
import com.donutclone.economy.gui.GuiSession;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatInputListener implements Listener {

    private final EconomyShopPlugin plugin;

    public ChatInputListener(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());

        if (session.getAwaitingInput() == GuiSession.AwaitingInput.NONE) return;

        event.setCancelled(true);
        String message = event.getMessage().trim();

        plugin.getServer().getScheduler().runTask(plugin, () -> handleInput(player, session, message));
    }

    private void handleInput(Player player, GuiSession session, String message) {
        switch (session.getAwaitingInput()) {
            case SEARCH_GENERIC -> handleSearch(player, session, message);
            case SET_PRICE -> handleSetPrice(player, session, message);
            case NEW_CATEGORY_NAME -> handleNewCategory(player, session, message);
            case SET_CATEGORY_SLOT -> handleSetCategorySlot(player, session, message);
            case ORDER_BROWSE_SEARCH -> handleOrderBrowseSearch(player, session, message);
            case ORDER_PICKER_SEARCH -> handleOrderPickerSearch(player, session, message);
            default -> {}
        }
    }

    private void handleSearch(Player player, GuiSession session, String message) {
        session.setSearchQuery(message);
        session.setPage(0);
        session.setAwaitingInput(GuiSession.AwaitingInput.NONE);

        if (session.isSearchFromMainShop()) {
            plugin.getAhGUI().open(player);
        } else if (session.getLastTitle() != null) {
            plugin.getItemGridGUI().open(player, session.getLastTitle(),
                    session.getLastMaterialsSource(), session.getCurrentGridMode());
        } else {
            plugin.getAhGUI().open(player);
        }
    }

    private void handleSetPrice(Player player, GuiSession session, String message) {
        double value = NumberFormatter.parse(message);
        if (value < 0) {
            msg(player, "&cFormat invalide. Exemple: 40K, 2.5M, 1000. Retape une valeur :");
            return; // reste en attente
        }
        Material material = session.getPendingMaterial();
        plugin.getPriceManager().setPrice(material, value);
        session.setAwaitingInput(GuiSession.AwaitingInput.NONE);
        msg(player, "&aPrix de &f" + material.name() + " &adefini a " + NumberFormatter.formatMoney(value) + ".");
        plugin.getConfigGUI().openPrices(player);
    }

    private void handleNewCategory(Player player, GuiSession session, String message) {
        String id = message.toLowerCase().replace(" ", "_").replaceAll("[^a-z0-9_]", "");
        if (id.isBlank()) {
            msg(player, "&cNom invalide, retape un nom :");
            return;
        }
        if (plugin.getCategoryManager().getCategory(id) != null) {
            msg(player, "&cCette categorie existe deja, choisis un autre nom :");
            return;
        }

        int freeSlot = 0;
        outer:
        for (int s = 0; s < 9; s++) {
            for (Category c : plugin.getCategoryManager().getCategories()) {
                if (c.getSlot() == s) continue outer;
            }
            freeSlot = s;
            break;
        }

        Category category = new Category(id, message, Material.CHEST, freeSlot);
        plugin.getCategoryManager().addCategory(category);
        session.setAwaitingInput(GuiSession.AwaitingInput.NONE);
        msg(player, "&aCategorie &f" + message + " &acreee (slot " + freeSlot + "). Change l'icone via /config si besoin.");
        plugin.getConfigGUI().openCategoriesList(player);
    }

    private void handleSetCategorySlot(Player player, GuiSession session, String message) {
        int newSlot;
        try {
            newSlot = Integer.parseInt(message.trim());
        } catch (NumberFormatException e) {
            msg(player, "&cCe n'est pas un nombre. Tape un slot entre 0 et 8 :");
            return;
        }
        if (newSlot < 0 || newSlot > 8) {
            msg(player, "&cLe slot doit etre entre 0 et 8. Retape :");
            return;
        }

        Category category = plugin.getCategoryManager().getCategory(session.getPendingCategoryId());
        if (category != null) {
            category.setSlot(newSlot);
            plugin.getCategoryManager().save();
            msg(player, "&aEmplacement de &f" + category.getDisplayName() + " &adefini a " + newSlot + ".");
        }
        session.setAwaitingInput(GuiSession.AwaitingInput.NONE);
        plugin.getConfigGUI().openCategoriesList(player);
    }

    private void handleOrderBrowseSearch(Player player, GuiSession session, String message) {
        session.setSearchQuery(message);
        session.setPage(0);
        session.setAwaitingInput(GuiSession.AwaitingInput.NONE);
        plugin.getOrderBrowseGUI().render(player);
    }

    private void handleOrderPickerSearch(Player player, GuiSession session, String message) {
        session.setSearchQuery(message);
        session.setPage(0);
        session.setAwaitingInput(GuiSession.AwaitingInput.NONE);
        plugin.getOrderMaterialPickerGUI().render(player);
    }

    private void msg(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
