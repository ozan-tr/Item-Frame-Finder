package ozang.itemframefinder;

import net.minecraft.entity.EntityType;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ItemFinder {

   	public static HashSet<Item> searchItems = new HashSet<Item>();


    static ClientWorld clientWorld = MinecraftClient.getInstance().world;

    public static void FindMatchingFrames(PlayerEntity player) {
        ItemFrameFinderConfig config = AutoConfig.getConfigHolder(ItemFrameFinderConfig.class).getConfig();

        List<ItemFrameEntity> itemFrames = clientWorld.getEntitiesByType(EntityType.ITEM_FRAME, player.getBoundingBox().expand(config.searchDistance, 50.0D, config.searchDistance), EntityPredicates.VALID_ENTITY);

        if(config.frameType == ItemFrameFinderConfig.FRAME_TYPES.GLOW){
            itemFrames.clear();
            List<GlowItemFrameEntity> glowItemFrames = clientWorld.getEntitiesByType(EntityType.GLOW_ITEM_FRAME, player.getBoundingBox().expand(config.searchDistance, 50.0D, config.searchDistance), EntityPredicates.VALID_ENTITY);
            itemFrames.addAll(glowItemFrames);
        }else if(config.frameType == ItemFrameFinderConfig.FRAME_TYPES.BOTH){
            itemFrames.addAll(clientWorld.getEntitiesByType(EntityType.GLOW_ITEM_FRAME, player.getBoundingBox().expand(config.searchDistance, 50.0D, config.searchDistance), EntityPredicates.VALID_ENTITY));
        }

        int foundFrameCount = 0;

        for (Entity entity : itemFrames) {
            ItemFrameEntity itemFrame = (ItemFrameEntity) entity;
            ItemStack itemStack = itemFrame.getHeldItemStack();
            if(searchItems.contains(itemStack.getItem())){
                foundFrameCount++;
                if(config.renderMode == ItemFrameFinderConfig.RENDER_MODE.EDGES){
                    drawEdges(itemFrame.getBlockPos().offset(itemFrame.getHorizontalFacing().getOpposite()),config);
                }else{
                    drawFaces(itemFrame.getBlockPos().offset(itemFrame.getHorizontalFacing().getOpposite()),config);       
                }             
            }
        }

        if (foundFrameCount >= searchItems.size()) {
            player.sendMessage(Text.of("Found all items"), true);
        }else{
            player.sendMessage(Text.of("Found " + foundFrameCount + " out of " + searchItems.size()), true);
        }
        
    }

    public static void drawEdges(BlockPos pos,ItemFrameFinderConfig config) {

        float x = pos.getX();
        float y = pos.getY() + config.yOffset;
        float z = pos.getZ();
        float dx = x + config.Width;
        float dy = y + config.Height;
        float dz = z + config.Width;

        for (float i = x; i < dx; i += 0.1) {
            clientWorld.addParticle(ParticleTypes.END_ROD, i, y, z, 0.0D, 0.0D, 0.0D);
            clientWorld.addParticle(ParticleTypes.END_ROD, i, y, dz, 0.0D, 0.0D, 0.0D);

            clientWorld.addParticle(ParticleTypes.END_ROD, i, dy, z, 0.0D, 0.0D, 0.0D);
            clientWorld.addParticle(ParticleTypes.END_ROD, i, dy, dz, 0.0D, 0.0D, 0.0D);
        }

        for (float i = y; i < dy; i += 0.1) {
            clientWorld.addParticle(ParticleTypes.END_ROD, x, i, z, 0.0D, 0.0D, 0.0D);
            clientWorld.addParticle(ParticleTypes.END_ROD, x, i, dz, 0.0D, 0.0D, 0.0D);

            clientWorld.addParticle(ParticleTypes.END_ROD, dx, i, z, 0.0D, 0.0D, 0.0D);
            clientWorld.addParticle(ParticleTypes.END_ROD, dx, i, dz, 0.0D, 0.0D, 0.0D);
        }

        for (float i = z; i < dz; i += 0.1) {
            clientWorld.addParticle(ParticleTypes.END_ROD, x, y, i, 0.0D, 0.0D, 0.0D);
            clientWorld.addParticle(ParticleTypes.END_ROD, x, dy, i, 0.0D, 0.0D, 0.0D);

            clientWorld.addParticle(ParticleTypes.END_ROD, dx, y, i, 0.0D, 0.0D, 0.0D);
            clientWorld.addParticle(ParticleTypes.END_ROD, dx, dy, i, 0.0D, 0.0D, 0.0D);
        }
    }

    public static void drawFaces(BlockPos pos,ItemFrameFinderConfig config){
        float x = pos.getX();
        float y = pos.getY() + config.yOffset;
        float z = pos.getZ();
        float dx = x + config.Width;
        float dy = y + config.Height;
        float dz = z + config.Width;

        for (float i = x; i < dx; i += 0.1) {
            for (float j = z; j < dz; j += 0.1) {
                clientWorld.addParticle(ParticleTypes.END_ROD, i, y, j, 0.0D, 0.0D, 0.0D);
                clientWorld.addParticle(ParticleTypes.END_ROD, i, dy, j, 0.0D, 0.0D, 0.0D);
            }
        }

        for (float i = y; i < dy; i += 0.1) {
            for (float j = z; j < dz; j += 0.1) {
                clientWorld.addParticle(ParticleTypes.END_ROD, x, i, j, 0.0D, 0.0D, 0.0D);
                clientWorld.addParticle(ParticleTypes.END_ROD, dx, i, j, 0.0D, 0.0D, 0.0D);
            }
        }

        for (float i = x; i < dx; i += 0.1) {
            for (float j = y; j < dy; j += 0.1) {
                clientWorld.addParticle(ParticleTypes.END_ROD, i, j, z, 0.0D, 0.0D, 0.0D);
                clientWorld.addParticle(ParticleTypes.END_ROD, i, j, dz, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
