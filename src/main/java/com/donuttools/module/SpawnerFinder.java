package com.donuttools.module;

import com.donuttools.config.DonutConfig;

public final class SpawnerFinder extends Module {
    public SpawnerFinder(DonutConfig config) {
        super("spawner_finder", "Spawner Finder", config.spawnerFinder);
    }
}
