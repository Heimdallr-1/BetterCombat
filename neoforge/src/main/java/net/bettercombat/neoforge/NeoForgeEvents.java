package net.bettercombat.neoforge;

import net.bettercombat.BetterCombatMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@EventBusSubscriber(modid = BetterCombatMod.ID, bus = EventBusSubscriber.Bus.GAME)
public class NeoForgeEvents {
    @SubscribeEvent
    public static void register(final ServerAboutToStartEvent event) {
        BetterCombatMod.loadWeaponAttributes(event.getServer());
    }
}
