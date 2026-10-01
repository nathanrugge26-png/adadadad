package com.donuttools.module;

import com.donuttools.config.DonutConfig;

public final class FarmAnalyzerModule extends Module {
    public FarmAnalyzerModule(DonutConfig config) {
        super("farm_analyzer", "Farm Analyzer", config.farmAnalyzer);
    }
}
