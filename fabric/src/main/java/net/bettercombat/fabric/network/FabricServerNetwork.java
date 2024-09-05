package net.bettercombat.fabric.network;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerConfigurationTask;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class FabricServerNetwork {
    public static void init() {
        // Config stage

        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            // This if block is required! Otherwise the client gets stuck in connection screen
            // if the client cannot handle the packet.
            if (ServerConfigurationNetworking.canSend(handler, Packets.ConfigSync.ID)) {
                // System.out.println("Starting ConfigurationTask");
                handler.addTask(new ConfigurationTask(Packets.ConfigSync.serialize(BetterCombatMod.config)));
            } else {
                handler.disconnect(Text.literal("Network configuration task not supported: " + ConfigurationTask.name));
            }
        });
        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (ServerConfigurationNetworking.canSend(handler, Packets.WeaponRegistrySync.ID)) {
                if (WeaponRegistry.getEncodedRegistry().chunks().isEmpty()) {
                    throw new AssertionError("Weapon registry is empty!");
                }
                // System.out.println("Starting WeaponRegistrySyncTask, chunks: " + WeaponRegistry.getEncodedRegistry().chunks().size());
                handler.addTask(new WeaponRegistrySyncTask(WeaponRegistry.getEncodedRegistry().chunks()));
            } else {
                handler.disconnect(Text.literal("Network configuration task not supported: " + WeaponRegistrySyncTask.name));
            }
        });

        PayloadTypeRegistry.configurationC2S().register(Packets.Ack.PACKET_ID, Packets.Ack.CODEC);
        ServerConfigurationNetworking.registerGlobalReceiver(Packets.Ack.PACKET_ID, (packet, context) -> {
            // Warning: if you do not call completeTask, the client gets stuck!
            if (packet.code().equals(ConfigurationTask.name)) {
                context.networkHandler().completeTask(ConfigurationTask.KEY);
            }
            if (packet.code().equals(WeaponRegistrySyncTask.name)) {
                context.networkHandler().completeTask(WeaponRegistrySyncTask.KEY);
            }
        });

        // Play stage

        PayloadTypeRegistry.playC2S().register(Packets.AttackAnimation.PACKET_ID, Packets.AttackAnimation.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(Packets.AttackAnimation.PACKET_ID, (packet, context) -> {
            ServerNetwork.handleAttackAnimation(packet, context.server(), context.player());
        });

        PayloadTypeRegistry.playC2S().register(Packets.C2S_AttackRequest.PACKET_ID, Packets.C2S_AttackRequest.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(Packets.C2S_AttackRequest.PACKET_ID, (packet, context) -> {
            ServerNetwork.handleAttackRequest(packet, context.server(), context.player(), context.player().networkHandler);
        });
    }

    public record ConfigurationTask(String configString) implements ServerPlayerConfigurationTask {
        public static final String name = BetterCombatMod.ID + ":" + "config";
        public static final Key KEY = new Key(name);

        @Override
        public Key getKey() {
            return KEY;
        }

        @Override
        public void sendPacket(Consumer<Packet<?>> sender) {
            var packet = new Packets.ConfigSync(this.configString);
            sender.accept(ServerConfigurationNetworking.createS2CPacket(packet));
        }
    }

    public record WeaponRegistrySyncTask(List<String> encodedRegistry) implements ServerPlayerConfigurationTask {
        public static final String name = BetterCombatMod.ID + ":" + "weapon_registry";
        public static final Key KEY = new Key(name);

        @Override
        public Key getKey() {
            return KEY;
        }

        @Override
        public void sendPacket(Consumer<Packet<?>> sender) {
            var packet = new Packets.WeaponRegistrySync(encodedRegistry);
            sender.accept(ServerConfigurationNetworking.createS2CPacket(packet));
        }
    }
}
