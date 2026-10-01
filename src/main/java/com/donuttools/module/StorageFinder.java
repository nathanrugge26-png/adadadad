package com.donuttools.module;

import com.donuttools.config.DonutConfig;

public final class StorageFinder extends Module {
    public StorageFinder(DonutConfig config) {
        super("storage_finder", "Storage Finder", config.storageFinder);
    }
}
