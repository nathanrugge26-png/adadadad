package com.donuttools.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.donuttools.render.HighlightMode;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DonutConfig {
    public boolean storageFinder = true;
    public boolean spawnerFinder = true;
    public boolean farmAnalyzer = true;

    public int storageRange = 96;
    public int spawnerRange = 128;
    public float storageOpacity = 0.85f;
    public float spawnerOpacity = 0.95f;
    public float storageFillOpacity = 0.16f;
    public float spawnerFillOpacity = 0.20f;
    public float lineWidth = 2.25f;
    public boolean throughWalls = true;
    public HighlightMode storageMode = HighlightMode.OUTLINE;
    public HighlightMode spawnerMode = HighlightMode.OUTLINE_FILL;

    public int chestColor = 0xFFFFB347;
    public int trappedChestColor = 0xFFFF4D6D;
    public int shulkerColor = 0xFFB56CFF;
    public int hopperColor = 0xFF62C7FF;
    public int spawnerColor = 0xFFFF3B5C;
    public int accentColor = 0xFFFF315A;
    public float guiOpacity = 0.96f;

    public int farmRefreshSeconds = 30;
    public String marketUrl = "";
    public String marketApiKey = "";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("donuttools.json");
    }

    public static DonutConfig load() {
        try {
            if (Files.exists(path())) {
                try (Reader reader = Files.newBufferedReader(path())) {
                    DonutConfig loaded = GSON.fromJson(reader, DonutConfig.class);
                    return loaded != null ? loaded : new DonutConfig();
                }
            }
        } catch (Exception ignored) { }
        DonutConfig config = new DonutConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(path().getParent());
            try (Writer writer = Files.newBufferedWriter(path())) {
                GSON.toJson(this, writer);
            }
        } catch (Exception ignored) { }
    }
}
