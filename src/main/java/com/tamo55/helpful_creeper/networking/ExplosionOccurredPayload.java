package com.tamo55.helpful_creeper.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record ExplosionOccurredPayload(UUID explosionId) implements CustomPayload {
    public static final Id<ExplosionOccurredPayload> ID = new Id<>(Identifier.of("helpful_creeper", "explosion_occurred"));
    
    public static final PacketCodec<RegistryByteBuf, ExplosionOccurredPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, ExplosionOccurredPayload::explosionId,
            ExplosionOccurredPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
