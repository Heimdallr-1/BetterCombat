package net.bettercombat.neoforge.client;

import me.shedaniel.autoconfig.AutoConfig;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.Keybindings;
import net.bettercombat.config.ClientConfigWrapper;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = BetterCombatMod.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NeoForgeClientMod {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event){
        Keybindings.all.forEach(event::register);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        BetterCombatClientMod.init();
        BetterCombatClientMod.loadAnimation();

        ModelPredicateProviderRegistry.registerGeneric(Identifier.of(BetterCombatMod.ID, "loaded"), (stack, world, entity, seed) -> {
            return 1.0F;
        });

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> {
            return (IConfigScreenFactory) (modContainer, parent) -> AutoConfig.getConfigScreen(ClientConfigWrapper.class, parent).get();
        });
    }
}