package com.donutclone.economy.gui;

import com.donutclone.economy.EconomyShopPlugin;
import org.bukkit.entity.Player;

public class AHGUI {

    private final EconomyShopPlugin plugin;

    public AHGUI(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        session.setCurrentMenu(GuiSession.MenuType.AH);
        session.setCurrentCategoryId(null);

        // /ah affiche tous les items ayant un prix defini (donc vendables/achetables)
        plugin.getItemGridGUI().open(player, Menus.AH_TITLE,
                plugin.getPriceManager().getAllPricedMaterials(), false);
    }
}
