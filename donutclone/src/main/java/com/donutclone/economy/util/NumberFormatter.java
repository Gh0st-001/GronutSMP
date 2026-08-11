package com.donutclone.economy.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Formate / parse les nombres avec les suffixes K, M, B, T, Qa, Qi, Sx, Sp,
 * O, N, Dc, Ud, Dd, Td, Qad, Qid, Sxd, Spd, Od.
 */
public class NumberFormatter {

    // Ordre du plus grand au plus petit pour le formatage
    private static final Map<Long, String> SUFFIXES_BY_POWER = new LinkedHashMap<>();
    // puissance de 10 -> suffixe. On utilise double pour aller au-dela de long.

    private static final String[] SUFFIX_LIST = {
            "", "K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "O", "N", "Dc",
            "Ud", "Dd", "Td", "Qad", "Qid", "Sxd", "Spd", "Od"
    };

    /** Formate un montant en "1.2K", "45M", "200M", "5B", etc. */
    public static String format(double value) {
        if (value < 1000) {
            // pas de decimales pour les petits nombres
            if (value == Math.floor(value)) {
                return String.valueOf((long) value);
            }
            return String.format("%.2f", value);
        }

        int magnitude = 0;
        double reduced = value;
        while (reduced >= 1000 && magnitude < SUFFIX_LIST.length - 1) {
            reduced /= 1000.0;
            magnitude++;
        }

        String suffix = SUFFIX_LIST[magnitude];
        // 2 decimales max, sans zeros inutiles
        String formatted = String.format("%.2f", reduced);
        if (formatted.endsWith("00")) {
            formatted = formatted.substring(0, formatted.length() - 3);
        } else if (formatted.endsWith("0")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        return formatted + suffix;
    }

    /** Formate avec le symbole $ devant, ex: "$45M" */
    public static String formatMoney(double value) {
        return "$" + format(value);
    }

    /**
     * Parse une chaine du type "40K", "2.5M", "1000" vers sa valeur numerique.
     * Retourne -1 si le format est invalide.
     */
    public static double parse(String input) {
        if (input == null || input.isEmpty()) return -1;
        input = input.trim();

        // essaie d'abord un nombre pur
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ignored) {
            // continue, il y a peut-etre un suffixe
        }

        // trouve ou commence le suffixe (les lettres a la fin)
        int splitIndex = input.length();
        while (splitIndex > 0 && Character.isLetter(input.charAt(splitIndex - 1))) {
            splitIndex--;
        }
        if (splitIndex == input.length() || splitIndex == 0) return -1;

        String numberPart = input.substring(0, splitIndex);
        String suffixPart = input.substring(splitIndex);

        double base;
        try {
            base = Double.parseDouble(numberPart);
        } catch (NumberFormatException e) {
            return -1;
        }

        for (int i = 0; i < SUFFIX_LIST.length; i++) {
            if (SUFFIX_LIST[i].equalsIgnoreCase(suffixPart)) {
                return base * Math.pow(1000, i);
            }
        }
        return -1;
    }
}
