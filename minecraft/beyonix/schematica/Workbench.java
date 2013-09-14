package beyonix.schematica;

import java.io.File;
import java.io.IOException;

import lunatrius.schematica.Settings;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.lwjgl.util.vector.Vector3f;

public class Workbench extends net.minecraft.block.Block {

	public Workbench(int id, Material material) {
		super(id, material);
		IconRegister t;
		
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		
		Settings.logger.logInfo( "Workbench thread: '" + Thread.currentThread().getName() + "' - " + Thread.currentThread().getId() + " - Remote: " + world.isRemote );
		
		if ( world.isRemote )
			return true;
		
		ItemStack usedItem = player.getCurrentEquippedItem();
    	InventoryPlayer inventory = player.inventory;
    	((EntityPlayerMP)player).sendPlayerAbilities();
    	if ( usedItem != null) {
    		
    		if ( usedItem.itemID == Item.emptyMap.itemID ) {
    			int currentItem = inventory.currentItem;
			
				if (usedItem.stackSize > 1) {
					//player.addChatMessage("Activated workbench@{x:"+x+", y:"+y+", z:"+z+"} with a stack of empty maps. Decrementing stacksize and add a new partial plan");
					//Subtract one from the stacksize
					inventory.decrStackSize(currentItem, 1);
					
					//and add a partial plan to the inventory in a free slot
					if ( !inventory.addItemStackToInventory(getPartialPlan(x,y,z)) ) {
						//FIXME: Make sure the item drops in the world if it cannot be placed in the inventory
						
						//Code stolen from BlockCauldron ( doesn't work... )
						//world.spawnEntityInWorld(new EntityItem(world, (double)x + 0.5D, (double)y + 1.5D, (double)z + 0.5D, getPartialPlan(x,y,z)));
						//Code stolen from SlotCrafting ( doesn't work :( )
						//player.dropPlayerItem(getPartialPlan(x,y,z));
					} else if (player instanceof EntityPlayerMP) {
						((EntityPlayerMP)player).sendContainerToPlayer( player.inventoryContainer );
					}
				} else {
					//Replace the inventory slot with a partialPlanStack
					//player.addChatMessage("Activated workbench@{x:"+x+", y:"+y+", z:"+z+"} with an empty map. Substituting it for a partial plan");
					inventory.setInventorySlotContents(currentItem, getPartialPlan(x,y,z));
				}
    		} else if (usedItem.itemID == WorldRecipe.fullPlan.itemID) {
    			NBTTagCompound metadata = usedItem.getTagCompound();
    			String filename = metadata.getString("filename");
    			player.addChatMessage("Loading projection with centerpoint: " + filename);
    			
    			Settings s = Settings.instance();
    			try {
	    			s.loadSchematic((new File(s.schematicDirectory, filename)).getCanonicalPath());
	    			s.moveHere(new Vector3f(x,y,z));
    			} catch (IOException e) {
    				player.addChatMessage("Loading projection failed due to: '" + e.getLocalizedMessage() + "'");
    			}
    		}
    	}
    	
        return true;
	}
	
	private ItemStack getPartialPlan( long x, long y, long z ) {
		ItemStack stack = WorldRecipe.partialPlanStack.copy();
		NBTTagCompound metadata = new NBTTagCompound();
		metadata.setLong("x", x);
		metadata.setLong("y", y);
		metadata.setLong("z", z);
		stack.setTagCompound(metadata);
		
		return stack;
	}

}
