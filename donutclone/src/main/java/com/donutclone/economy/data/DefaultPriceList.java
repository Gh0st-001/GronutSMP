package com.donutclone.economy.data;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * Liste de prix de base fournie par l'utilisateur (economie basee sur le
 * cout des crafts, la raretee et la difficulte d'obtention). Appliquee une
 * seule fois au demarrage (voir PriceManager.ensureAllPriced), puis tout
 * reste modifiable via /config.
 */
public class DefaultPriceList {

    private static final String[] WOOD = {
            "OAK", "SPRUCE", "BIRCH", "JUNGLE", "ACACIA", "DARK_OAK", "MANGROVE", "CHERRY"
    };
    private static final String[] NETHER_WOOD = { "CRIMSON", "WARPED" };
    private static final String[] ALL_WOOD;
    static {
        ALL_WOOD = new String[WOOD.length + NETHER_WOOD.length];
        System.arraycopy(WOOD, 0, ALL_WOOD, 0, WOOD.length);
        System.arraycopy(NETHER_WOOD, 0, ALL_WOOD, WOOD.length, NETHER_WOOD.length);
    }

    private static final String[] DYE_COLORS = {
            "WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME", "PINK", "GRAY",
            "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK"
    };

    private static final String[] TOOL_TIERS = { "WOODEN", "STONE", "IRON", "GOLDEN", "DIAMOND", "NETHERITE" };

