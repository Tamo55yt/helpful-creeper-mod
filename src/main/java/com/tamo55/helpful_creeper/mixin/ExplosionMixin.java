package com.tamo55.helpful_creeper.mixin;

import com.tamo55.helpful_creeper.ExplosionManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(CreeperEntity.class)
public abstract class ExplosionMixin {

    @Inject(method = "explode", at = @At("HEAD"))
    private void onExplode(CallbackInfo ci) {
        CreeperEntity creeper = (CreeperEntity)(Object)this;
        net.minecraft.world.World world = creeper.getEntityWorld();
        
        if (!world.isClient()) {
            // Patlama alanındaki blokları patlama gerçekleşmeden önce yakala
            Map<BlockPos, BlockState> blocks = new HashMap<>();
            int radius = 3; // Creeper patlama yarıçapı
            BlockPos center = creeper.getBlockPos();
            
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = center.add(x, y, z);
                        blocks.put(pos, world.getBlockState(pos));
                    }
                }
            }
            
            // Veriyi kaydet ve ID al
            UUID explosionId = ExplosionManager.registerExplosion(blocks);
            
            // Oyuncuyu bilgilendir
            ExplosionManager.notifyPlayerOfExplosion(creeper, explosionId);
        }
    }
}
