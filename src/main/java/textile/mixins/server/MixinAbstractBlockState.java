package textile.mixins.server;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import textile.data.TextileUpdates;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class MixinAbstractBlockState {
	
	@Inject(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V",
		at = @At(value = "HEAD")
	)
	private void onUpdateNeighbors(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo ci) {
		//TextileUpdates.addBlockUpdate(new UpdateData(pos));
		TextileUpdates.addBlockUpdate(pos, pos);
	}
	
	@Inject(method = "neighborUpdate(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;Z)V",
		at = @At(value = "HEAD")
	)
	private void onNeighborUpdate(World world, BlockPos pos, Block block, BlockPos posFrom, boolean notify, CallbackInfo ci) {
		//TextileUpdates.addBlockUpdate(new UpdateData(pos));
		TextileUpdates.addBlockUpdate(posFrom, pos);
	}
}