package textile.data;

import carpet.helpers.TickSpeed;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.BlockPos;
import org.w3c.dom.Node;
import textile.utils.CachedHashMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TextileUpdates {
	private static CachedHashMap<BlockPos, BlockUpdateContainer> update_map = new CachedHashMap<>();
	private static long last_tick;
	
	public static void addBlockUpdate(BlockPos from, BlockPos target) {
		IntegratedServer server = MinecraftClient.getInstance().getServer();
		if(server.isOnThread()) {
			update_map.computeIfAbsent(target, BlockUpdateContainer::new)
				.addUpdate(_tick, from, target);
		} else {
			System.out.println("Tried to add block update from non server thread: " + Thread.currentThread());
		}
	}
	
	public static Map<BlockPos, BlockUpdateContainer> getContainers() {
		return update_map.getCache();
		
		NeighborUpdateDebugRenderer
	}
	
	private static long _tick = 1;
	
	public static void preTick() {
		if(TickSpeed.isPaused()) {
			if(TickSpeed.player_active_timeout > TickSpeed.PLAYER_GRACE) {
				_tick++;
			}
		} else {
			_tick++;
		}
		
		if(last_tick != _tick) {
			last_tick = _tick;
			update_map.clear();
		}
	}
	
	public static void postTick() {
		update_map.updateCache();
	}
}
