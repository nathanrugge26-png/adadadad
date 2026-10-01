package com.donuttools.render;

import com.donuttools.DonutToolsClient;
import com.donuttools.config.DonutConfig;
import com.donuttools.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;

public final class HighlightRenderer {
    private static int scanTicker = 0;

    private HighlightRenderer() {}

    public static void init() {}

    public static void tick(Minecraft client, ModuleManager modules) {
        if (client.level == null || client.player == null) return;
        if (++scanTicker % 3 != 0) return;

        DonutConfig c = DonutToolsClient.CONFIG;
        if (modules.storageFinder().enabled()) scan(client.level, client.player.blockPosition(), c.storageRange, false, c);
        if (modules.spawnerFinder().enabled()) scan(client.level, client.player.blockPosition(), c.spawnerRange, true, c);
    }

    private static void scan(Level level, BlockPos center, int range, boolean spawnerOnly, DonutConfig c) {
        int r = Math.min(range, 192);
        int minX = center.getX() - r;
        int maxX = center.getX() + r;
        int minY = Math.max(level.getMinY(), center.getY() - Math.min(r, 96));
        int maxY = Math.min(level.getMaxY(), center.getY() + Math.min(r, 96));
        int minZ = center.getZ() - r;
        int maxZ = center.getZ() + r;
        long maxDistanceSq = (long) r * r;

        for (int x = minX; x <= maxX; x += 1) {
            for (int y = minY; y <= maxY; y += 1) {
                for (int z = minZ; z <= maxZ; z += 1) {
                    long dx = x - center.getX();
                    long dy = y - center.getY();
                    long dz = z - center.getZ();
                    if (dx * dx + dy * dy + dz * dz > maxDistanceSq) continue;
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (spawnerOnly) {
                        if (state.is(Blocks.SPAWNER)) emit(pos, c.spawnerColor, c.spawnerOpacity, c.spawnerFillOpacity, c.spawnerMode, c);
                    } else {
                        Block block = state.getBlock();
                        if (block == Blocks.CHEST) emit(pos, c.chestColor, c.storageOpacity, c.storageFillOpacity, c.storageMode, c);
                        else if (block == Blocks.TRAPPED_CHEST) emit(pos, c.trappedChestColor, c.storageOpacity, c.storageFillOpacity, c.storageMode, c);
                        else if (block instanceof ShulkerBoxBlock) emit(pos, c.shulkerColor, c.storageOpacity, c.storageFillOpacity, c.storageMode, c);
                        else if (block == Blocks.HOPPER) emit(pos, c.hopperColor, c.storageOpacity, c.storageFillOpacity, c.storageMode, c);
                    }
                }
            }
        }
    }

    private static void emit(BlockPos pos, int color, float strokeOpacity, float fillOpacity, HighlightMode mode, DonutConfig c) {
        int stroke = withAlpha(color, strokeOpacity);
        int fill = withAlpha(color, fillOpacity);
        float width = Math.max(0.5f, c.lineWidth);
        GizmoStyle style;
        switch (mode) {
            case FILLED -> style = GizmoStyle.fill(fill);
            case OUTLINE_FILL, GLOW -> style = GizmoStyle.strokeAndFill(stroke, mode == HighlightMode.GLOW ? width * 1.6f : width, fill);
            case CORNERS, OUTLINE -> style = GizmoStyle.stroke(stroke, width);
            default -> style = GizmoStyle.stroke(stroke, width);
        }

        var properties = Gizmos.cuboid(pos, style);
        if (c.throughWalls) properties.setAlwaysOnTop();
        properties.persistForMillis(100).fadeOut();
    }

    private static int withAlpha(int argb, float opacity) {
        int a = Math.max(0, Math.min(255, Math.round(opacity * 255f)));
        return (a << 24) | (argb & 0x00FFFFFF);
    }
}
