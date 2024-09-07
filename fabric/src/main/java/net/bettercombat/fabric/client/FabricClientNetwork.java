package net.bettercombat.fabric.client;

import net.bettercombat.client.ClientNetwork;
import net.bettercombat.fabric.network.FabricServerNetwork;
import net.bettercombat.network.Packets;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FabricClientNetwork {
    public static void init() {
        ClientConfigurationNetworking.registerGlobalReceiver(Packets.WeaponRegistrySync.PACKET_ID, (packet, context) -> {
            ClientNetwork.handleWeaponRegistrySync(packet);
            context.responseSender().sendPacket(new Packets.Ack(FabricServerNetwork.WeaponRegistrySyncTask.name));
        });

        ClientConfigurationNetworking.registerGlobalReceiver(Packets.ConfigSync.PACKET_ID, (packet, context) -> {
            ClientNetwork.handleConfigSync(packet);
            context.responseSender().sendPacket(new Packets.Ack(FabricServerNetwork.ConfigurationTask.name));
        });

        ClientPlayNetworking.registerGlobalReceiver(Packets.AttackAnimation.PACKET_ID, (packet, context) -> {
            ClientNetwork.handleAttackAnimation(packet);
        });

        ClientPlayNetworking.registerGlobalReceiver(Packets.AttackSound.PACKET_ID, (packet, context) -> {
            ClientNetwork.handleAttackSound(packet);
        });
    }
}
