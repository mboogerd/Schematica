package beyonix.schematica;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PartialPlan extends Item {

	public PartialPlan(int id) {
		super(id);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("partialPlan");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		player.addChatMessage("isRemote: " + world.isRemote);	
		return super.onItemRightClick(itemstack, world, player);
	}	
	
	public boolean equals(Object obj) {
		if (obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			return stack.itemID == this.itemID;
			
		} else if (obj instanceof Item) {
			Item item = (Item)obj;
			return item.itemID == this.itemID;			
		}
		
		return false;
	}
}