    public static Map<Material, Double> build() {
        Map<Material, Double> p = new HashMap<>();

        // ---------------- BLOCS NATURELS / CONSTRUCTION ----------------
        put(p, "DIRT", 1); put(p, "COARSE_DIRT", 2); put(p, "GRASS_BLOCK", 3);
        put(p, "PODZOL", 3); put(p, "MYCELIUM", 4); put(p, "SAND", 2); put(p, "RED_SAND", 3);
        put(p, "GRAVEL", 2); put(p, "CLAY", 3); put(p, "MUD", 2); put(p, "PACKED_MUD", 4);
        put(p, "SNOW", 1); put(p, "SNOW_BLOCK", 4); put(p, "ICE", 4); put(p, "PACKED_ICE", 8);
        put(p, "BLUE_ICE", 20);

        put(p, "STONE", 2); put(p, "SMOOTH_STONE", 2);
        put(p, "ANDESITE", 2); put(p, "POLISHED_ANDESITE", 3);
        put(p, "DIORITE", 2); put(p, "POLISHED_DIORITE", 3);
        put(p, "GRANITE", 2); put(p, "POLISHED_GRANITE", 3);
        put(p, "TUFF", 3); put(p, "POLISHED_TUFF", 4);
        put(p, "CALCITE", 5); put(p, "POINTED_DRIPSTONE", 4); put(p, "DRIPSTONE_BLOCK", 12);
        put(p, "DEEPSLATE", 3); put(p, "POLISHED_DEEPSLATE", 4);
        put(p, "CHISELED_DEEPSLATE", 5); put(p, "REINFORCED_DEEPSLATE", 25);
        put(p, "OBSIDIAN", 30); put(p, "CRYING_OBSIDIAN", 40);

        put(p, "BASALT", 3); put(p, "POLISHED_BASALT", 4); put(p, "SMOOTH_BASALT", 5);

        put(p, "NETHERRACK", 2); put(p, "SOUL_SAND", 5); put(p, "SOUL_SOIL", 5);
        put(p, "BLACKSTONE", 3); put(p, "POLISHED_BLACKSTONE", 4);
        put(p, "CHISELED_POLISHED_BLACKSTONE", 5); put(p, "NETHER_BRICKS", 6);
        put(p, "QUARTZ_BLOCK", 12); put(p, "CHISELED_QUARTZ_BLOCK", 14);
        put(p, "SMOOTH_QUARTZ", 14); put(p, "QUARTZ", 5);
        put(p, "AMETHYST_BLOCK", 15); put(p, "AMETHYST_SHARD", 5);

        put(p, "END_STONE", 8); put(p, "END_STONE_BRICKS", 12);
        put(p, "PURPUR_PILLAR", 15); put(p, "PURPUR_BLOCK", 18);

        putSuffixed(p, 5, WOOD, "_LOG"); putSuffixed(p, 5, WOOD, "_WOOD");
        putSuffixed(p, 6, prefixed("STRIPPED_", WOOD), "_LOG");
        putSuffixed(p, 2, WOOD, "_PLANKS");
        put(p, "STICK", 1);
        putSuffixed(p, 3, ALL_WOOD, "_STAIRS");
        putSuffixed(p, 1, ALL_WOOD, "_SLAB");
        putSuffixed(p, 4, ALL_WOOD, "_FENCE");
        putSuffixed(p, 5, ALL_WOOD, "_DOOR");
        putSuffixed(p, 5, ALL_WOOD, "_TRAPDOOR");
        putSuffixed(p, 3, ALL_WOOD, "_SIGN");
        putSuffixed(p, 8, ALL_WOOD, "_BOAT");
        putSuffixed(p, 18, ALL_WOOD, "_CHEST_BOAT");

        put(p, "CRIMSON_PLANKS", 3); put(p, "WARPED_PLANKS", 3);
        put(p, "CRIMSON_STEM", 6); put(p, "WARPED_STEM", 6);

        put(p, "BRICK", 8); put(p, "BRICKS", 8);
        put(p, "STONE_BRICKS", 4); put(p, "MOSSY_STONE_BRICKS", 5);
        put(p, "CHISELED_STONE_BRICKS", 5); put(p, "CRACKED_STONE_BRICKS", 5);

        put(p, "GLASS", 4); put(p, "GLASS_PANE", 1); put(p, "TINTED_GLASS", 6);
        putSuffixed(p, 5, DYE_COLORS, "_STAINED_GLASS");
        putSuffixed(p, 2, DYE_COLORS, "_STAINED_GLASS_PANE");
        putSuffixed(p, 5, DYE_COLORS, "_TERRACOTTA"); put(p, "TERRACOTTA", 5);
        putSuffixed(p, 12, DYE_COLORS, "_GLAZED_TERRACOTTA");
        putSuffixed(p, 5, DYE_COLORS, "_CONCRETE");
        putSuffixed(p, 4, DYE_COLORS, "_CONCRETE_POWDER");

        put(p, "LANTERN", 12); put(p, "SOUL_LANTERN", 15);
        put(p, "TORCH", 2); put(p, "SOUL_TORCH", 4); put(p, "REDSTONE_TORCH", 5);
        put(p, "CAMPFIRE", 12); put(p, "SOUL_CAMPFIRE", 16);

        // ---------------- MINERAIS / MATERIAUX ----------------
        put(p, "COAL", 4); put(p, "COAL_BLOCK", 36);
        put(p, "RAW_IRON", 8); put(p, "RAW_IRON_BLOCK", 72);
        put(p, "IRON_INGOT", 10); put(p, "IRON_BLOCK", 90); put(p, "IRON_NUGGET", 2);
        put(p, "RAW_COPPER", 3); put(p, "COPPER_INGOT", 4); put(p, "COPPER_BLOCK", 36);
        put(p, "RAW_GOLD", 18); put(p, "GOLD_INGOT", 22); put(p, "GOLD_BLOCK", 198); put(p, "GOLD_NUGGET", 3);
        put(p, "DIAMOND", 180); put(p, "DIAMOND_BLOCK", 1620);
        put(p, "EMERALD", 220); put(p, "EMERALD_BLOCK", 1980);
        put(p, "LAPIS_LAZULI", 8); put(p, "LAPIS_BLOCK", 72);
        put(p, "REDSTONE", 5); put(p, "REDSTONE_BLOCK", 45);
        put(p, "ANCIENT_DEBRIS", 500); put(p, "NETHERITE_SCRAP", 550);
        put(p, "NETHERITE_INGOT", 2200); put(p, "NETHERITE_BLOCK", 19800);

        put(p, "COAL_ORE", 18); put(p, "IRON_ORE", 30); put(p, "COPPER_ORE", 12);
        put(p, "GOLD_ORE", 55); put(p, "REDSTONE_ORE", 15); put(p, "LAPIS_ORE", 20);
        put(p, "DIAMOND_ORE", 600); put(p, "EMERALD_ORE", 700);
        put(p, "NETHER_QUARTZ_ORE", 12); put(p, "NETHER_GOLD_ORE", 35);
        put(p, "DEEPSLATE_COAL_ORE", 25); put(p, "DEEPSLATE_IRON_ORE", 40);
        put(p, "DEEPSLATE_COPPER_ORE", 20); put(p, "DEEPSLATE_GOLD_ORE", 70);
        put(p, "DEEPSLATE_REDSTONE_ORE", 25); put(p, "DEEPSLATE_LAPIS_ORE", 30);
        put(p, "DEEPSLATE_DIAMOND_ORE", 750); put(p, "DEEPSLATE_EMERALD_ORE", 900);

        // ---------------- AGRICULTURE / NOURRITURE ----------------
        put(p, "WHEAT", 3); put(p, "WHEAT_SEEDS", 1); put(p, "HAY_BLOCK", 8);
        put(p, "CARROT", 3); put(p, "POTATO", 3); put(p, "BAKED_POTATO", 5);
        put(p, "BEETROOT", 3); put(p, "BEETROOT_SEEDS", 1);
        put(p, "SUGAR_CANE", 4); put(p, "BAMBOO", 2); put(p, "COCOA_BEANS", 5);
        put(p, "STRING", 5); put(p, "RED_MUSHROOM", 4); put(p, "BROWN_MUSHROOM", 4);
        put(p, "SWEET_BERRIES", 4); put(p, "GLOW_BERRIES", 6);
        put(p, "MELON", 10); put(p, "MELON_SLICE", 1);
        put(p, "PUMPKIN", 8); put(p, "PUMPKIN_SEEDS", 2); put(p, "MELON_SEEDS", 2);

        putAll(p, 2, "DANDELION", "POPPY", "BLUE_ORCHID", "ALLIUM", "AZURE_BLUET",
                "RED_TULIP", "ORANGE_TULIP", "WHITE_TULIP", "PINK_TULIP", "OXEYE_DAISY",
                "CORNFLOWER", "LILY_OF_THE_VALLEY", "TORCHFLOWER", "WITHER_ROSE");
        putAll(p, 4, "SUNFLOWER", "LILAC", "ROSE_BUSH", "PEONY");

        put(p, "KELP", 2); put(p, "DRIED_KELP", 4); put(p, "DRIED_KELP_BLOCK", 30);
        put(p, "CACTUS", 4); put(p, "VINE", 3); put(p, "GLOW_LICHEN", 3);
        put(p, "CHORUS_FLOWER", 8); put(p, "CHORUS_FRUIT", 12);

        put(p, "BREAD", 10); put(p, "COOKIE", 8); put(p, "CAKE", 35);
        put(p, "PUMPKIN_PIE", 20); put(p, "MUSHROOM_STEW", 20); put(p, "RABBIT_STEW", 35);
        put(p, "SUSPICIOUS_STEW", 25); put(p, "BEETROOT_SOUP", 20);
        put(p, "APPLE", 8); put(p, "GOLDEN_APPLE", 120);
        put(p, "GOLDEN_CARROT", 100); put(p, "GLISTERING_MELON_SLICE", 70);
        put(p, "COOKED_BEEF", 12); put(p, "COOKED_PORKCHOP", 12); put(p, "COOKED_CHICKEN", 10);
        put(p, "COOKED_MUTTON", 10); put(p, "COOKED_RABBIT", 15);
        put(p, "COOKED_COD", 10); put(p, "COOKED_SALMON", 12);
        put(p, "COD", 5); put(p, "SALMON", 6); put(p, "TROPICAL_FISH", 15); put(p, "PUFFERFISH", 20);
        put(p, "EGG", 2); put(p, "MILK_BUCKET", 12);
        put(p, "HONEY_BLOCK", 15); put(p, "HONEY_BOTTLE", 20);

        // ---------------- MOB DROPS / COMBAT ----------------
        put(p, "BONE", 5); put(p, "BONE_MEAL", 2); put(p, "ARROW", 4);
        put(p, "SPIDER_EYE", 8); put(p, "FERMENTED_SPIDER_EYE", 15);
        put(p, "ROTTEN_FLESH", 2); put(p, "FEATHER", 4); put(p, "LEATHER", 8);
        put(p, "INK_SAC", 8); put(p, "GLOW_INK_SAC", 15); put(p, "GUNPOWDER", 15);
        put(p, "ENDER_PEARL", 60); put(p, "BLAZE_ROD", 100); put(p, "MAGMA_CREAM", 80);
        put(p, "GHAST_TEAR", 120); put(p, "WITHER_SKELETON_SKULL", 350);
        put(p, "ZOMBIE_HEAD", 250); put(p, "SKELETON_SKULL", 250); put(p, "CREEPER_HEAD", 300);
        put(p, "SHULKER_SHELL", 250); put(p, "NETHER_STAR", 2500); put(p, "SADDLE", 100);
        put(p, "PHANTOM_MEMBRANE", 80); put(p, "NAUTILUS_SHELL", 100);
        put(p, "HEART_OF_THE_SEA", 800); put(p, "PRISMARINE_SHARD", 8); put(p, "PRISMARINE_CRYSTALS", 12);

        // ---------------- OUTILS / ARMES / ARMURES ----------------
        double[] pickAxePrices = {5, 12, 35, 45, 600, 2600};
        double[] shovelHoePrices = {4, 10, 30, 40, 450, 2200};
        for (int i = 0; i < TOOL_TIERS.length; i++) {
            put(p, TOOL_TIERS[i] + "_PICKAXE", pickAxePrices[i]);
            put(p, TOOL_TIERS[i] + "_AXE", pickAxePrices[i]);
            put(p, TOOL_TIERS[i] + "_SWORD", pickAxePrices[i]);
            put(p, TOOL_TIERS[i] + "_SHOVEL", shovelHoePrices[i]);
            put(p, TOOL_TIERS[i] + "_HOE", shovelHoePrices[i]);
        }

        put(p, "BOW", 50); put(p, "CROSSBOW", 100); put(p, "SHIELD", 60);
        put(p, "FISHING_ROD", 35);
        put(p, "CARROT_ON_A_STICK", 50); put(p, "WARPED_FUNGUS_ON_A_STICK", 50);
        put(p, "FLINT", 5); put(p, "FLINT_AND_STEEL", 20);
        put(p, "BUCKET", 35); put(p, "WATER_BUCKET", 45); put(p, "LAVA_BUCKET", 100);
        put(p, "SHEARS", 25); put(p, "COMPASS", 80); put(p, "CLOCK", 120);
        put(p, "NAME_TAG", 100); put(p, "SPYGLASS", 60); put(p, "ELYTRA", 5000);

        put(p, "LEATHER_HELMET", 40); put(p, "LEATHER_CHESTPLATE", 65);
        put(p, "LEATHER_LEGGINGS", 55); put(p, "LEATHER_BOOTS", 35);
        put(p, "IRON_HELMET", 80); put(p, "IRON_CHESTPLATE", 140);
        put(p, "IRON_LEGGINGS", 120); put(p, "IRON_BOOTS", 70);
        put(p, "GOLDEN_HELMET", 160); put(p, "GOLDEN_CHESTPLATE", 280);
        put(p, "GOLDEN_LEGGINGS", 240); put(p, "GOLDEN_BOOTS", 140);
        put(p, "DIAMOND_HELMET", 1200); put(p, "DIAMOND_CHESTPLATE", 2100);
        put(p, "DIAMOND_LEGGINGS", 1800); put(p, "DIAMOND_BOOTS", 1050);
        put(p, "NETHERITE_HELMET", 4800); put(p, "NETHERITE_CHESTPLATE", 8400);
        put(p, "NETHERITE_LEGGINGS", 7200); put(p, "NETHERITE_BOOTS", 4200);

        // ---------------- REDSTONE / MECANIQUE ----------------
        put(p, "REPEATER", 30); put(p, "COMPARATOR", 40);
        put(p, "PISTON", 35); put(p, "STICKY_PISTON", 55); put(p, "OBSERVER", 50);
        put(p, "DISPENSER", 35); put(p, "DROPPER", 30); put(p, "HOPPER", 50);
        put(p, "RAIL", 8); put(p, "POWERED_RAIL", 30); put(p, "DETECTOR_RAIL", 15); put(p, "ACTIVATOR_RAIL", 15);
        put(p, "MINECART", 50); put(p, "CHEST_MINECART", 80);
        put(p, "HOPPER_MINECART", 100); put(p, "TNT_MINECART", 120);
        put(p, "REDSTONE_LAMP", 40); put(p, "NOTE_BLOCK", 45); put(p, "JUKEBOX", 150);
        put(p, "IRON_DOOR", 40); put(p, "IRON_TRAPDOOR", 35); put(p, "LEVER", 3);
        putAll(p, 2, "OAK_BUTTON", "SPRUCE_BUTTON", "BIRCH_BUTTON", "JUNGLE_BUTTON",
                "ACACIA_BUTTON", "DARK_OAK_BUTTON", "MANGROVE_BUTTON", "CHERRY_BUTTON",
                "CRIMSON_BUTTON", "WARPED_BUTTON", "STONE_BUTTON", "POLISHED_BLACKSTONE_BUTTON");
        putAll(p, 5, "OAK_PRESSURE_PLATE", "SPRUCE_PRESSURE_PLATE", "BIRCH_PRESSURE_PLATE",
                "JUNGLE_PRESSURE_PLATE", "ACACIA_PRESSURE_PLATE", "DARK_OAK_PRESSURE_PLATE",
                "MANGROVE_PRESSURE_PLATE", "CHERRY_PRESSURE_PLATE", "CRIMSON_PRESSURE_PLATE",
                "WARPED_PRESSURE_PLATE", "STONE_PRESSURE_PLATE", "POLISHED_BLACKSTONE_PRESSURE_PLATE",
                "LIGHT_WEIGHTED_PRESSURE_PLATE", "HEAVY_WEIGHTED_PRESSURE_PLATE");
        put(p, "TARGET", 30); put(p, "TNT", 100); put(p, "FIREWORK_ROCKET", 8);

        // ---------------- ALCHIMIE / ENCHANTEMENT ----------------
        put(p, "ENCHANTING_TABLE", 1000); put(p, "ANVIL", 350); put(p, "GRINDSTONE", 60);
        put(p, "SMITHING_TABLE", 50); put(p, "STONECUTTER", 40); put(p, "BOOKSHELF", 35);
        put(p, "BOOK", 10); put(p, "ENCHANTED_BOOK", 150);
        put(p, "EXPERIENCE_BOTTLE", 100); put(p, "BREWING_STAND", 150);
        put(p, "BLAZE_POWDER", 50); put(p, "SUGAR", 3); put(p, "GLOWSTONE", 12);

        put(p, "POTION", 100); put(p, "SPLASH_POTION", 150); put(p, "LINGERING_POTION", 200);

        // ---------------- TRANSPORT / STOCKAGE ----------------
        put(p, "CHEST", 25); put(p, "TRAPPED_CHEST", 50); put(p, "BARREL", 20);
        put(p, "SHULKER_BOX", 600); putSuffixed(p, 600, DYE_COLORS, "_SHULKER_BOX");
        put(p, "ENDER_CHEST", 1000);
        put(p, "MAP", 30); put(p, "FILLED_MAP", 300); put(p, "LODESTONE", 800);
        put(p, "ITEM_FRAME", 30); put(p, "GLOW_ITEM_FRAME", 100);

        // ---------------- DECORATION / UTILITAIRE ----------------
        put(p, "FLOWER_POT", 8); put(p, "DECORATED_POT", 15); put(p, "CHAIN", 10);
        put(p, "BELL", 150); put(p, "WRITABLE_BOOK", 20); put(p, "PAINTING", 10);
        putSuffixed(p, 25, DYE_COLORS, "_BANNER");
        put(p, "CANDLE", 8); putSuffixed(p, 10, DYE_COLORS, "_CANDLE");
        put(p, "WHITE_BED", 20); putSuffixedExcept(p, 22, DYE_COLORS, "_BED", "WHITE");
        put(p, "SCAFFOLDING", 5); put(p, "LADDER", 4);

        // ---------------- COULEURS / LAINE ----------------
        put(p, "WHITE_WOOL", 5); putSuffixedExcept(p, 7, DYE_COLORS, "_WOOL", "WHITE");
        put(p, "WHITE_CARPET", 2); putSuffixedExcept(p, 3, DYE_COLORS, "_CARPET", "WHITE");
        put(p, "WHITE_DYE", 2); put(p, "BLACK_DYE", 5); put(p, "RED_DYE", 2); put(p, "YELLOW_DYE", 2);
        put(p, "GREEN_DYE", 3); put(p, "BLUE_DYE", 3); put(p, "PURPLE_DYE", 4); put(p, "PINK_DYE", 3);
        put(p, "ORANGE_DYE", 3); put(p, "CYAN_DYE", 4); put(p, "MAGENTA_DYE", 4); put(p, "BROWN_DYE", 4);
        put(p, "GRAY_DYE", 3); put(p, "LIGHT_GRAY_DYE", 3); put(p, "LIGHT_BLUE_DYE", 3); put(p, "LIME_DYE", 3);

        // ---------------- VILLAGE / ECHANGES ----------------
        put(p, "CARTOGRAPHY_TABLE", 40); put(p, "LOOM", 20); put(p, "COMPOSTER", 15);
        put(p, "SMOKER", 40); put(p, "BLAST_FURNACE", 80); put(p, "FLETCHING_TABLE", 30);

        // ---------------- STRUCTURES / OBJETS TRES RARES ----------------
        put(p, "TOTEM_OF_UNDYING", 100); put(p, "DRAGON_EGG", 10000);
        put(p, "DRAGON_HEAD", 2500); put(p, "BEACON", 8000);
        put(p, "ENCHANTED_GOLDEN_APPLE", 500);

        putAll(p, 500, "MUSIC_DISC_5", "MUSIC_DISC_PIGSTEP", "MUSIC_DISC_OTHERSIDE",
                "MUSIC_DISC_RELIC", "MUSIC_DISC_PRECIPICE", "MUSIC_DISC_CREATOR", "MUSIC_DISC_CREATOR_MUSIC_BOX");
        putAll(p, 200, "MUSIC_DISC_13", "MUSIC_DISC_CAT", "MUSIC_DISC_BLOCKS", "MUSIC_DISC_CHIRP",
                "MUSIC_DISC_FAR", "MUSIC_DISC_MALL", "MUSIC_DISC_MELLOHI", "MUSIC_DISC_STAL",
                "MUSIC_DISC_STRAD", "MUSIC_DISC_WARD", "MUSIC_DISC_11", "MUSIC_DISC_WAIT");

        return p;
    }

    private static String[] prefixed(String prefix, String[] names) {
        String[] out = new String[names.length];
        for (int i = 0; i < names.length; i++) out[i] = prefix + names[i];
        return out;
    }

    private static void put(Map<Material, Double> map, String materialName, double price) {
        try {
            map.put(Material.valueOf(materialName), price);
        } catch (IllegalArgumentException ignored) {
            // le material n'existe pas dans cette version -> on ignore silencieusement
        }
    }

    private static void putAll(Map<Material, Double> map, double price, String... names) {
        for (String n : names) put(map, n, price);
    }

    private static void putSuffixed(Map<Material, Double> map, double price, String[] prefixes, String suffix) {
        for (String prefix : prefixes) put(map, prefix + suffix, price);
    }

    /** Applique le suffixe a toutes les couleurs sauf une (utile quand la couleur exclue a un prix different) */
    private static void putSuffixedExcept(Map<Material, Double> map, double price, String[] prefixes, String suffix, String except) {
        for (String prefix : prefixes) {
            if (prefix.equals(except)) continue;
            put(map, prefix + suffix, price);
        }
    }
}
