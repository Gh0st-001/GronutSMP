package com.donutclone.economy.listeners;

import com.donutclone.economy.EconomyShopPlugin;
import com.donutclone.economy.data.Order;
import com.donutclone.economy.gui.GuiSession;
import com.donutclone.economy.gui.Menus;
import com.donutclone.economy.gui.OrderManager;
import com.donutclone.economy.util.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OrderListener implements Listener {

    private final EconomyShopPlugin plugin;

    public OrderListener(EconomyShopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        if (title.equals(Menus.ORDER_FULFILL_DEPOSIT_TITLE)) {
            handleDepositClick(event);
            return;
        }
        if (!isFixedOrderMenu(title)) return;

        event.setCancelled(true);
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(event.getView().getTopInventory())) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (title.equals(Menus.ORDER_BROWSE_TITLE)) handleBrowseClick(player, slot);
        else if (title.equals(Menus.ORDER_MATERIAL_PICKER_TITLE)) handlePickerClick(player, slot);
        else if (title.equals(Menus.ORDER_RECAP_TITLE)) handleRecapClick(player, slot);
        else if (title.equals(Menus.ORDER_MY_TITLE)) handleMyClick(player, slot);
        else if (title.equals(Menus.ORDER_CANCEL_CONFIRM_TITLE)) handleCancelConfirmClick(player, slot);
        else if (title.equals(Menus.ORDER_FULFILL_CONFIRM_TITLE)) handleFulfillConfirmClick(player, slot);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(Menus.ORDER_FULFILL_DEPOSIT_TITLE)) {
            if (event.getRawSlots().contains(53)) {
                event.setCancelled(true);
                return;
            }
            Player player = (Player) event.getWhoClicked();
            Bukkit.getScheduler().runTask(plugin, () -> plugin.getOrderFulfillGUI().refreshDepositButton(player));
        } else if (isFixedOrderMenu(title)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        String title = event.getView().getTitle();
        OrderManager om = plugin.getOrderManager();

        if (title.equals(Menus.ORDER_FULFILL_DEPOSIT_TITLE)) {
            if (om.getFulfillState(player.getUniqueId()) == OrderManager.FulfillState.DEPOSIT_OPEN) {
                plugin.getOrderFulfillGUI().giveBackDeposit(player, event.getInventory());
                om.resetFulfilling(player.getUniqueId());
            }
        } else if (title.equals(Menus.ORDER_FULFILL_CONFIRM_TITLE)) {
            if (om.getFulfillState(player.getUniqueId()) == OrderManager.FulfillState.CONFIRM_OPEN) {
                plugin.getOrderFulfillGUI().giveBackPending(player);
                om.resetFulfilling(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        OrderManager om = plugin.getOrderManager();
        OrderManager.FulfillState state = om.getFulfillState(player.getUniqueId());

        if (state == OrderManager.FulfillState.DEPOSIT_OPEN) {
            var inv = om.getFulfillDeposit(player.getUniqueId());
            if (inv != null) plugin.getOrderFulfillGUI().giveBackDeposit(player, inv);
        } else if (state == OrderManager.FulfillState.CONFIRM_OPEN) {
            plugin.getOrderFulfillGUI().giveBackPending(player);
        }
        om.resetFulfilling(player.getUniqueId());
    }

    private boolean isFixedOrderMenu(String title) {
        return title.equals(Menus.ORDER_BROWSE_TITLE)
                || title.equals(Menus.ORDER_MATERIAL_PICKER_TITLE)
                || title.equals(Menus.ORDER_RECAP_TITLE)
                || title.equals(Menus.ORDER_MY_TITLE)
                || title.equals(Menus.ORDER_CANCEL_CONFIRM_TITLE)
                || title.equals(Menus.ORDER_FULFILL_CONFIRM_TITLE);
    }

    // ---------------- parcours des commandes ----------------
    private void handleBrowseClick(Player player, int slot) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());
        OrderManager om = plugin.getOrderManager();

        if (slot == 45) {
            session.setPage(session.getPage() - 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            plugin.getOrderBrowseGUI().render(player);
            return;
        }
        if (slot == 48) {
            session.toggleSort();
            plugin.getOrderBrowseGUI().render(player);
            return;
        }
        if (slot == 49) {
            session.setAwaitingInput(GuiSession.AwaitingInput.ORDER_BROWSE_SEARCH);
            player.closeInventory();
            msg(player, "&bTape dans le chat le nom du bloc que tu cherches :");
            return;
        }
        if (slot == 50) {
            Order existing = om.getOrderByCreator(player.getUniqueId());
            if (existing != null) {
                plugin.getOrderMyGUI().open(player, existing);
            } else {
                OrderManager.Draft draft = om.getDraft(player.getUniqueId());
                draft.material = null;
                draft.quantity = 0;
                draft.reward = 0;
                plugin.getOrderMaterialPickerGUI().open(player);
            }
            return;
        }
        if (slot == 53) {
            session.setPage(session.getPage() + 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            plugin.getOrderBrowseGUI().render(player);
            return;
        }
        if (slot >= 45) return;

        var pageOrders = session.getCurrentPageOrders();
        if (slot < 0 || slot >= pageOrders.size()) return;
        UUID orderId = pageOrders.get(slot);
        Order order = om.getOrder(orderId);
        if (order == null || !order.isBrowsable()) {
            msg(player, "&cCette commande n'est plus disponible.");
            plugin.getOrderBrowseGUI().render(player);
            return;
        }
        if (order.getCreator().equals(player.getUniqueId())) {
            msg(player, "&cC'est ta propre commande. Gere-la via le coffre en bas du menu.");
            return;
        }

        plugin.getOrderFulfillGUI().openDeposit(player, order);
    }

    // ---------------- selection du bloc (grille + recherche + tri) ----------------
    private void handlePickerClick(Player player, int slot) {
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());

        if (slot == 0) {
            session.setAwaitingInput(GuiSession.AwaitingInput.ORDER_PICKER_SEARCH);
            player.closeInventory();
            msg(player, "&bTape dans le chat le nom du bloc que tu cherches :");
            return;
        }
        if (slot == 1) {
            session.toggleSort();
            plugin.getOrderMaterialPickerGUI().render(player);
            return;
        }
        if (slot == 45) {
            session.setPage(session.getPage() - 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            plugin.getOrderMaterialPickerGUI().render(player);
            return;
        }
        if (slot == 53) {
            session.setPage(session.getPage() + 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            plugin.getOrderMaterialPickerGUI().render(player);
            return;
        }
        if (slot < 9 || slot >= 45) return;

        var pageItems = session.getCurrentPageItems();
        int index = slot - 9;
        if (index < 0 || index >= pageItems.size()) return;

        Material material = pageItems.get(index);
        OrderManager.Draft draft = plugin.getOrderManager().getDraft(player.getUniqueId());
        draft.material = material;

        openPriceAnvil(player);
    }

    // ---------------- saisie du prix / de la quantite via enclume ----------------
    private void openPriceAnvil(Player player) {
        plugin.getAnvilInputManager().register(player.getUniqueId(), (p, text) -> {
            double value = NumberFormatter.parse(text);
            if (value <= 0) {
                msg(p, "&cPrix invalide. Reessaie.");
                openPriceAnvil(p);
                return;
            }
            plugin.getOrderManager().getDraft(p.getUniqueId()).reward = value;
            openQuantityAnvil(p);
        });
        plugin.getAnvilInputManager().open(player, "\u00A78Prix a l'unite ($)", "50");
    }

    private void openQuantityAnvil(Player player) {
        plugin.getAnvilInputManager().register(player.getUniqueId(), (p, text) -> {
            int value;
            try {
                value = Integer.parseInt(text.trim().replace(" ", ""));
            } catch (NumberFormatException e) {
                msg(p, "&cQuantite invalide. Reessaie.");
                openQuantityAnvil(p);
                return;
            }
            if (value <= 0) {
                msg(p, "&cLa quantite doit etre superieure a 0. Reessaie.");
                openQuantityAnvil(p);
                return;
            }
            plugin.getOrderManager().getDraft(p.getUniqueId()).quantity = value;
            plugin.getOrderRecapGUI().open(p);
        });
        plugin.getAnvilInputManager().open(player, "\u00A78Quantite voulue", "1000");
    }

    // ---------------- recapitulatif ----------------
    private void handleRecapClick(Player player, int slot) {
        if (slot == 10) {
            plugin.getOrderMaterialPickerGUI().open(player);
        } else if (slot == 13) {
            openQuantityAnvil(player);
        } else if (slot == 16) {
            openPriceAnvil(player);
        } else if (slot == 20) {
            plugin.getOrderManager().clearDraft(player.getUniqueId());
            player.closeInventory();
        } else if (slot == 24) {
            confirmCreateOrder(player);
        }
    }

    private void confirmCreateOrder(Player player) {
        OrderManager om = plugin.getOrderManager();
        OrderManager.Draft draft = om.getDraft(player.getUniqueId());

        if (om.getOrderByCreator(player.getUniqueId()) != null) {
            msg(player, "&cTu as deja une commande en cours.");
            return;
        }
        if (draft.material == null) { msg(player, "&cChoisis un bloc d'abord."); return; }
        if (draft.quantity <= 0) { msg(player, "&cLa quantite doit etre superieure a 0."); return; }
        if (draft.reward <= 0) { msg(player, "&cLa recompense doit etre superieure a 0."); return; }

        double total = draft.quantity * draft.reward;
        if (!plugin.getEconomyManager().withdraw(player.getUniqueId(), total)) {
            msg(player, "&cTu n'as pas assez d'argent. Il te faut " + NumberFormatter.formatMoney(total) + ".");
            return;
        }

        Order order = new Order(player.getUniqueId(), player.getName(), draft.material, draft.quantity, draft.reward);
        om.addOrder(order);
        om.clearDraft(player.getUniqueId());
        plugin.getScoreboardManager().update(player);

        player.closeInventory();
        msg(player, "&aCommande creee: &f" + draft.quantity + "x " + prettyName(draft.material)
                + " &apour " + NumberFormatter.formatMoney(total) + ".");
    }

    // ---------------- ma commande ----------------
    private void handleMyClick(Player player, int slot) {
        OrderManager om = plugin.getOrderManager();
        Order order = om.getOrderByCreator(player.getUniqueId());
        if (order == null) { player.closeInventory(); return; }
        GuiSession session = plugin.getSessionManager().get(player.getUniqueId());

        if (slot == 45) {
            if (order.getStatus() != Order.Status.ACTIVE) {
                msg(player, "&cCette commande est deja terminee, il ne reste qu'a recuperer les blocs.");
                return;
            }
            plugin.getOrderCancelConfirmGUI().open(player, order);
            return;
        }
        if (slot == 48) {
            session.setPage(session.getPage() - 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            plugin.getOrderMyGUI().render(player, order);
            return;
        }
        if (slot == 50) {
            session.setPage(session.getPage() + 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            plugin.getOrderMyGUI().render(player, order);
            return;
        }
        if (slot == 49 || slot >= 45) return;

        // clic sur un stack virtuel -> on le recupere (toujours depuis la 1ere case en tenant compte de la page)
        int page = session.getPage();
        int already = page * 45 * 64 + slot * 64;
        int amount = Math.min(64, order.getCollectedQuantity() - already);
        if (amount <= 0) return;

        order.withdraw(amount);
        giveItems(player, order.getMaterial(), amount);
        om.removeIfResolved(order);

        if (om.getOrder(order.getId()) == null) {
            player.closeInventory();
            msg(player, "&aCommande entierement recuperee !");
        } else {
            plugin.getOrderMyGUI().render(player, order);
        }
    }

    // ---------------- confirmation d'annulation ----------------
    private void handleCancelConfirmClick(Player player, int slot) {
        OrderManager om = plugin.getOrderManager();
        Order order = om.getOrderByCreator(player.getUniqueId());
        if (order == null) { player.closeInventory(); return; }

        if (slot == 11) {
            double refund = order.getRemainingPot();
            order.setStatus(Order.Status.CANCELLED);
            plugin.getEconomyManager().deposit(player.getUniqueId(), refund);
            plugin.getScoreboardManager().update(player);
            msg(player, "&cCommande annulee. &a" + NumberFormatter.formatMoney(refund) + " &ete rembourse.");

            om.removeIfResolved(order);
            if (om.getOrder(order.getId()) == null) {
                player.closeInventory();
            } else {
                plugin.getOrderMyGUI().open(player, order);
            }
        } else if (slot == 15) {
            plugin.getOrderMyGUI().open(player, order);
        }
    }

    // ---------------- confirmation de fourniture ----------------
    private void handleFulfillConfirmClick(Player player, int slot) {
        if (slot == 15) {
            plugin.getOrderFulfillGUI().confirmFulfill(player);
        } else if (slot == 11) {
            plugin.getOrderFulfillGUI().cancelFulfill(player);
        }
    }

    // ---------------- depot de blocs pour une commande ----------------
    private void handleDepositClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != null
                && event.getClickedInventory().equals(event.getView().getTopInventory())
                && event.getSlot() == 53) {
            event.setCancelled(true);
            plugin.getOrderFulfillGUI().onDepositConfirmClicked(player);
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> plugin.getOrderFulfillGUI().refreshDepositButton(player));
    }

    private void giveItems(Player player, Material material, int amount) {
        int remaining = amount;
        int maxStack = material.getMaxStackSize();
        while (remaining > 0) {
            int chunk = Math.min(remaining, maxStack);
            ItemStack stack = new ItemStack(material, chunk);
            player.getInventory().addItem(stack).values()
                    .forEach(left -> player.getWorld().dropItem(player.getLocation(), left));
            remaining -= chunk;
        }
    }

    private String prettyName(Material mat) {
        String[] parts = mat.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        return sb.toString().trim();
    }

    private void msg(Player player, String text) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }
}
