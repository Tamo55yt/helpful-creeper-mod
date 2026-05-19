package com.tamo55.helpful_creeper;

import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleTypes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.Direction;

public class ExplosionManager {
    private static final Map<UUID, Map<BlockPos, BlockState>> pendingExplosions = new ConcurrentHashMap<>();
    private static final Set<UUID> processedCreepers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static boolean isProcessed(UUID uuid) {
        return processedCreepers.contains(uuid);
    }

    public static void handlePendingCreeperExplosion(CreeperEntity creeper) {
        if (!processedCreepers.add(creeper.getUuid())) {
            return; // Zaten işlenmiş
        }

        UUID explosionId = UUID.randomUUID();
        
        // Etraftaki oyunculara paket gönder
        ServerPlayerEntity player = (ServerPlayerEntity) creeper.getEntityWorld().getClosestPlayer(creeper, 20);
        if (player != null) {
            System.out.println("Sending explosion prompt to player: " + player.getName().getString());
            ServerPlayNetworking.send(
                player,
                new com.tamo55.helpful_creeper.networking.CreeperExplosionPayload(explosionId)
            );
        } else {
            System.out.println("No player found nearby to send prompt.");
        }
    }

    public static void notifyPlayerOfExplosion(CreeperEntity creeper, UUID explosionId) {
        // Etraftaki oyunculara paket gönder
        ServerPlayerEntity player = (ServerPlayerEntity) creeper.getEntityWorld().getClosestPlayer(creeper, 20);
        if (player != null) {
            ServerPlayNetworking.send(
                player,
                new com.tamo55.helpful_creeper.networking.CreeperExplosionPayload(explosionId)
            );
        }
    }

    public static UUID registerExplosion(Map<BlockPos, BlockState> blocks) {
        UUID id = UUID.randomUUID();
        pendingExplosions.put(id, new HashMap<>(blocks));
        
        // 30 saniye sonra otomatik temizle
        new Thread(() -> {
            try {
                Thread.sleep(30000);
                pendingExplosions.remove(id);
            } catch (InterruptedException e) {
                // Silinmiş olabilir
            }
        }).start();
        
        return id;
    }

    public static void undoExplosion(ServerWorld world, UUID id) {
        Map<BlockPos, BlockState> blocks = pendingExplosions.remove(id);
        if (blocks != null && !blocks.isEmpty()) {
            List<BlockPos> sortedPositions = new ArrayList<>(blocks.keySet());
            sortedPositions.sort(Comparator.comparingInt(BlockPos::getY));
            BlockPos center = sortedPositions.get(sortedPositions.size() / 2);

            new Thread(() -> {
                for (BlockPos pos : sortedPositions) {
                    BlockState state = blocks.get(pos);
                    world.getServer().execute(() -> {
                        world.setBlockState(pos, state);
                        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, 
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
                            5, 0.2, 0.2, 0.2, 0.05);
                    });

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                
                world.getServer().execute(() -> {
                    world.playSound(null, center, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1.0f, 1.2f);
                    
                    // En üstteki katı bloğu bul
                    int highestY = world.getTopY(Heightmap.Type.WORLD_SURFACE, center.getX(), center.getZ());
                    BlockPos.Mutable mutablePos = new BlockPos.Mutable(center.getX(), highestY, center.getZ());
                    
                    while (mutablePos.getY() > world.getBottomY() && !world.getBlockState(mutablePos).isSolidBlock(world, mutablePos)) {
                        mutablePos.move(Direction.DOWN);
                    }
                    
                    // Katı bloğun üzerine yerleştir
                    BlockPos signPos = mutablePos.up();
                    world.setBlockState(signPos, Blocks.OAK_SIGN.getDefaultState());
                    SignBlockEntity signEntity = (SignBlockEntity) world.getBlockEntity(signPos);
                    
                    if (signEntity != null) {
                        signEntity.setText(new net.minecraft.block.entity.SignText(
                            new Text[]{Text.literal("Sorry!!"), Text.empty(), Text.empty(), Text.literal("-Creeper")},
                            new Text[]{Text.empty(), Text.empty(), Text.empty(), Text.empty()},
                            DyeColor.BLACK, false
                        ), false);
                    }
                });
            }).start();
        }
    }

    public static void discardExplosion(UUID id) {
        pendingExplosions.remove(id);
    }
}
