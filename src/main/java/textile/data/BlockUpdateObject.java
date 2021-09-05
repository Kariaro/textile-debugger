package textile.data;

import net.minecraft.util.math.BlockPos;

import java.util.HashSet;

/**
 * This object contains the data of a block that got updated
 */
public class BlockUpdateObject {
	private static final Object[] EMPTY_ARRAY = new Object[0];
	
	// The stack trace of this update
	private final StackTraceElement[] stackTrace;
	
	// The tick when this update was created
	private final long worldTick;
	
	// The block that created this update
	private final BlockPos from;
	
	// The target block
	private final BlockPos target;
	
	protected BlockUpdateObject(long worldTick, BlockPos from, BlockPos target) {
		if(BlockUpdateSettings.shouldDebugStackTrace()) {
			stackTrace = Thread.currentThread().getStackTrace();
		} else {
			stackTrace = (StackTraceElement[])EMPTY_ARRAY;
		}
		
		this.worldTick = worldTick;
		this.from = from;
		this.target = target;
	}
	
	public BlockPos getPosition() {
		return target;
	}
	
	public BlockPos getSource() {
		return from;
	}
	
	public long getWorldTick() {
		return worldTick;
	}
	
	private String _cacheTrace;
	private String getCacheTrace() {
		if(stackTrace == null || stackTrace.length == 0) return target.toShortString();
		
		String network = "";
		{
			for(int i = 0; i < stackTrace.length; i++) {
				StackTraceElement elm = stackTrace[i];
				
				String className = elm.getClassName();
				
				if(className.contains("net.minecraft.server.network")) {
					network = className;
					break;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 3; i < Math.min(16, stackTrace.length); i++) {
			StackTraceElement elm = stackTrace[i];
			sb.append(elm.toString()).append("\\n");
		}
		
		if(!network.isEmpty()) {
			sb.append("\\n").append("Network:\\n")
				.append(network);
		}
		
		return sb.toString();
	}

	public String getTrace() {
		if(_cacheTrace == null) {
			_cacheTrace = getCacheTrace();
		}
		
		return _cacheTrace;
	}
}
