package beyonix.schematica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lunatrius.schematica.SchematicWorld;
import lunatrius.schematica.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.common.ICraftingHandler;

public class FullPlanCraftingHandler implements ICraftingHandler {

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {
		Settings.logger.logInfo( "Workbench thread: '" + Thread.currentThread().getName() + "' - " + Thread.currentThread().getId() + " - Remote: " + player.worldObj.isRemote );
		player.addChatMessage( Arrays.toString( Thread.currentThread().getStackTrace() ) );
		if (player.worldObj.isRemote)
			return;
		
		//We're only interested in the crafting of full plans
		if (item.itemID == WorldRecipe.fullPlan.itemID ) {

			//This code is based on the assumption that the crafting recipe requires two PartialPlans, and that the metadata is set!
			Vector3f corner1 = null;
			Vector3f corner2 = null;
			NBTTagCompound partialMetadata = null;
			NBTTagCompound fullMetadata = new NBTTagCompound();
			
			for( int i = 0; i < craftMatrix.getSizeInventory(); i++ ) {
				ItemStack stack = craftMatrix.getStackInSlot(i);
				
				if ( stack != null && stack.itemID == WorldRecipe.partialPlan.itemID) {
					partialMetadata = stack.getTagCompound();
					
					if (partialMetadata == null) {
						item.stackSize = 0;
						player.addChatMessage("Full plan construction failed");
						return;
					}
					
					if (corner1 == null)
						corner1 = new Vector3f(partialMetadata.getLong("x"), partialMetadata.getLong("y"), partialMetadata.getLong("z"));
					else
						corner2 = new Vector3f(partialMetadata.getLong("x"), partialMetadata.getLong("y"), partialMetadata.getLong("z"));
				}
			}
			
			//Safety
			if (corner1 == null || corner2 == null) {
				item.stackSize = 0;
				player.addChatMessage("Full plan construction failed");
				return;
			}
			
			/* Possibly deprecated code */
			/*
			//Acquire the center point
			int x = Math.round( (corner1.x + corner2.x)/2 );
			int y = Math.round( (corner1.y + corner2.y)/2 );
			int z = Math.round( (corner1.z + corner2.z)/2 );
			String filename = x + "_" + y + "_" + z + ".schematic";
			
			//Save the schematic on disk
			Settings s = Settings.instance();
			s.saveSchematic(s.schematicDirectory, filename, corner1, corner2);
			
			//Save the filename in the item
			fullMetadata.setString("filename", filename);
			*/
			NBTTagCompound itemMetadata = item.getTagCompound();
			
			if (itemMetadata == null) {
				//If per chance the item didn't have a compound (not sure if possible), add it.
				itemMetadata = new NBTTagCompound();
				item.setTagCompound(itemMetadata);
			}
			NBTTagCompound schematic = getNBTfromBoundingBox( player.worldObj, corner1, corner2 );			
			itemMetadata.setCompoundTag(
					"schematic"
					, schematic);
			
			player.addChatMessage("SCHEMATICA: Stored " + schematic.getTags().size() + " tags in the blueprint. Full schematic metadata is " + itemMetadata.getTags().size() + " tags");
		}
	}

	/* Not implemented */
	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {}

	
	/**
	 * This code is adapted from lunatrius' Settings.saveSchematic.
	 * @author lunatrius
	 * @param from	one corner of the bounding box
	 * @param to	the opposite corner of the bounding box
	 * @return	NBTTagCompound filled with the data within the bounding box
	 */
	private NBTTagCompound getNBTfromBoundingBox( World world, Vector3f from, Vector3f to ) {
		NBTTagCompound tagCompound = new NBTTagCompound("Schematic");
		
		int minX = (int) Math.min(from.x, to.x);
		int maxX = (int) Math.max(from.x, to.x);
		int minY = (int) Math.min(from.y, to.y);
		int maxY = (int) Math.max(from.y, to.y);
		int minZ = (int) Math.min(from.z, to.z);
		int maxZ = (int) Math.max(from.z, to.z);
		short width = (short) (Math.abs(maxX - minX) + 1);
		short height = (short) (Math.abs(maxY - minY) + 1);
		short length = (short) (Math.abs(maxZ - minZ) + 1);

		int[][][] blocks = new int[width][height][length];
		int[][][] metadata = new int[width][height][length];
		List<TileEntity> tileEntities = new ArrayList<TileEntity>();
		TileEntity tileEntity = null;
		NBTTagCompound tileEntityNBT = null;

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					blocks[x - minX][y - minY][z - minZ] = world.getBlockId(x, y, z);
					metadata[x - minX][y - minY][z - minZ] = world.getBlockMetadata(x, y, z);
					tileEntity = world.getBlockTileEntity(x, y, z);
					if (tileEntity != null) {
						tileEntityNBT = new NBTTagCompound();
						tileEntity.writeToNBT(tileEntityNBT);

						tileEntity = TileEntity.createAndLoadEntity(tileEntityNBT);
						tileEntity.xCoord -= minX;
						tileEntity.yCoord -= minY;
						tileEntity.zCoord -= minZ;
						tileEntities.add(tileEntity);
					}
				}
			}
		}

		String icon = Integer.toString(Settings.defaultIcon.copy().itemID);
		SchematicWorld schematicOut = new SchematicWorld(icon, blocks, metadata, tileEntities, width, height, length);
		schematicOut.writeToNBT(tagCompound);
		
		return tagCompound;
	}
}
