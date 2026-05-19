package com.tamo55.helpful_creeper.client;

import com.tamo55.helpful_creeper.networking.UndoExplosionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UndoExplosionScreen extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger("HelpfulCreeperGUI");
    private final UUID explosionId;
    private int timer = 15 * 20; // 15 saniye (20 tick/sn)

    public UndoExplosionScreen(UUID explosionId) {
        super(Text.translatable("gui.helpful_creeper.title"));
        this.explosionId = explosionId;
        LOGGER.info("UndoExplosionScreen initialized for explosion: {}", explosionId);
    }

    @Override
    protected void init() {
        LOGGER.info("Initializing buttons for UndoExplosionScreen");
        int buttonWidth = 120;
        int buttonHeight = 20;
        int spacing = 10;

        // Başlık Butonu (Tıklanamaz, metin göstermek için)
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.helpful_creeper.question"), button -> {})
                .dimensions(this.width / 2 - 150, this.height / 2 - 50, 300, 20)
                .build());

        // Evet Butonu
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.helpful_creeper.yes"), button -> {
            LOGGER.info("Yes button clicked");
            ClientPlayNetworking.send(new UndoExplosionPayload(explosionId, true));
            this.close();
        }).dimensions(this.width / 2 - buttonWidth - spacing / 2, this.height / 2, buttonWidth, buttonHeight).build());

        // Hayır Butonu
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.helpful_creeper.no"), button -> {
            LOGGER.info("No button clicked");
            ClientPlayNetworking.send(new UndoExplosionPayload(explosionId, false));
            this.close();
        }).dimensions(this.width / 2 + spacing / 2, this.height / 2, buttonWidth, buttonHeight).build());
    }

    @Override
    public void tick() {
        if (timer > 0) {
            timer--;
        } else {
            LOGGER.info("Timer expired, closing screen");
            ClientPlayNetworking.send(new UndoExplosionPayload(explosionId, false));
            this.close();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Arka planı manuel gradyan ile çiz
        context.fillGradient(0, 0, this.width, this.height, 0xAAFF0000, 0xAA000000);
        
        // Butonları çiz
        super.render(context, mouseX, mouseY, delta);
        
        // Zamanlayıcıyı basit bir şekilde çiz
        String timerText = String.format("%.1f s", timer / 20.0);
        context.drawText(this.textRenderer, timerText, 10, 10, 0xFFFFFF, true);
    }

    @Override
    public boolean shouldPause() {
        return false; // Oyun arkada durmasın (Creeperlar gelmeye devam eder!)
    }
}
