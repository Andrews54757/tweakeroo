package fi.dy.masa.tweakeroo.mixin;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fi.dy.masa.tweakeroo.tweaks.RenderTweaks;
import net.minecraft.util.math.BlockPos;
import net.optifine.BlockPosM;

@Mixin(targets = "net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk$RebuildTask")
public abstract class MixinChunkBuilder_rebuildTask_optifine
{

    @Redirect(method = "Lnet/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk$RebuildTask;render(FFFLnet/minecraft/client/render/chunk/ChunkBuilder$ChunkData;Lnet/minecraft/client/render/chunk/BlockBufferBuilderStorage;)Ljava/util/Set;", at = @At(value = "INVOKE",
    target = "Lnet/optifine/BlockPosM;getAllInBoxMutable(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Iterable;", ordinal = 0))
    private Iterable<BlockPosM> iterateProxy(BlockPos start, BlockPos end) {
         Iterator<BlockPos> iterator = BlockPos.iterate(start, end).iterator();
         BlockPosM blockPosM = new BlockPosM(0,0,0);
         return () -> {
            return new AbstractIterator<BlockPosM>() {
               protected BlockPosM computeNext() {
                  if (!iterator.hasNext()) {
                     return (BlockPosM) this.endOfData();
                  } else {
                     BlockPos pos = iterator.next();
                     while (!RenderTweaks.isPositionValidForRendering(pos)) {
                        if (!iterator.hasNext())
                           return (BlockPosM) this.endOfData();
                        pos = iterator.next();
                     }
                     
                     blockPosM.setXyz(pos.getX(), pos.getY(), pos.getZ());
                     return blockPosM;
                  }
               }
            };
         };   
    }
}
