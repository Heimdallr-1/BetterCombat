package net.bettercombat.fabric.client;

import net.bettercombat.client.ClientNetwork;
import net.bettercombat.fabric.network.FabricServerNetwork;
import net.bettercombat.network.Packets;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class FabricClientNetwork {
    public static void init() {
        PayloadTypeRegistry.configurationS2C().register(Packets.WeaponRegistrySync.PACKET_ID, Packets.WeaponRegistrySync.CODEC);
        ClientConfigurationNetworking.registerGlobalReceiver(Packets.WeaponRegistrySync.PACKET_ID, (packet, context) -> {
            ClientNetwork.handleWeaponRegistrySync(packet);
            context.responseSender().sendPacket(new Packets.Ack(FabricServerNetwork.WeaponRegistrySyncTask.name));
        });

        PayloadTypeRegistry.configurationS2C().register(Packets.ConfigSync.PACKET_ID, Packets.ConfigSync.CODEC);
        ClientConfigurationNetworking.registerGlobalReceiver(Packets.ConfigSync.PACKET_ID, (packet, context) -> {
            ClientNetwork.handleConfigSync(packet);
            context.responseSender().sendPacket(new Packets.Ack(FabricServerNetwork.ConfigurationTask.name));
        });

        PayloadTypeRegistry.playS2C().register(Packets.AttackAnimation.PACKET_ID, Packets.AttackAnimation.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(Packets.AttackAnimation.PACKET_ID, (packet, context) -> {
            ClientNetwork.handleAttackAnimation(packet);
        });

        PayloadTypeRegistry.playS2C().register(Packets.AttackSound.PACKET_ID, Packets.AttackSound.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(Packets.AttackSound.PACKET_ID, (packet, context) -> {
            ClientNetwork.handleAttackSound(packet);
        });
    }
}
