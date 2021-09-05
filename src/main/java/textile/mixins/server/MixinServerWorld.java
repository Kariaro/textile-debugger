package textile.mixins.server;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import textile.commands.BlockEventCommand;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends World {
	
	@Shadow @Final private ObjectLinkedOpenHashSet<BlockEvent> syncedBlockEventQueue;
	@Shadow @Final private MinecraftServer server;
	
	protected MixinServerWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
		super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
	}
	
	/**
	 * @author HardCoded
	 * @reason stupid code
	 */
	@Overwrite
	public void processSyncedBlockEvents() {
		while(!this.syncedBlockEventQueue.isEmpty()) {
			BlockEvent blockEvent = (BlockEvent)this.syncedBlockEventQueue.removeFirst();
			if (this.processBlockEvent(blockEvent)) {
				this.server.getPlayerManager().sendToAround((PlayerEntity)null, blockEvent.getPos().getX(), blockEvent.getPos().getY(), blockEvent.getPos().getZ(), 64.0D, this.getRegistryKey(), new BlockEventS2CPacket(blockEvent.getPos(), blockEvent.getBlock(), blockEvent.getType(), blockEvent.getData()));
			}
			
			if(BlockEventCommand.HAS_FROZEN_EVENTS) {
				Thread.dumpStack();
				break;
			}
		}
	}
	
	@Inject(method = "tick", //(Ljava/util/function/BooleanSupplier)V",
		at = @At(value = "HEAD"),
		cancellable = true
	)
	public void tick(BooleanSupplier keepTicking, CallbackInfo ci) {
		if(BlockEventCommand.HAS_FROZEN_EVENTS) {
			if(!syncedBlockEventQueue.isEmpty()) {
				for(int i = 0; i < BlockEventCommand.DO_TICKS; i++) {
					processSyncedBlockEvents();
				}
				
				BlockEventCommand.DO_TICKS = 0;
			}
			ci.cancel();
		}
	}
	
	@Shadow
	private boolean processBlockEvent(BlockEvent event) {
		return false;
	}
}
