package tconstruct.world.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class CobaltGravelOreItem extends MultiItemBlock
{
	
	public static final int COBALT = 0;
	
    public static final String blockTypes[] = { "cobalt" };

    public CobaltGravelOreItem(Block b)
    {
        super(b, "block.ore.gravel", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
