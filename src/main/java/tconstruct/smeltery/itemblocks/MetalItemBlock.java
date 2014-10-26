package tconstruct.smeltery.itemblocks;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class MetalItemBlock extends MultiItemBlock
{
    public static final String[] BLOCK_TYPES = { "Cobalt", "Ardite", "Manyullyn", "AlBrass", "Alumite", "Ender", "Platinum" };

    public static final int COBALT = ArrayUtils.indexOf(BLOCK_TYPES, "Cobalt");
    public static final int ARDITE = ArrayUtils.indexOf(BLOCK_TYPES, "Ardite");
    public static final int MANYULLYN = ArrayUtils.indexOf(BLOCK_TYPES, "Manyullyn");
    public static final int ALUMITE = ArrayUtils.indexOf(BLOCK_TYPES, "Alumite");
    public static final int ENDER = ArrayUtils.indexOf(BLOCK_TYPES, "Ender");
    public static final int PLATINUM = ArrayUtils.indexOf(BLOCK_TYPES, "Platinum");
    public static final int ALUBRASS = ArrayUtils.indexOf(BLOCK_TYPES, "AlBrass");
    public static final int INVAR = 0; //Apparently...



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
