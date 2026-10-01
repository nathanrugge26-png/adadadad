package com.donuttools.farm;

import com.donuttools.config.DonutConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Farm calculator foundation. Farm production is user-editable; prices can later be supplied
 * by a live DonutSMP market provider without changing the GUI/module code.
 */
public final class FarmAnalyzer {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DonutConfig config;
    private final List<FarmProfile> farms = new ArrayList<>();
    private final Map<String, Double> prices = new HashMap<>();
    private FarmResult best;
    private long lastRefresh;

    public FarmAnalyzer(DonutConfig config) { this.config = config; }

    public void load() {
        farms.clear();
        Path path = FabricLoader.getInstance().getConfigDir().resolve("donuttools-farms.json");
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                Type type = new TypeToken<List<FarmProfile>>() {}.getType();
                List<FarmProfile> loaded = GSON.fromJson(reader, type);
                if (loaded != null) farms.addAll(loaded);
            } catch (Exception ignored) {}
        }
        if (farms.isEmpty()) {
            farms.add(new FarmProfile("Cactus", List.of(new FarmProfile.Output("minecraft:cactus", 100000)), 0));
            farms.add(new FarmProfile("Sugar Cane", List.of(new FarmProfile.Output("minecraft:sugar_cane", 85000)), 0));
            farms.add(new FarmProfile("Pumpkin", List.of(new FarmProfile.Output("minecraft:pumpkin", 55000)), 0));
            saveFarms(path);
        }
        // Placeholder local prices. Replace with the live market provider in the next milestone.
        prices.putIfAbsent("minecraft:cactus", 0.0);
        prices.putIfAbsent("minecraft:sugar_cane", 0.0);
        prices.putIfAbsent("minecraft:pumpkin", 0.0);
        recalculate();
    }

    private void saveFarms(Path path) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) { GSON.toJson(farms, writer); }
        } catch (Exception ignored) {}
    }

    public void tick(Minecraft client) {
        long now = System.currentTimeMillis();
        if (now - lastRefresh >= Math.max(5, config.farmRefreshSeconds) * 1000L) {
            lastRefresh = now;
            recalculate();
        }
    }

    private void recalculate() {
        best = farms.stream()
                .map(this::calculate)
                .max(Comparator.comparingDouble(FarmResult::moneyPerHour))
                .orElse(null);
    }

    private FarmResult calculate(FarmProfile farm) {
        double revenue = -farm.operatingCostPerHour();
        StringBuilder produced = new StringBuilder();
        for (FarmProfile.Output output : farm.outputs()) {
            double price = prices.getOrDefault(output.itemId(), 0.0);
            revenue += output.itemsPerHour() * price;
            if (!produced.isEmpty()) produced.append(", ");
            produced.append(output.itemId().replace("minecraft:", ""));
        }
        return new FarmResult(farm.name(), Math.max(0, revenue), produced.toString());
    }

    public FarmResult best() { return best; }
    public List<FarmProfile> farms() { return List.copyOf(farms); }
    public Map<String, Double> prices() { return Map.copyOf(prices); }
}
