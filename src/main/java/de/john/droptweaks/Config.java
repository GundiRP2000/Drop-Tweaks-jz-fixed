package de.john.droptweaks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class Config {
    public static final Set<String> blocked = new HashSet<>();
    public static int style = 0;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static class Data { int style; List<String> blocked = new ArrayList<>(); }

    private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("johns_drop_tweaks.json"); }

    public static boolean isBlocked(ItemStack stack) {
        if (stack.isEmpty() || blocked.isEmpty()) return false;
        return blocked.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
    }

    public static void load() {
        try {
            Path f = file();
            if (!Files.exists(f)) return;
            Data d = GSON.fromJson(Files.readString(f), Data.class);
            if (d == null) return;
            style = Math.max(0, Math.min(d.style, Style.ALL.length - 1));
            blocked.clear();
            if (d.blocked != null) blocked.addAll(d.blocked);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void save() {
        try {
            Data d = new Data();
            d.style = style;
            d.blocked = new ArrayList<>(new TreeSet<>(blocked));
            Files.writeString(file(), GSON.toJson(d));
        } catch (Exception e) { e.printStackTrace(); }
    }
}
