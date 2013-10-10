package beyonix.schematica;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class WorldRecipe {

	/* All blocks used in this mod */
	public final static Block workbench = new Workbench(500, Material.ground).setHardness(0.5F).setStepSound(Block.soundGravelFootstep).setUnlocalizedName("workbench").setCreativeTab(CreativeTabs.tabDecorations);
	
	/* All items owned by this mod */
	public final static Item partialPlan = new PartialPlan(5000);
	public final static ItemStack partialPlanStack = new ItemStack( new PartialPlan(5000) );
	public final static Item fullPlan = new FullPlan(5001);
	public final static ItemStack fullPlanStack = new ItemStack( new FullPlan(5001 ) );
	
	/* All item(stack)s used/referenced by this mod */
	private final static Item emptyMap = Item.emptyMap;
	private final static ItemStack emptyMapStack = new ItemStack( emptyMap );
	
	private final static Item enderEye = Item.eyeOfEnder;
	private final static ItemStack enderEyeStack = new ItemStack( enderEye );
	
	/* Crafting handler */
	private final static ICraftingHandler fullPlanCraftHandler = new FullPlanCraftingHandler();
	
	public void init() {
		//Register the workbench
		GameRegistry.registerBlock(workbench, "workbench");
		LanguageRegistry.addName(workbench, "Schematica workbench");
		MinecraftForge.setBlockHarvestLevel(workbench, "pickaxe", 1);
		
		//Register partial plan
		GameRegistry.registerItem(partialPlan, "partialPlan");
		LanguageRegistry.addName(partialPlan, "Partial Plan");
		//and configure its crafting
		GameRegistry.addShapelessRecipe(partialPlanStack, emptyMapStack);
		
		//Register full plan
		GameRegistry.registerItem(fullPlan, "fullPlan");
		LanguageRegistry.addName(fullPlan, "Constructionplan");
		//and configure its crafting
		GameRegistry.addShapedRecipe(fullPlanStack, "xyx", 'x', partialPlanStack, 'y', enderEyeStack );
		GameRegistry.registerCraftingHandler(fullPlanCraftHandler);
		
	}
}
