package com.donuttools.farm;

import java.util.List;

public record FarmProfile(String name, List<Output> outputs, double operatingCostPerHour) {
    public record Output(String itemId, double itemsPerHour) {}
}
