package com.donutclone.economy.data;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Gere le prix de chaque item. Stocke/charge dans config.yml (section "prices").
 */
public class PriceManager {

    private final JavaPlugin plugin;
    private final Map<Material, Double> prices = new HashMap<>();

    // Materials a exclure de l'/ah (non obtenables en survie / techniques)
    private static final Set<Material> BLACKLIST = new HashSet<>(Arrays.asList(
            Material.BEDROCK, Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK,
            Material.REPEATING_COMMAND_BLOCK, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID,
            Material.BARRIER, Material.LIGHT, Material.JIGSAW, Material.DEBUG_STICK,
            Material.COMMAND_BLOCK_MINECART, Material.KNOWLEDGE_BOOK, Material.SPAWNER,
            Material.PETRIFIED_OAK_SLAB, Material.END_PORTAL_FRAME, Material.AIR,
            Material.CAVE_AIR, Material.VOID_AIR, Material.WATER, Material.LAVA
    ));

    public PriceManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        prices.clear();
        plugin.reloadConfig();
        var section = plugin.getConfig().getConfigurationSection("prices");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            try {
                Material mat = Material.valueOf(key.toUpperCase());
                double price = section.getDouble(key);
                prices.put(mat, price);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Material invalide dans config.yml: " + key);
            }
        }
    }

    public void save() {
        for (Map.Entry<Material, Double> entry : prices.entrySet()) {
            plugin.getConfig().set("prices." + entry.getKey().name(), entry.getValue());
        }
        plugin.saveConfig();
    }

    /** Prix de l'item, 0 si pas defini (= pas achetable/vendable) */
    public double getPrice(Material material) {
        return prices.getOrDefault(material, 0.0);
    }

    public boolean hasPrice(Material material) {
        return prices.containsKey(material);
    }

    public void setPrice(Material material, double price) {
        setPrice(material, price, true);
    }

    public void setPrice(Material material, double price, boolean save) {
        prices.put(material, price);
        if (save) save();
    }

    public void removePrice(Material material) {
        prices.remove(material);
        plugin.getConfig().set("prices." + material.name(), null);
        plugin.saveConfig();
    }

    /** Tous les items ayant un prix defini, exclus de la blacklist */
    public List<Material> getAllPricedMaterials() {
        List<Material> list = new ArrayList<>(prices.keySet());
        list.removeIf(BLACKLIST::contains);
        return list;
    }

    /** Tous les materials du jeu potentiellement affichables dans /ah (obtenables) */
    public List<Material> getAllShopableMaterials() {
        List<Material> list = new ArrayList<>();
        for (Material m : Material.values()) {
            if (!m.isItem()) continue;
            if (m.isLegacy()) continue;
            if (BLACKLIST.contains(m)) continue;
            if (m.name().endsWith("_SPAWN_EGG")) continue; // non tarifie: pas obtenable normalement
            list.add(m);
        }
        return list;
    }

    public boolean isBlacklisted(Material material) {
        return BLACKLIST.contains(material);
    }

    /**
     * S'assure qu'absolument TOUS les items/blocs du jeu ont un prix.
     * Au tout premier demarrage (ou une seule fois apres cette mise a jour),
     * on importe la liste de prix de base fournie. Ensuite, tout item encore
     * sans prix recoit un prix par defaut estime. Une fois importee, la liste
     * de base ne sera plus jamais reappliquee automatiquement: tes changements
     * via /config sont toujours preserves apres.
     */
    public void ensureAllPriced() {
        boolean changed = false;

        if (!plugin.getConfig().getBoolean("imported-default-prices", false)) {
            for (Map.Entry<Material, Double> entry : DefaultPriceList.build().entrySet()) {
                prices.put(entry.getKey(), entry.getValue());
            }
            plugin.getConfig().set("imported-default-prices", true);
            changed = true;
        }

        for (Material m : getAllShopableMaterials()) {
            if (!prices.containsKey(m)) {
                prices.put(m, computeDefaultPrice(m));
                changed = true;
            }
        }
        if (changed) save();
    }

    /**
     * Heuristique de prix par defaut basee sur le nom du material et sa
     * raretee habituelle. C'est un point de depart: tout est ensuite
     * modifiable a la main via /config.
     */
    private double computeDefaultPrice(Material m) {
        String name = m.name();

        // items uniques / tres rares
        if (name.equals("ELYTRA")) return 200_000_000;
        if (name.equals("DRAGON_EGG")) return 5_000_000_000.0;
        if (name.equals("NETHER_STAR")) return 750_000_000;
        if (name.equals("BEACON")) return 900_000_000;
        if (name.equals("TOTEM_OF_UNDYING")) return 50_000_000;
        if (name.equals("ENCHANTED_GOLDEN_APPLE")) return 5_000_000;
        if (name.contains("SHULKER_BOX")) return 8_000_000;
        if (name.startsWith("MUSIC_DISC")) return 1_500_000;
        if (name.contains("SPAWN_EGG")) return 1_500_000;
        if (name.startsWith("ENCHANTED_BOOK")) return 100_000;

        // potions et effets
        if (name.contains("LINGERING_POTION")) return 90_000;
        if (name.contains("SPLASH_POTION")) return 60_000;
        if (name.contains("POTION")) return 40_000;
        if (name.equals("EXPERIENCE_BOTTLE")) return 2_500;

        // outils / armes / armures selon le tier du materiau
        if (name.startsWith("NETHERITE_")) return 2_000_000;
        if (name.startsWith("DIAMOND_")) return 45_000;
        if (name.startsWith("GOLDEN_") || name.startsWith("GOLD_")) return 8_000;
        if (name.startsWith("IRON_")) return 2_000;
        if (name.startsWith("CHAINMAIL_")) return 3_000;
        if (name.startsWith("STONE_")) return 300;
        if (name.startsWith("WOODEN_") || name.startsWith("WOOD_")) return 100;
        if (name.startsWith("LEATHER_")) return 150;
        if (name.startsWith("TURTLE_")) return 5_000;

        // minerais / materiaux bruts
        if (name.contains("NETHERITE")) return 500_000;
        if (name.contains("DIAMOND")) return 5_000;
        if (name.contains("EMERALD")) return 4_500;
        if (name.contains("AMETHYST")) return 600;
        if (name.contains("QUARTZ")) return 250;
        if (name.contains("LAPIS")) return 200;
        if (name.contains("REDSTONE")) return 150;
        if (name.contains("COPPER")) return 300;
        if (name.contains("GOLD")) return 1_200;
        if (name.contains("IRON")) return 500;
        if (name.contains("COAL")) return 80;

        // nourriture
        if (m.isEdible()) return 100;

        // par defaut: on se base sur la taille de stack.
        // les items non stackables (1) sont generalement plus rares/precieux
        // que les blocs qui stackent a 64.
        int stack = m.getMaxStackSize();
        if (stack >= 64) return 40;
        if (stack >= 16) return 500;
        return 5_000;
    }
}
