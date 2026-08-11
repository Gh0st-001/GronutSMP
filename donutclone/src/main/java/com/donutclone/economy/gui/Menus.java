package com.donutclone.economy.gui;

public class Menus {
    public static final String SHOP_TITLE = "\u00A78Boutique";
    public static final String AH_TITLE = "\u00A78Hotel des ventes";
    public static final String CONFIG_TITLE = "\u00A78Config - Prix";
    public static final String CONFIG_CATEGORIES_TITLE = "\u00A78Config - Categories";
    public static final String SELL_TITLE = "\u00A78Vendre";
    public static final String SELL_CONFIRM_TITLE = "\u00A78Confirmer la vente";
    public static final String BUY_CONFIRM_TITLE = "\u00A78Confirmer l'achat";
    public static final String RTP_TITLE = "\u00A78Choisir une destination";
    public static final String ORDER_BROWSE_TITLE = "\u00A78Commandes de recolte";
    public static final String ORDER_MATERIAL_PICKER_TITLE = "\u00A78Choisir un bloc";
    public static final String ORDER_RECAP_TITLE = "\u00A78Recapitulatif de la commande";
    public static final String ORDER_MY_TITLE = "\u00A78Ma commande";
    public static final String ORDER_FULFILL_DEPOSIT_TITLE = "\u00A78Fournir des blocs";
    public static final String ORDER_FULFILL_CONFIRM_TITLE = "\u00A78Confirmer la fourniture";
    public static final String ORDER_CANCEL_CONFIRM_TITLE = "\u00A78Annuler la commande ?";

    public static String categoryTitle(String displayName) {
        return "\u00A78Boutique > " + org.bukkit.ChatColor.translateAlternateColorCodes('&', displayName);
    }

    public static String configCategoryItemsTitle(String displayName) {
        return "\u00A78Config > " + org.bukkit.ChatColor.translateAlternateColorCodes('&', displayName);
    }
}
