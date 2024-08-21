package net.bettercombat.neoforge.client;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.client.WeaponAttributeTooltip;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod.EventBusSubscriber(modid = BetterCombatMod.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class NeoForgeClientEvents {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event){
        WeaponAttributeTooltip.modifyTooltip(event.getItemStack(), event.getToolTip());
    }
}
