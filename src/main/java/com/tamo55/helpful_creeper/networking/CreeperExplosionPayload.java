package com.tamo55.helpful_creeper.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record CreeperExplosionPayload(UUID explosionId) implements CustomPayload {
    public static final CustomPayload.Id<CreeperExplosionPayload> ID = new CustomPayload.Id<>(Identifier.of("helpful_creeper", "creeper_explosion"));
    
    public static final PacketCodec<RegistryByteBuf, CreeperExplosionPayload> CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, CreeperExplosionPayload::explosionId,
        CreeperExplosionPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
