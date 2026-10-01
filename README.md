# DonutTools — Fabric 26.1.x

Client-side utility toolkit inspired by the supplied DonutSMP Toolkit UI reference.

## Included in v0.1.0

- Custom dark click GUI with Modules / Settings / Farms tabs.
- Storage Finder: chest, trapped chest, shulker boxes and hoppers.
- Spawner Finder: mob spawner highlighting.
- Through-wall highlights using Minecraft 26.x Gizmos.
- Outline / filled / outline+fill / glow / corners modes in the shared renderer model.
- Per-module opacity, fill opacity, range, line width and colors in config.
- Farm Analyzer framework with editable farm profiles.
- Right Shift opens the GUI.

## Important

Minecraft 26.1 uses Java 25 and the new unobfuscated Fabric Loom toolchain. Fabric's 26.1 guidance uses `net.fabricmc.fabric-loom`, not the old remapping plugin.

The Farm Analyzer is intentionally separated into farm production data and market-price data. The supplied starter profiles contain production-rate examples but zero prices, so the GUI will say that market data is unavailable until a price provider is connected. This avoids presenting fake current DonutSMP prices as facts.

## Build

1. Install JDK 25.
2. Open this folder in IntelliJ IDEA 2025.3+.
3. Let Gradle import the project.
4. Run `gradlew runClient`.
5. Build with `gradlew build`.

The built mod will be in `build/libs/`.

## Configuration

After running once, the mod creates:

- `.minecraft/config/donuttools.json`
- `.minecraft/config/donuttools-farms.json`

Farm profiles use `itemsPerHour` and item IDs. A live DonutSMP market adapter will be added as the next milestone so the analyzer can calculate current $/hour rather than relying on static prices.
