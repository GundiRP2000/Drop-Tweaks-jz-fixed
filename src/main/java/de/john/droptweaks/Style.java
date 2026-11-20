package de.john.droptweaks;

/** Farbpaletten des Drop-Tweaks-Menüs (ARGB). */
public record Style(String name, int bg, int border, int accent, int header, int text, int cell) {
    public static final Style[] ALL = {
        // Das ursprüngliche schlichte Grau bleibt als Option erhalten.
        new Style("Classic Gray", 0xFF2B2B2B, 0xFF8B8B8B, 0xFFFFCC33, 0xFF454545, 0xFFFFFFFF, 0xFF3A3A3A),
        new Style("Neon Pulse", 0xFF090D1B, 0xFF16D9FF, 0xFFFF42D0, 0xFF14234A, 0xFFE9FAFF, 0xFF101A35),
        new Style("Woodland", 0xFF21170F, 0xFFB77A43, 0xFFE8B66A, 0xFF49301E, 0xFFFFEAD0, 0xFF332317),
        new Style("Crimson", 0xFF1B080D, 0xFFE43D55, 0xFFFF8795, 0xFF45121E, 0xFFFFE9EC, 0xFF2C1019),
        new Style("Galaxy", 0xFF100B25, 0xFF8A63FF, 0xFF55D9FF, 0xFF28194D, 0xFFF0EAFF, 0xFF1C1438),
        new Style("Emerald", 0xFF081D19, 0xFF27B889, 0xFF8DFFD0, 0xFF123B30, 0xFFE2FFF4, 0xFF102B24),
        new Style("Royal Gold", 0xFF17120A, 0xFFD7A83E, 0xFFFFE08A, 0xFF3A2B12, 0xFFFFF4D6, 0xFF29200F),
    };

    public static Style current() {
        return ALL[Math.max(0, Math.min(Config.style, ALL.length - 1))];
    }
}
