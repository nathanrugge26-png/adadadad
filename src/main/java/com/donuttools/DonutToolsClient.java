package com.donuttools;

import com.donuttools.config.DonutConfig;
import com.donuttools.farm.FarmAnalyzer;
import com.donuttools.gui.DonutScreen;
import com.donuttools.module.ModuleManager;
import com.donuttools.render.HighlightRenderer;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class DonutToolsClient implements ClientModInitializer {
    public static final String MOD_ID = "donuttools";
    public static final DonutConfig CONFIG = DonutConfig.load();
    public static final ModuleManager MODULES = new ModuleManager(CONFIG);
    public static final FarmAnalyzer FARM_ANALYZER = new FarmAnalyzer(CONFIG);

    private static KeyMapping openGuiKey;

    @Override
    public void onInitializeClient() {
        MODULES.registerDefaults();
        FARM_ANALYZER.load();
        HighlightRenderer.init();

        KeyMapping.Category category = KeyMapping.Category.register(id("key_category"));
        openGuiKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.donuttools.open_gui",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new DonutScreen());
                }
            }
            MODULES.tick(client);
            FARM_ANALYZER.tick(client);
            HighlightRenderer.tick(client, MODULES);
        });
    }

    public static net.minecraft.resources.Identifier id(String path) {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
