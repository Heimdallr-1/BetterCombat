package net.bettercombat.neoforge.network;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.client.ClientNetwork;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = BetterCombatMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkEvents {
    @SubscribeEvent
    public static void register(final OnGameConfigurationEvent event) {
        event.register(new ConfigurationTask());
        event.register(new WeaponRegistrySyncTask());
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(BetterCombatMod.ID);

        // Server config stage

        registrar.configuration(Packets.Ack.ID, Packets.Ack::read, handler -> {
            handler.server((packet, context) -> {
                if (packet.code().equals(ConfigurationTask.name)) {
                    context.taskCompletedHandler().onTaskCompleted(ConfigurationTask.KEY);
                }
                if (packet.code().equals(WeaponRegistrySyncTask.name)) {
                    context.taskCompletedHandler().onTaskCompleted(WeaponRegistrySyncTask.KEY);
                }
            });
        });

        // Server play stage

        registrar.play(Packets.C2S_AttackRequest.ID, Packets.C2S_AttackRequest::read, handler -> {
            handler.server((packet, context) -> {
                var player = (ServerPlayerEntity)context.player().get();
                var server = player.server;
                var vanillaHandler = player.networkHandler;
                ServerNetwork.handleAttackRequest(packet, server, player, vanillaHandler);
            });
        });

        // Shared play stage

        registrar.play(Packets.AttackAnimation.ID, Packets.AttackAnimation::read, handler -> {
            handler.client((packet, context) -> {
                        ClientNetwork.handleAttackAnimation(packet);
                    })
                    .server((packet, context) -> {
                        var player = (ServerPlayerEntity)context.player().get();
                        var server = player.server;
                        ServerNetwork.handleAttackAnimation(packet, server, player);
                    });
        });

        // Client config stage

        registrar.configuration(Packets.ConfigSync.ID, Packets.ConfigSync::read, handler -> {
            handler.client((packet, context) -> {
                ClientNetwork.handleConfigSync(packet);
                context.replyHandler().send(new Packets.Ack(ConfigurationTask.name));
            });
        });

        registrar.configuration(Packets.WeaponRegistrySync.ID, Packets.WeaponRegistrySync::read, handler -> {
            handler.client((packet, context) -> {
                ClientNetwork.handleWeaponRegistrySync(packet);
                context.replyHandler().send(new Packets.Ack(WeaponRegistrySyncTask.name));
            });
        });

        // Client play stage

        registrar.play(Packets.AttackSound.ID, Packets.AttackSound::read, handler -> {
            handler.client((packet, context) -> {
                ClientNetwork.handleAttackSound(packet);
            });
        });
    }

    public record ConfigurationTask() implements ICustomConfigurationTask {
        public static final String name = BetterCombatMod.ID + ":" + "config";
        public static final Key KEY = new Key(name);

        @Override
        public Key getKey() {
            return KEY;
        }

        @Override
        public void run(Consumer<CustomPayload> sender) {
            var configString = Packets.ConfigSync.serialize(BetterCombatMod.config);
            var packet = new Packets.ConfigSync(configString);
            sender.accept(packet);
        }
    }

    public record WeaponRegistrySyncTask() implements ICustomConfigurationTask {
        public static final String name = BetterCombatMod.ID + ":" + "weapon_registry";
        public static final Key KEY = new Key(name);

        @Override
        public Key getKey() {
            return KEY;
        }

        @Override
        public void run(Consumer<CustomPayload> sender) {
            var packet = new Packets.WeaponRegistrySync(WeaponRegistry.getEncodedRegistry().chunks());
            sender.accept(packet);
        }
    }
}