package beyonix.schematica;

import cpw.mods.fml.common.ICraftingHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FullPlan extends Item {

	public FullPlan(int id) {
		super(id);
		setMaxStackSize(1); //Not stackable so we can use the NBT data on ItemStack, as well as not having to deal with the issue of only stacking equivalent plans 
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("fullPlan");
	}


}
