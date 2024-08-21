package net.bettercombat.neoforge;

import net.bettercombat.BetterCombatMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod.EventBusSubscriber(modid = BetterCombatMod.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NeoForgeEvents {
    @SubscribeEvent
    public static void register(final ServerAboutToStartEvent event) {
        BetterCombatMod.loadWeaponAttributes(event.getServer());
    }
}
