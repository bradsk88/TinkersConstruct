package tconstruct.world.blocks;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import tconstruct.blocks.TConstructBlock;

public class TMetalBlock extends TConstructBlock
{

    static String[] metalTypes = new String[] {
        "compressed_cobalt", "compressed_ardite", "compressed_manyullyn",
        "compressed_alubrass", "compressed_alumite", "compressed_ender" };

    public static final int COBALT = ArrayUtils.indexOf(metalTypes, "compressed_cobalt");
	public static final int ARDITE = ArrayUtils.indexOf(metalTypes, "compressed_ardite");
	public static final int MANYULLYN = ArrayUtils.indexOf(metalTypes, "compressed_manyullyn");
	public static final int ALUMITE = ArrayUtils.indexOf(metalTypes, "compressed_alumite");
	public static final int ENDER = ArrayUtils.indexOf(metalTypes, "compressed_ender");
    public static final int PLATINUM = 0; //TODO: This is what WAS being used for platinium, but I doubt that's right. -BJ
    public static final int INVAR = 0; //TODO: This is what WAS being used for platinium, but I doubt that's right. -BJ
    public static final int ALUBRASS = ArrayUtils.indexOf(metalTypes, "compressed_alubrass");

    public TMetalBlock(Material material, float hardness)
    {
        super(material, hardness, metalTypes);
        this.setStepSound(Block.soundTypeMetal);
    }

    @Override
    public boolean isBeaconBase (IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
    {
        return true;
    }
}
