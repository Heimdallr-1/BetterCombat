package net.bettercombat.neoforge;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(BetterCombatMod.ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modEventBus) {
        BetterCombatMod.init();
        SOUND_EVENTS.register(modEventBus);
    }

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, BetterCombatMod.ID);

    static {
        SoundHelper.soundKeys.forEach(soundKey -> SOUND_EVENTS.register(soundKey, () -> SoundEvent.of(Identifier.of(BetterCombatMod.ID, soundKey))));
    }
}
