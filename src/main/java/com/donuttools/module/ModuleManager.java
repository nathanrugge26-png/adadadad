package com.donuttools.module;

import com.donuttools.config.DonutConfig;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private final DonutConfig config;
    private final List<Module> modules = new ArrayList<>();
    private StorageFinder storageFinder;
    private SpawnerFinder spawnerFinder;
    private FarmAnalyzerModule farmAnalyzer;

    public ModuleManager(DonutConfig config) { this.config = config; }

    public void registerDefaults() {
        storageFinder = new StorageFinder(config);
        spawnerFinder = new SpawnerFinder(config);
        farmAnalyzer = new FarmAnalyzerModule(config);
        modules.clear();
        modules.add(storageFinder);
        modules.add(spawnerFinder);
        modules.add(farmAnalyzer);
    }

    public List<Module> all() { return Collections.unmodifiableList(modules); }
    public StorageFinder storageFinder() { return storageFinder; }
    public SpawnerFinder spawnerFinder() { return spawnerFinder; }
    public FarmAnalyzerModule farmAnalyzer() { return farmAnalyzer; }

    public void tick(Minecraft client) {
        config.storageFinder = storageFinder.enabled();
        config.spawnerFinder = spawnerFinder.enabled();
        config.farmAnalyzer = farmAnalyzer.enabled();
    }

    public void save() { config.save(); }
}
