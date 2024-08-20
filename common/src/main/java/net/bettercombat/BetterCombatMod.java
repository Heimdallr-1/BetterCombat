package net.bettercombat;

import com.mojang.logging.LogUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.bettercombat.compatibility.CompatibilityFlags;
import net.bettercombat.config.FallbackConfig;
import net.bettercombat.config.ServerConfig;
import net.bettercombat.config.ServerConfigWrapper;
import net.bettercombat.logic.WeaponAttributesFallback;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.server.MinecraftServer;
import net.tinyconfig.ConfigManager;
import org.slf4j.Logger;

public class BetterCombatMod {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final String ID = "bettercombat";
    public static ServerConfig config;
    private static FallbackConfig fallbackDefault = FallbackConfig.createDefault();
    public static ConfigManager<FallbackConfig> fallbackConfig = new ConfigManager<>
            ("fallback_compatibility", fallbackDefault)
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();

    public static void init() {
        AutoConfig.register(ServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        // Intuitive way to load a config :)
        config = AutoConfig.getConfigHolder(ServerConfigWrapper.class).getConfig().server;
        loadFallbackConfig();
        CompatibilityFlags.initialize();
    }

    private static void loadFallbackConfig() {
        fallbackConfig.load();
        if (fallbackConfig.value == null) {
            // Most likely corrupt config
            fallbackConfig.value = FallbackConfig.createDefault();
        }
        if (fallbackConfig.value.schema_version < fallbackDefault.schema_version) {
            fallbackConfig.value = FallbackConfig.migrate(fallbackConfig.value, FallbackConfig.createDefault());
        }
        fallbackConfig.save();
    }

    public static void loadWeaponAttributes(MinecraftServer server) {
        WeaponRegistry.loadAttributes(server.getResourceManager());
        if (config.fallback_compatibility_enabled) {
            WeaponAttributesFallback.initialize();
        }
        WeaponRegistry.encodeRegistry();
    }
}