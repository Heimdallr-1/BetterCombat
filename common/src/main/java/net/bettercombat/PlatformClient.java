package net.bettercombat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class PlatformClient {
    @ExpectPlatform
    public static void onEmptyLeftClick(PlayerEntity player) {
        throw new AssertionError();
    }
}
