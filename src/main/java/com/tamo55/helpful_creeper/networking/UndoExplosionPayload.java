package com.tamo55.helpful_creeper.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record UndoExplosionPayload(UUID explosionId, boolean undo) implements CustomPayload {
    public static final Id<UndoExplosionPayload> ID = new Id<>(Identifier.of("helpful_creeper", "undo_explosion"));
    
    public static final PacketCodec<RegistryByteBuf, UndoExplosionPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, UndoExplosionPayload::explosionId,
            PacketCodecs.BOOLEAN, UndoExplosionPayload::undo,
            UndoExplosionPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
