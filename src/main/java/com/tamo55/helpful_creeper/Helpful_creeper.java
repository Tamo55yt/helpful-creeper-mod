package com.tamo55.helpful_creeper;

import com.tamo55.helpful_creeper.networking.ExplosionOccurredPayload;
import com.tamo55.helpful_creeper.networking.UndoExplosionPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.world.ServerWorld;

public class Helpful_creeper implements ModInitializer {

    @Override
    public void onInitialize() {
        // Payload Kaydı
        PayloadTypeRegistry.playC2S().register(UndoExplosionPayload.ID, UndoExplosionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ExplosionOccurredPayload.ID, ExplosionOccurredPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(com.tamo55.helpful_creeper.networking.CreeperExplosionPayload.ID, com.tamo55.helpful_creeper.networking.CreeperExplosionPayload.CODEC);

        // Sunucu Tarafı Dinleyici: Oyuncunun GUI seçimini karşıla
        ServerPlayNetworking.registerGlobalReceiver(UndoExplosionPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerWorld world = context.player().getCommandSource().getWorld();
                if (payload.undo()) {
                    ExplosionManager.undoExplosion(world, payload.explosionId());
                } else {
                    ExplosionManager.discardExplosion(payload.explosionId());
                }
            });
        });
    }
}
