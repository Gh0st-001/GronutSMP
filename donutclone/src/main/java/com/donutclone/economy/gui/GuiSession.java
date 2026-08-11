package com.donutclone.economy.gui;

import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

/**
 * Etat de navigation d'un joueur dans les menus (page, tri, categorie,
 * et ce qu'on attend de lui dans le chat s'il doit taper quelque chose).
 */
public class GuiSession {

    public enum SortMode { LOW_TO_HIGH, HIGH_TO_LOW }
    public enum MenuType { SHOP, SHOP_CATEGORY, AH, CONFIG_PRICES, CONFIG_CATEGORY_ITEMS, CONFIG_CATEGORY_SLOT }

    public enum AwaitingInput {
        NONE, SEARCH_GENERIC, SET_PRICE, NEW_CATEGORY_NAME, SET_CATEGORY_SLOT,
        ORDER_BROWSE_SEARCH, ORDER_PICKER_SEARCH
    }

    private MenuType currentMenu = MenuType.SHOP;
    private String currentCategoryId;
    private int page = 0;
    private SortMode sortMode = SortMode.LOW_TO_HIGH;
    private String searchQuery = "";

    private AwaitingInput awaitingInput = AwaitingInput.NONE;
    private Material pendingMaterial; // pour SET_PRICE par exemple

    // mapping slot -> material actuellement affiche (rempli par ItemGridGUI a chaque ouverture)
    private java.util.List<Material> currentPageItems = new java.util.ArrayList<>();
    private ItemGridGUI.GridMode currentGridMode = ItemGridGUI.GridMode.BUY_SELL;

    public java.util.List<Material> getCurrentPageItems() { return currentPageItems; }
    public void setCurrentPageItems(java.util.List<Material> items) { this.currentPageItems = items; }
    public ItemGridGUI.GridMode getCurrentGridMode() { return currentGridMode; }
    public void setCurrentGridMode(ItemGridGUI.GridMode mode) { this.currentGridMode = mode; }

    // pour pouvoir rafraichir/rouvrir le meme menu apres une action
    private String lastTitle;
    private java.util.List<Material> lastMaterialsSource = new java.util.ArrayList<>();

    public String getLastTitle() { return lastTitle; }
    public void setLastTitle(String lastTitle) { this.lastTitle = lastTitle; }
    public java.util.List<Material> getLastMaterialsSource() { return lastMaterialsSource; }
    public void setLastMaterialsSource(java.util.List<Material> list) { this.lastMaterialsSource = list; }

    private String pendingCategoryId;
    private boolean searchFromMainShop = false;

    public String getPendingCategoryId() { return pendingCategoryId; }
    public void setPendingCategoryId(String pendingCategoryId) { this.pendingCategoryId = pendingCategoryId; }
    public boolean isSearchFromMainShop() { return searchFromMainShop; }
    public void setSearchFromMainShop(boolean searchFromMainShop) { this.searchFromMainShop = searchFromMainShop; }

    // mapping slot -> orderId actuellement affiche dans /order
    private java.util.List<UUID> currentPageOrders = new java.util.ArrayList<>();
    public java.util.List<UUID> getCurrentPageOrders() { return currentPageOrders; }
    public void setCurrentPageOrders(java.util.List<UUID> orders) { this.currentPageOrders = orders; }

    public MenuType getCurrentMenu() { return currentMenu; }
    public void setCurrentMenu(MenuType currentMenu) { this.currentMenu = currentMenu; }

    public String getCurrentCategoryId() { return currentCategoryId; }
    public void setCurrentCategoryId(String currentCategoryId) { this.currentCategoryId = currentCategoryId; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(0, page); }

    public SortMode getSortMode() { return sortMode; }
    public void toggleSort() {
        sortMode = (sortMode == SortMode.LOW_TO_HIGH) ? SortMode.HIGH_TO_LOW : SortMode.LOW_TO_HIGH;
    }

    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery == null ? "" : searchQuery; }

    public AwaitingInput getAwaitingInput() { return awaitingInput; }
    public void setAwaitingInput(AwaitingInput awaitingInput) { this.awaitingInput = awaitingInput; }

    public Material getPendingMaterial() { return pendingMaterial; }
    public void setPendingMaterial(Material pendingMaterial) { this.pendingMaterial = pendingMaterial; }
}
