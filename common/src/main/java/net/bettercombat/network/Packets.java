package net.bettercombat.network;

import com.google.gson.Gson;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.config.ServerConfig;
import net.bettercombat.logic.AnimatedHand;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class Packets {
    public record C2S_AttackRequest(int comboCount, boolean isSneaking, int selectedSlot, int[] entityIds) {
        public C2S_AttackRequest(int comboCount, boolean isSneaking, int selectedSlot, List<Entity> entities) {
            this(comboCount, isSneaking, selectedSlot, convertEntityList(entities));
        }

        private static int[] convertEntityList(List<Entity> entities) {
            int[] ids = new int[entities.size()];
            for(int i = 0; i < entities.size(); i++) {
                ids[i] = entities.get(i).getId();
            }
            return ids;
        }

        public static Identifier ID = new Identifier(BetterCombatMod.ID, "c2s_request_attack");
        public static boolean UseVanillaPacket = true;
        public PacketByteBuf write() {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeInt(comboCount);
            buffer.writeBoolean(isSneaking);
            buffer.writeInt(selectedSlot);
            buffer.writeIntArray(entityIds);
            return buffer;
        }

        public static C2S_AttackRequest read(PacketByteBuf buffer) {
            int comboCount = buffer.readInt();
            boolean isSneaking = buffer.readBoolean();
            int selectedSlot = buffer.readInt();
            int[] ids = buffer.readIntArray();
            return new C2S_AttackRequest(comboCount, isSneaking, selectedSlot, ids);
        }
    }

    public record AttackAnimation(int playerId, AnimatedHand animatedHand, String animationName, float length, float upswing) {
        public static Identifier ID = new Identifier(BetterCombatMod.ID, "attack_animation");
        public static String StopSymbol = "!STOP!";
        public static AttackAnimation stop(int playerId, int length) { return new AttackAnimation(playerId, AnimatedHand.MAIN_HAND, StopSymbol, length, 0); }

        public PacketByteBuf write() {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeInt(playerId);
            buffer.writeInt(animatedHand.ordinal());
            buffer.writeString(animationName);
            buffer.writeFloat(length);
            buffer.writeFloat(upswing);
            return buffer;
        }

        public static AttackAnimation read(PacketByteBuf buffer) {
            int playerId = buffer.readInt();
            var animatedHand = AnimatedHand.values()[buffer.readInt()];
            String animationName = buffer.readString();
            float length = buffer.readFloat();
            float upswing = buffer.readFloat();
            return new AttackAnimation(playerId, animatedHand, animationName, length, upswing);
        }
    }

    public record AttackSound(double x, double y, double z, String soundId, float volume, float pitch, long seed) {
        public static Identifier ID = new Identifier(BetterCombatMod.ID, "attack_sound");
        public PacketByteBuf write() {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeDouble(x);
            buffer.writeDouble(y);
            buffer.writeDouble(z);
            buffer.writeString(soundId);
            buffer.writeFloat(volume);
            buffer.writeFloat(pitch);
            buffer.writeLong(seed);
            return buffer;
        }

        public static AttackSound read(PacketByteBuf buffer) {
            var x = buffer.readDouble();
            var y = buffer.readDouble();
            var z = buffer.readDouble();
            var soundId = buffer.readString();
            var volume = buffer.readFloat();
            var pitch = buffer.readFloat();
            var seed = buffer.readLong();
            return new AttackSound(x, y, z, soundId, volume, pitch, seed);
        }
    }

    public record WeaponRegistrySync(List<String> chunks) {
        public static Identifier ID = new Identifier(BetterCombatMod.ID, "weapon_registry");
        public PacketByteBuf write() {
            var buffer = PacketByteBufs.create();
            buffer.writeInt(chunks.size());
            for (var chunk: chunks) {
                buffer.writeString(chunk);
            }
            return buffer;
        }

        public static ServerConfig read(PacketByteBuf buffer) {
            var gson = new Gson();
            var json = buffer.readString();
            var config = gson.fromJson(json, ServerConfig.class);
            return config;
        }
    }

    public record ConfigSync(String json) {
        public static Identifier ID = new Identifier(BetterCombatMod.ID, "config_sync");

        public static String serialize(ServerConfig config) {
            var gson = new Gson();
            return gson.toJson(config);
        }

        public PacketByteBuf write() {
            var buffer = PacketByteBufs.create();
            buffer.writeString(json);
            return buffer;
        }

        public static ServerConfig read(PacketByteBuf buffer) {
            var gson = new Gson();
            var json = buffer.readString();
            var config = gson.fromJson(json, ServerConfig.class);
            return config;
        }
    }
}
