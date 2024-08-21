package net.bettercombat.fabric.network;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
                System.out.println("Starting ConfigurationTask");
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
                System.out.println("Starting WeaponRegistrySyncTask, chunks: " + WeaponRegistry.getEncodedRegistry().chunks().size());
                handler.addTask(new WeaponRegistrySyncTask(WeaponRegistry.getEncodedRegistry().chunks()));
            } else {
                handler.disconnect(Text.literal("Network configuration task not supported: " + WeaponRegistrySyncTask.name));
            }
        });

        ServerConfigurationNetworking.registerGlobalReceiver(Packets.Ack.ID, (server, handler, buf, responseSender) -> {
            var packet = Packets.Ack.read(buf);
            // Warning: if you do not call completeTask, the client gets stuck!
            if (packet.code().equals(ConfigurationTask.name)) {
                handler.completeTask(ConfigurationTask.KEY);
            }
            if (packet.code().equals(WeaponRegistrySyncTask.name)) {
                handler.completeTask(WeaponRegistrySyncTask.KEY);
            }
        });

        // Play stage

        ServerPlayNetworking.registerGlobalReceiver(Packets.AttackAnimation.ID, (server, player, handler, buf, responseSender) -> {
            var packet = Packets.AttackAnimation.read(buf);
            ServerNetwork.handleAttackAnimation(packet, server, player);
        });

        ServerPlayNetworking.registerGlobalReceiver(Packets.C2S_AttackRequest.ID, (server, player, handler, buf, responseSender) -> {
            var packet = Packets.C2S_AttackRequest.read(buf);
            ServerNetwork.handleAttackRequest(packet, server, player, handler);
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
            var buffer = PacketByteBufs.create();
            new Packets.ConfigSync(this.configString).write(buffer);
            sender.accept(ServerConfigurationNetworking.createS2CPacket(Packets.ConfigSync.ID, buffer));
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
            var buffer = PacketByteBufs.create();
            new Packets.WeaponRegistrySync(encodedRegistry).write(buffer);
            sender.accept(ServerConfigurationNetworking.createS2CPacket(Packets.WeaponRegistrySync.ID, buffer));
        }
    }
}
