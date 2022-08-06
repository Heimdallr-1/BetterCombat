package net.bettercombat;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.bettercombat.config.BetterCombatConfig;
import net.bettercombat.config.FallbackConfig;
import net.bettercombat.config.ServerConfig;
import net.bettercombat.logic.WeaponAttributesFallback;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.ServerNetwork;
import net.bettercombat.utils.SoundHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.tinyconfig.ConfigManager;

public class BetterCombat implements ModInitializer {
    public static final String MODID = "bettercombat";
//    public static BetterCombatConfig clothConfig;
    public static ServerConfig config = new ServerConfig();
    public static ConfigManager<FallbackConfig> fallbackConfig = new ConfigManager<FallbackConfig>
            ("fallback_compatibility", FallbackConfig.createDefault())
            .builder()
            .setDirectory(MODID)
            .sanitize(true)
            .build();

    @Override
    public void onInitialize() {
//        clothConfig = AutoConfig.getConfigHolder(BetterCombatConfig.class).getConfig();
        // AutoConfig.getConfigHolder(BetterCombatConfig.class).save();

        config.load();
        fallbackConfig.refresh();
        ServerNetwork.initializeHandlers();
        ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer) -> {
            WeaponRegistry.loadAttributes(minecraftServer.getResourceManager());
            if (config.fallback_compatibility_enabled) {
                WeaponAttributesFallback.initialize();
            }
            WeaponRegistry.encodeRegistry();
        });
        SoundHelper.registerSounds();
    }
}