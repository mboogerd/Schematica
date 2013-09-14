package beyonix.schematica;

import lunatrius.schematica.Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.common.ICraftingHandler;

public class FullPlanCraftingHandler implements ICraftingHandler {

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {
		
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
			item.setTagCompound(fullMetadata);
		}
	}

	/* Not implemented */
	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {}

}
