package textile.data;

import net.minecraft.util.math.BlockPos;
import textile.utils.DirectList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container class for block updates
 */
public class BlockUpdateContainer {
	// The position of the updated block
	private final BlockPos pos;
	
	// The list of blocks
	private final DirectList<BlockUpdateObject> list;
	
	public BlockUpdateContainer(BlockPos pos) {
		this.pos = pos;
		this.list = new DirectList<>();
	}
	
	public void addUpdate(long worldTick, BlockPos from, BlockPos target) {
		list.add(new BlockUpdateObject(worldTick, from, target));
	}
	
	public int countUpdates() {
		return list.size();
	}
	
	public BlockPos getPosition() {
		return pos;
	}
	
	public Iterator<BlockUpdateObject> getIterator() {
		return new Iterator<BlockUpdateObject>() {
			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < list.size();
			}
			
			@Override
			public BlockUpdateObject next() {
				return list.get(index++);
			}
		};
	}
}
