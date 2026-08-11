package com.donutclone.economy;

import com.donutclone.economy.commands.*;
import com.donutclone.economy.data.CategoryManager;
import com.donutclone.economy.data.PriceManager;
import com.donutclone.economy.economy.EconomyManager;
import com.donutclone.economy.economy.ShopService;
import com.donutclone.economy.economy.StatsManager;
import com.donutclone.economy.gui.*;
import com.donutclone.economy.listeners.AnvilInputListener;
import com.donutclone.economy.listeners.ChatInputListener;
import com.donutclone.economy.listeners.InventoryListener;
import com.donutclone.economy.listeners.OrderListener;
import com.donutclone.economy.listeners.PlayerEventsListener;
import com.donutclone.economy.listeners.SellListener;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyShopPlugin extends JavaPlugin {

    private PriceManager priceManager;
    private CategoryManager categoryManager;
    private EconomyManager economyManager;
    private StatsManager statsManager;
    private ShopService shopService;

    private SessionManager sessionManager;
    private ShopGUI shopGUI;
    private AHGUI ahGUI;
    private ConfigGUI configGUI;
    private ItemGridGUI itemGridGUI;
    private ScoreboardManager scoreboardManager;
    private RtpManager rtpManager;
    private RtpGUI rtpGUI;
    private SellManager sellManager;
    private SellGUI sellGUI;
    private BuyConfirmGUI buyConfirmGUI;
    private OrderManager orderManager;
    private OrderBrowseGUI orderBrowseGUI;
    private OrderMaterialPickerGUI orderMaterialPickerGUI;
    private OrderRecapGUI orderRecapGUI;
    private OrderMyGUI orderMyGUI;
    private OrderFulfillGUI orderFulfillGUI;
    private OrderCancelConfirmGUI orderCancelConfirmGUI;
    private AnvilTextInputManager anvilInputManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.priceManager = new PriceManager(this);
        this.priceManager.ensureAllPriced();
        this.categoryManager = new CategoryManager(this);
        this.economyManager = new EconomyManager(this);
        this.statsManager = new StatsManager(this);
        this.shopService = new ShopService(this);

        this.sessionManager = new SessionManager();
        this.shopGUI = new ShopGUI(this);
        this.ahGUI = new AHGUI(this);
        this.configGUI = new ConfigGUI(this);
        this.itemGridGUI = new ItemGridGUI(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.rtpManager = new RtpManager(this);
        this.rtpGUI = new RtpGUI(this);
        this.sellManager = new SellManager();
        this.sellGUI = new SellGUI(this);
        this.buyConfirmGUI = new BuyConfirmGUI(this);
        this.orderManager = new OrderManager();
        this.orderBrowseGUI = new OrderBrowseGUI(this);
        this.orderMaterialPickerGUI = new OrderMaterialPickerGUI(this);
        this.orderRecapGUI = new OrderRecapGUI(this);
        this.orderMyGUI = new OrderMyGUI(this);
        this.orderFulfillGUI = new OrderFulfillGUI(this);
        this.orderCancelConfirmGUI = new OrderCancelConfirmGUI(this);
        this.anvilInputManager = new AnvilTextInputManager();

        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatInputListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEventsListener(this), this);
        getServer().getPluginManager().registerEvents(new SellListener(this), this);
        getServer().getPluginManager().registerEvents(new OrderListener(this), this);
        getServer().getPluginManager().registerEvents(new AnvilInputListener(this), this);

        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("ah").setExecutor(new AHCommand(this));
        getCommand("config").setExecutor(new ConfigCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("add").setExecutor(new AddCommand(this));
        getCommand("rtp").setExecutor(new RtpCommand(this));
        getCommand("sell").setExecutor(new SellCommand(this));
        getCommand("order").setExecutor(new OrderCommand(this));

        // met en place le scoreboard pour les joueurs deja connectes (ex: apres un /reload)
        for (var player : getServer().getOnlinePlayers()) {
            scoreboardManager.setup(player);
        }

        getLogger().info("EconomyShop active !");
    }

    @Override
    public void onDisable() {
        if (economyManager != null) economyManager.save();
        if (statsManager != null) statsManager.save();
    }

    public PriceManager getPriceManager() { return priceManager; }
    public CategoryManager getCategoryManager() { return categoryManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public StatsManager getStatsManager() { return statsManager; }
    public ShopService getShopService() { return shopService; }
    public SessionManager getSessionManager() { return sessionManager; }
    public ShopGUI getShopGUI() { return shopGUI; }
    public AHGUI getAhGUI() { return ahGUI; }
    public ConfigGUI getConfigGUI() { return configGUI; }
    public ItemGridGUI getItemGridGUI() { return itemGridGUI; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public RtpManager getRtpManager() { return rtpManager; }
    public RtpGUI getRtpGUI() { return rtpGUI; }
    public SellManager getSellManager() { return sellManager; }
    public SellGUI getSellGUI() { return sellGUI; }
    public BuyConfirmGUI getBuyConfirmGUI() { return buyConfirmGUI; }
    public OrderManager getOrderManager() { return orderManager; }
    public OrderBrowseGUI getOrderBrowseGUI() { return orderBrowseGUI; }
    public OrderMaterialPickerGUI getOrderMaterialPickerGUI() { return orderMaterialPickerGUI; }
    public OrderRecapGUI getOrderRecapGUI() { return orderRecapGUI; }
    public OrderMyGUI getOrderMyGUI() { return orderMyGUI; }
    public OrderFulfillGUI getOrderFulfillGUI() { return orderFulfillGUI; }
    public OrderCancelConfirmGUI getOrderCancelConfirmGUI() { return orderCancelConfirmGUI; }
    public AnvilTextInputManager getAnvilInputManager() { return anvilInputManager; }
}
