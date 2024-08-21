package net.bettercombat.fabric.client;

import net.bettercombat.client.ClientNetwork;
import net.bettercombat.fabric.network.FabricServerNetwork;
import net.bettercombat.network.Packets;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FabricClientNetwork {
    public static void init() {
        ClientConfigurationNetworking.registerGlobalReceiver(Packets.WeaponRegistrySync.ID, (client, handler, buf, responseSender) -> {
            var packet = Packets.WeaponRegistrySync.read(buf);
            System.out.println("Received weapon registry sync packet 2");
            ClientNetwork.handleWeaponRegistrySync(packet);
            responseSender.sendPacket(new Packets.Ack(FabricServerNetwork.WeaponRegistrySyncTask.name));
        });

        ClientConfigurationNetworking.registerGlobalReceiver(Packets.ConfigSync.ID, (client, handler, buf, responseSender) -> {
            var packet = Packets.ConfigSync.read(buf);
            System.out.println("Received config sync packet");
            ClientNetwork.handleConfigSync(packet);
            responseSender.sendPacket(new Packets.Ack(FabricServerNetwork.ConfigurationTask.name));
        });

        ClientPlayNetworking.registerGlobalReceiver(Packets.AttackAnimation.ID, (client, handler, buf, responseSender) -> {
            var packet = Packets.AttackAnimation.read(buf);
            ClientNetwork.handleAttackAnimation(packet);
        });

        ClientPlayNetworking.registerGlobalReceiver(Packets.AttackSound.ID, (client, handler, buf, responseSender) -> {
            var packet = Packets.AttackSound.read(buf);
            ClientNetwork.handleAttackSound(packet);
        });
    }
}
