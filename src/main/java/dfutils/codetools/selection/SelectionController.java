package dfutils.codetools.selection;

import diamondcore.utils.ColorReference;
import dfutils.codesystem.objects.CodeBlockGroup;
import diamondcore.utils.BlockUtils;
import dfutils.codetools.utils.CodeBlockUtils;
import dfutils.utils.CodeFormatException;
import diamondcore.utils.GraphicsUtils;
import diamondcore.utils.chunk.ChunkCache;
import diamondcore.utils.playerdata.PlayerStateHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class SelectionController {
	
	public static boolean selectionActive = false;
	static BlockPos selectionPos;
	public static SelectionState selectionState = SelectionState.NULL;
	
	private static final Minecraft minecraft = Minecraft.getMinecraft();
	
	
	static void renderSelection(float partialTicks) {
		
		BlockPos[] selectionEdges;
		ChunkCache devSpaceCache = PlayerStateHandler.devSpaceCache;
		
		try {
			selectionEdges = getSelectionEdges();
			
		} catch (CodeFormatException exception) {
			resetSelection();
			exception.printError();
			
			return;
		}
		
		BlockPos renderPos = selectionEdges[0];
		ColorReference drawColor = ColorReference.DULL_COPY_CODE;
		
		do {
			
			if (BlockUtils.getName(renderPos, devSpaceCache).equals("minecraft:air"))
				renderPos = renderPos.south();
			
			GraphicsUtils.drawBlock(partialTicks, renderPos, drawColor);
			
			if (BlockUtils.getName(renderPos.west(), devSpaceCache).equals("minecraft:wall_sign"))
				GraphicsUtils.drawSign(partialTicks, renderPos.west(), drawColor);
			
			if (BlockUtils.getName(renderPos.up(), devSpaceCache).equals("minecraft:chest"))
				GraphicsUtils.drawChest(partialTicks, renderPos.up(), drawColor);
			
			renderPos = renderPos.south();
			
		}
		while (BlockUtils.isWithinRegion(CodeBlockUtils.getBlockCore(renderPos), selectionEdges[0], selectionEdges[1]));
	}
	
	static boolean isWithinSelection(BlockPos checkPos) throws CodeFormatException {
		if (selectionPos == null)
			return false;
		
		checkPos = CodeBlockUtils.getBlockCore(checkPos);
		BlockPos[] selectionEdges = getSelectionEdges();
		
		return BlockUtils.isWithinRegion(checkPos, selectionEdges[0], selectionEdges[1]);
	}
	
	public static void resetSelection() {
		selectionActive = false;
		selectionPos = null;
		selectionState = SelectionState.NULL;
	}
	
	public static BlockPos[] getSelectionEdges() throws CodeFormatException {
		BlockPos[] edges = new BlockPos[2];
		
		switch (selectionState) {
			case CODEBLOCK:
				minecraft.player.sendStatusMessage(new TextComponentString("§eSelection Mode: §aCode Block"), true);
				edges[0] = selectionPos;
				
				if (CodeBlockUtils.getBlockName(selectionPos).hasBrackets) {
					edges[1] = CodeBlockUtils.getOppositePiston(selectionPos.south());
				} else {
					edges[1] = selectionPos;
				}
				
				break;
			
			case LOCAL_SCOPE:
				minecraft.player.sendStatusMessage(new TextComponentString("§eSelection Mode: §aLocal Scope"), true);
				
				edges = getLocalScopeSelection();
				break;
			
			case CODE_LINE:
				minecraft.player.sendStatusMessage(new TextComponentString("§eSelection Mode: §aCode Line"), true);
				edges = getCodeLineSelection();
		}
		
		return edges;
	}
	
	private static BlockPos[] getLocalScopeSelection() throws CodeFormatException {
		BlockPos[] edges = new BlockPos[2];
		BlockPos checkPos = selectionPos;
		ChunkCache devSpaceCache = PlayerStateHandler.devSpaceCache;
		
		while (true) {
			
			checkPos = checkPos.north();
			if (CodeBlockUtils.isCodeBlock(checkPos)) {
				
				//Checks if piston is an opening piston, if so, the end of the local scope has been found.
				if (BlockUtils.getName(checkPos, devSpaceCache).equals("minecraft:piston") || BlockUtils.getName(checkPos, devSpaceCache).equals("minecraft:sticky_piston")) {
					if (BlockUtils.getFacing(checkPos, devSpaceCache).equals(EnumFacing.SOUTH))
						break;
				}
				
				checkPos = CodeBlockUtils.getBlockCore(checkPos);
				
			} else if (BlockUtils.getName(checkPos, devSpaceCache).equals("minecraft:air")) {
				break;
			} else {
				throw new CodeFormatException(checkPos);
			}
		}
		
		edges[0] = checkPos.south();
		
		//Resets the check position.
		checkPos = selectionPos;
		
		while (true) {
			
			checkPos = checkPos.south();
			if (CodeBlockUtils.isCodeBlock(checkPos)) {
				
				//Checks if piston is an opening piston, if so, jump to closing piston.
				if (BlockUtils.getName(checkPos, devSpaceCache).equals("minecraft:piston") || BlockUtils.getName(checkPos, devSpaceCache).equals("minecraft:sticky_piston")) {
					if (BlockUtils.getFacing(checkPos, devSpaceCache).equals(EnumFacing.SOUTH))
						checkPos = CodeBlockUtils.getOppositePiston(checkPos);
				}
				
				//If there is air, it means either that it is the air block before a closing piston
				//or it is the end of the code line.
			} else if (BlockUtils.getName(checkPos, devSpaceCache).equals("minecraft:air")) {
				break;
			} else {
				throw new CodeFormatException(checkPos);
			}
		}
		
		if (BlockUtils.getName(checkPos.north(), devSpaceCache).equals("minecraft:piston") || BlockUtils.getName(checkPos.north(), devSpaceCache).equals("minecraft:sticky_piston")) {
			edges[1] = checkPos.north();
		} else {
			edges[1] = CodeBlockUtils.getBlockCore(checkPos.north());
		}
		
		return edges;
	}
	
	private static BlockPos[] getCodeLineSelection() throws CodeFormatException {
		BlockPos[] edges = new BlockPos[2];
		BlockPos checkPos = selectionPos;
		ChunkCache devSpaceCache = PlayerStateHandler.devSpaceCache;
		
		while (true) {
			
			checkPos = checkPos.north();
			if (CodeBlockUtils.isCodeBlock(checkPos)) {
				
				if (CodeBlockUtils.getBlockName(CodeBlockUtils.getBlockCore(checkPos)).blockGroup.equals(CodeBlockGroup.EVENT)) {
					checkPos = CodeBlockUtils.getBlockCore(checkPos);
					break;
				}
				
				if (BlockUtils.getName(checkPos, devSpaceCache).equals("minecraft:piston") || BlockUtils.getName(checkPos, devSpaceCache).equals("minecraft:sticky_piston")) {
					if (BlockUtils.getFacing(checkPos, devSpaceCache) == EnumFacing.NORTH && CodeBlockUtils.hasOppositePiston(checkPos))
						checkPos = CodeBlockUtils.getOppositePiston(checkPos);
				}
				
			} else if (BlockUtils.getName(checkPos, devSpaceCache).equals("minecraft:air")) {
				checkPos = checkPos.south();
				break;
			} else {
				throw new CodeFormatException(checkPos);
			}
		}
		
		edges[0] = checkPos;
		
		//Resets the check position.
		checkPos = selectionPos;
		
		while (true) {
			
			checkPos = checkPos.south();
			if (!CodeBlockUtils.isCodeBlock(checkPos)) {
				
				if (BlockUtils.getName(checkPos, devSpaceCache).equals("minecraft:air")) {
					if (!(BlockUtils.getName(checkPos.south(), devSpaceCache).equals("minecraft:piston") || BlockUtils.getName(checkPos.south(), devSpaceCache).equals("minecraft:sticky_piston")))
						break;
				} else {
					throw new CodeFormatException(checkPos);
				}
			}
		}
		
		if (BlockUtils.getName(checkPos.north(), devSpaceCache).equals("minecraft:piston") || BlockUtils.getName(checkPos.north(), devSpaceCache).equals("minecraft:sticky_piston")) {
			edges[1] = checkPos.north();
		} else {
			edges[1] = CodeBlockUtils.getBlockCore(checkPos.north());
		}
		
		return edges;
	}
}
