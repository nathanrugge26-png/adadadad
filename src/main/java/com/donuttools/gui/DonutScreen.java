package com.donuttools.gui;

import com.donuttools.DonutToolsClient;
import com.donuttools.config.DonutConfig;
import com.donuttools.farm.FarmResult;
import com.donuttools.module.Module;
import com.donuttools.render.HighlightMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class DonutScreen extends Screen {
    private final DonutConfig config = DonutToolsClient.CONFIG;
    private int tab = 0;
    private Module selected;
    private long openTime;

    public DonutScreen() {
        super(Component.literal("DonutTools"));
        this.openTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        selected = DonutToolsClient.MODULES.all().isEmpty() ? null : DonutToolsClient.MODULES.all().get(0);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int w = width, h = height;
        g.fill(0, 0, w, h, 0xB4141821);
        int left = 36, top = 26, right = w - 36;
        g.fill(left, top, right, h - 26, 0xF21D222A);
        g.fill(left, top, right, top + 1, config.accentColor);

        g.drawString(font, "DONUTTOOLS", left + 28, top + 18, 0xFFFFFFFF, false);
        drawTab(g, "MODULES", left + 220, tab == 0);
        drawTab(g, "SETTINGS", left + 320, tab == 1);
        drawTab(g, "FARMS", left + 420, tab == 2);

        if (tab == 0) renderModules(g, left, top + 58, right, h - 26, mouseX, mouseY);
        else if (tab == 1) renderSettings(g, left, top + 58, right, h - 26, mouseX, mouseY);
        else renderFarms(g, left, top + 58, right, h - 26);
    }

    private void drawTab(GuiGraphics g, String label, int x, boolean active) {
        if (active) g.fill(x - 14, 0 + 50, x + 72, 78, 0xFF535A64);
        g.drawString(font, label, x, 60, active ? 0xFFFFFFFF : 0xFFB9BEC6, false);
    }

    private void renderModules(GuiGraphics g, int left, int top, int right, int bottom, int mx, int my) {
        List<Module> modules = DonutToolsClient.MODULES.all();
        int cardW = 250, cardH = 176, gap = 18;
        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            int col = i % 3, row = i / 3;
            int x = left + 18 + col * (cardW + gap);
            int y = top + row * (cardH + gap);
            if (x + cardW > right - 18) continue;
            drawModuleCard(g, module, x, y, cardW, cardH, mx, my);
        }
        if (selected != null) drawOptions(g, selected, right - 320, top, 280, bottom - top);
    }

    private void drawModuleCard(GuiGraphics g, Module module, int x, int y, int w, int h, int mx, int my) {
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
        int bg = hover ? 0xFF24272D : 0xFF202225;
        g.fill(x, y, x + w, y + h, bg);
        g.fill(x, y + h - 36, x + w, y + h, module.enabled() ? config.accentColor : 0xFF3A3C40);
        g.drawString(font, module.name().toUpperCase(), x + 18, y + 18, 0xFFF4F5F7, false);
        String description = switch (module.id()) {
            case "storage_finder" -> "Chest / Shulker / Hopper highlights";
            case "spawner_finder" -> "Spawner detection and ESP";
            default -> "Live farm profitability analyzer";
        };
        g.drawString(font, description, x + 18, y + 48, 0xFF9CA2AB, false);
        g.drawString(font, "OPTIONS", x + 18, y + h - 55, 0xFFD0D3D8, false);
        String state = module.enabled() ? "ENABLED" : "DISABLED";
        int tw = font.width(state);
        g.drawString(font, state, x + w - tw - 18, y + h - 24, 0xFFFFFFFF, false);
    }

    private void drawOptions(GuiGraphics g, Module module, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, 0xE91B1E23);
        g.drawString(font, module.name(), x + 18, y + 18, 0xFFFFFFFF, false);
        int line = y + 48;
        if (module.id().equals("storage_finder")) {
            line = slider(g, "Opacity", config.storageOpacity, x + 18, line, w - 36);
            line = slider(g, "Fill", config.storageFillOpacity, x + 18, line + 8, w - 36);
            line = slider(g, "Range", config.storageRange / 192f, x + 18, line + 8, w - 36);
            g.drawString(font, "Mode: " + config.storageMode.label(), x + 18, line + 20, 0xFFD5D8DE, false);
            g.drawString(font, "Click the card to enable / disable", x + 18, line + 50, 0xFF858B94, false);
        } else if (module.id().equals("spawner_finder")) {
            line = slider(g, "Opacity", config.spawnerOpacity, x + 18, line, w - 36);
            line = slider(g, "Fill", config.spawnerFillOpacity, x + 18, line + 8, w - 36);
            line = slider(g, "Range", config.spawnerRange / 192f, x + 18, line + 8, w - 36);
            g.drawString(font, "Mode: " + config.spawnerMode.label(), x + 18, line + 20, 0xFFD5D8DE, false);
        } else {
            FarmResult best = DonutToolsClient.FARM_ANALYZER.best();
            g.drawString(font, "CURRENT BEST FARM", x + 18, line, 0xFF8F96A0, false);
            g.drawString(font, best == null ? "Waiting for market data..." : best.farmName(), x + 18, line + 24, 0xFFFFFFFF, false);
            g.drawString(font, best == null ? "—" : money(best.moneyPerHour()) + "/hr", x + 18, line + 46, config.accentColor, false);
        }
    }

    private int slider(GuiGraphics g, String label, float value, int x, int y, int w) {
        value = Math.max(0, Math.min(1, value));
        g.drawString(font, label, x, y, 0xFFD5D8DE, false);
        g.fill(x, y + 15, x + w, y + 19, 0xFF3A3D43);
        g.fill(x, y + 15, x + (int)(w * value), y + 19, config.accentColor);
        g.fill(x + (int)(w * value) - 3, y + 11, x + (int)(w * value) + 3, y + 23, 0xFFFFFFFF);
        return y + 36;
    }

    private void renderSettings(GuiGraphics g, int left, int top, int right, int bottom, int mx, int my) {
        int x = left + 28, y = top + 12;
        g.drawString(font, "APPEARANCE", x, y, config.accentColor, false);
        y += 34;
        y = slider(g, "GUI opacity", config.guiOpacity, x, y, 360);
        y += 18;
        g.drawString(font, "Accent: #" + Integer.toHexString(config.accentColor & 0xFFFFFF).toUpperCase(), x, y, 0xFFD5D8DE, false);
        y += 34;
        g.drawString(font, "Rendering", x, y, config.accentColor, false);
        y += 30;
        g.drawString(font, "Through walls: " + (config.throughWalls ? "ON" : "OFF"), x, y, 0xFFD5D8DE, false);
        y += 28;
        g.drawString(font, "Line width: " + String.format("%.1f", config.lineWidth), x, y, 0xFFD5D8DE, false);
        g.drawString(font, "Right Shift opens this GUI", x, y + 54, 0xFF858B94, false);
    }

    private void renderFarms(GuiGraphics g, int left, int top, int right, int bottom) {
        g.drawString(font, "FARM ANALYZER", left + 24, top + 12, 0xFFFFFFFF, false);
        g.drawString(font, "Calculates revenue from production rates × current prices.", left + 24, top + 38, 0xFF9298A2, false);
        FarmResult best = DonutToolsClient.FARM_ANALYZER.best();
        int x = left + 24, y = top + 82;
        g.fill(x, y, x + 460, y + 150, 0xFF202225);
        g.drawString(font, "CURRENT BEST", x + 20, y + 18, config.accentColor, false);
        g.drawString(font, best == null ? "No live market data" : best.farmName(), x + 20, y + 48, 0xFFFFFFFF, false);
        g.drawString(font, best == null ? "Set market prices / API in config" : money(best.moneyPerHour()) + " / hour", x + 20, y + 74, 0xFFFFFFFF, false);
        g.drawString(font, best == null ? "" : "Produces: " + best.produces(), x + 20, y + 102, 0xFFB5BBC4, false);
    }

    private static String money(double value) {
        if (value >= 1_000_000_000) return String.format("%.2fB", value / 1_000_000_000);
        if (value >= 1_000_000) return String.format("%.2fM", value / 1_000_000);
        if (value >= 1_000) return String.format("%.2fK", value / 1_000);
        return String.format("%.0f", value);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int left = 36;
        if (mouseY >= 50 && mouseY <= 82) {
            if (mouseX >= left + 206 && mouseX < left + 306) tab = 0;
            else if (mouseX >= left + 306 && mouseX < left + 406) tab = 1;
            else if (mouseX >= left + 406 && mouseX < left + 506) tab = 2;
            return true;
        }
        if (tab == 0 && button == 0) {
            List<Module> modules = DonutToolsClient.MODULES.all();
            int cardW = 250, cardH = 176, gap = 18, top = 84;
            for (int i = 0; i < modules.size(); i++) {
                int col = i % 3, row = i / 3;
                int x = left + 18 + col * (cardW + gap);
                int y = top + row * (cardH + gap);
                if (mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH) {
                    selected = modules.get(i);
                    selected.toggle();
                    config.storageFinder = DonutToolsClient.MODULES.storageFinder().enabled();
                    config.spawnerFinder = DonutToolsClient.MODULES.spawnerFinder().enabled();
                    config.farmAnalyzer = DonutToolsClient.MODULES.farmAnalyzer().enabled();
                    config.save();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        config.save();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
