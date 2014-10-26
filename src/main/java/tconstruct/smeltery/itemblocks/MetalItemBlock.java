package tconstruct.smeltery.itemblocks;

import java.util.List;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class MetalItemBlock extends MultiItemBlock
{
    public static final String[] BLOCK_TYPES = { "Cobalt", "Ardite", "Manyullyn", "AlBrass", "Alumite", "Ender", "Platinum" };


    public MetalItemBlock(Block b)
    {
        super(b, "StorageMetals", BLOCK_TYPES);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add(StatCollector.translateToLocal("metalblock.tooltip"));
    }
}
