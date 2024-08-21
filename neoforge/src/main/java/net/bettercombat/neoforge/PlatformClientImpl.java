package net.bettercombat.neoforge;

import net.minecraft.entity.player.PlayerEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class PlatformClientImpl {
    public static void onEmptyLeftClick(PlayerEntity player) {
        NeoForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickEmpty(player));
    }
}
