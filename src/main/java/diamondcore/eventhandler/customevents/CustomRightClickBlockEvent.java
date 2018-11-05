package diamondcore.eventhandler.customevents;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class CustomRightClickBlockEvent extends Event {
	
	public final EnumFacing blockFace;
	public final BlockPos blockPos;
	
	public CustomRightClickBlockEvent(EnumFacing blockFace, BlockPos blockPos) {
		this.blockFace = blockFace;
		this.blockPos = blockPos;
	}
}
