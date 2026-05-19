package com.tamo55.helpful_creeper.client;

import com.tamo55.helpful_creeper.networking.ExplosionOccurredPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helpful_creeperClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("HelpfulCreeperClient");

    @Override
    public void onInitializeClient() {
        // İstemci Tarafı Dinleyici: Sunucudan gelen GUI açma komutunu karşıla
        ClientPlayNetworking.registerGlobalReceiver(ExplosionOccurredPayload.ID, (payload, context) -> {
            LOGGER.info("Received ExplosionOccurredPayload for: {}", payload.explosionId());
            context.client().execute(() -> {
                LOGGER.info("Setting screen to UndoExplosionScreen");
                context.client().setScreen(new UndoExplosionScreen(payload.explosionId()));
            });
        });

        // Yeni creeper patlama uyarısı için dinleyici
        ClientPlayNetworking.registerGlobalReceiver(com.tamo55.helpful_creeper.networking.CreeperExplosionPayload.ID, (payload, context) -> {
            LOGGER.info("Received CreeperExplosionPayload for: {}", payload.explosionId());
            context.client().execute(() -> {
                LOGGER.info("Setting screen to UndoExplosionScreen");
                context.client().setScreen(new UndoExplosionScreen(payload.explosionId()));
            });
        });
    }
}

