package tconstruct.plugins.te4;

import ca.bradj.orecore.item.OreCoreItems;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.*;
import tconstruct.TConstruct;
import tconstruct.smeltery.blocks.MetalOre;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.items.MaterialItem;
import tconstruct.world.TinkerWorld;
import tconstruct.world.itemblocks.MetalOreItemBlock;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers TE4 Compatibility", description = "Tinkers Construct compatibility for Thermal Expansion", modsRequired = "ThermalExpansion")
public class TinkerTE4
{
    @Handler
    public void init ()
    {
        TConstruct.logger.info("Thermal Expansion detected. Adding Pulverizer & Induction Smelter recipes");
        ItemStack crystalCinnabar = OreDictionary.getOres("crystalCinnabar").get(0);

        TE4Helper.addPulverizerRecipe(1000, new ItemStack(TinkerTools.materials, 1, MaterialItem.COBALT_INGOT), new ItemStack(TinkerTools.materials, 1, MaterialItem.COBALT_DUST), null, 0);
        TE4Helper.addPulverizerRecipe(1000, new ItemStack(TinkerTools.materials, 1, MaterialItem.ARDITE_INGOT), new ItemStack(TinkerTools.materials, 1, MaterialItem.ARDITE_DUST), null, 0);

        TE4Helper.addPulverizerRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, MetalOre.COBALT), new ItemStack(TinkerTools.materials, 2, MaterialItem.COBALT_DUST), GameRegistry.findItemStack("ThermalExpansion", "dustIron", 1), 10);
        TE4Helper.addSmelterRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, MetalOre.COBALT), crystalCinnabar.copy(), new ItemStack(TinkerTools.materials, 3, MaterialItem.COBALT_INGOT), new ItemStack(Items.iron_ingot), 100);
        TE4Helper.addPulverizerRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, MetalOre.ARDITE), new ItemStack(TinkerTools.materials, 2, MaterialItem.ARDITE_DUST), GameRegistry.findItemStack("ThermalExpansion", "dustGold", 1), 10);
        TE4Helper.addSmelterRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, MetalOre.ARDITE), crystalCinnabar.copy(), new ItemStack(TinkerTools.materials, 3, MaterialItem.ARDITE_INGOT), new ItemStack(Items.gold_ingot), 100);

        TE4Helper.addSmelterRecipe(4000, new ItemStack(TinkerTools.materials, 1, MaterialItem.ARDITE_INGOT), new ItemStack(TinkerTools.materials, 1, MaterialItem.COBALT_INGOT), new ItemStack(TinkerTools.materials, 1, MaterialItem.MANYULLYN_INGOT), null, 0);
        TE4Helper.addSmelterRecipe(4000, new ItemStack(OreCoreItems.copperIngot), new ItemStack(OreCoreItems.aluminumIngot), new ItemStack(TinkerTools.materials, 4, MaterialItem.ALUBRASS_INGOT), null, 0);

        // Dust Recipes
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerTools.materials, 1, MaterialItem.MANYULLYN_DUST), "dustArdite", "dustCobalt"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerTools.materials, 4, MaterialItem.ALUBRASS_DUST), "dustAluminum", "dustAluminum", "dustAluminum", "dustCopper"));
    }
}
