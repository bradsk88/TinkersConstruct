package tconstruct.smeltery.items;

import cpw.mods.fml.relauncher.*;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import tconstruct.TConstruct;
import tconstruct.smeltery.TinkerSmeltery;

public class FilledBucket extends ItemBucket
{

    public FilledBucket(Block b)
    {
        super(b);
        // setTextureFile(TRepo.craftingTexture);
        // setIconIndex(224);
        setUnlocalizedName("tconstruct.bucket");
        setContainerItem(Items.bucket);
        this.setHasSubtypes(true);
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        float var4 = 1.0F;
        double trueX = player.prevPosX + (player.posX - player.prevPosX) * var4;
        double trueY = player.prevPosY + (player.posY - player.prevPosY) * var4 + 1.62D - player.yOffset;
        double trueZ = player.prevPosZ + (player.posZ - player.prevPosZ) * var4;
        boolean wannabeFull = false;
        MovingObjectPosition position = this.getMovingObjectPositionFromPlayer(world, player, wannabeFull);

        if (position == null)
        {
            return stack;
        }
        else
        {
            /*
             * FillBucketEvent event = new FillBucketEvent(player, stack, world,
             * position); if (MinecraftForge.EVENT_BUS.post(event)) { return
             * stack; }
             *
             * if (event.getResult() == Event.Result.ALLOW) { if
             * (player.capabilities.isCreativeMode) { return stack; }
             *
             * if (--stack.stackSize <= 0) { return event.result; }
             *
             * if (!player.inventory.addItemStackToInventory(event.result)) {
             * player.dropPlayerItem(event.result); }
             *
             * return stack; }
             */

            if (position.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int clickX = position.blockX;
                int clickY = position.blockY;
                int clickZ = position.blockZ;

                if (!world.canMineBlock(player, clickX, clickY, clickZ))
                {
                    return stack;
                }

                if (position.sideHit == 0)
                {
                    --clickY;
                }

                if (position.sideHit == 1)
                {
                    ++clickY;
                }

                if (position.sideHit == 2)
                {
                    --clickZ;
                }

                if (position.sideHit == 3)
                {
                    ++clickZ;
                }

                if (position.sideHit == 4)
                {
                    --clickX;
                }

                if (position.sideHit == 5)
                {
                    ++clickX;
                }

                if (!player.canPlayerEdit(clickX, clickY, clickZ, position.sideHit, stack))
                {
                    return stack;
                }

                if (this.tryPlaceContainedLiquid(world, clickX, clickY, clickZ, stack.getItemDamage()) && !player.capabilities.isCreativeMode)
                {
                    return new ItemStack(Items.bucket);
                }
            }

            return stack;
        }
    }

    public boolean tryPlaceContainedLiquid (World world, int clickX, int clickY, int clickZ, int type)
    {
        if (!WorldHelper.isAirBlock(world, clickX, clickY, clickZ) && world.getBlock(clickX, clickY, clickZ).getMaterial().isSolid())
        {
            return false;
        }
        else
        {
            try
            {
                if (TinkerSmeltery.fluidBlocks[type] == null)
                    return false;

                int metadata = 0;
                if (TinkerSmeltery.fluidBlocks[type] instanceof BlockFluidFinite)
                    metadata = 7;

                world.setBlock(clickX, clickY, clickZ, TinkerSmeltery.fluidBlocks[type], metadata, 3); // TODO: Merge liquids
            }
            catch (ArrayIndexOutOfBoundsException ex)
            {
                TConstruct.logger.warn("AIOBE occured when placing bucket into world; " + ex);
                return false;
            }

            return true;
        }
    }

    @Override
    public void getSubItems (Item b, CreativeTabs tab, List list)
    {
        for (int i = 0; i < icons.length; i++)
            list.add(new ItemStack(b, 1, i));
    }

    public IIcon[] icons;

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage (int meta)
    {
        return icons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:materials/bucket_" + textureNames[i]);
        }
    }

    @Override
    public String getUnlocalizedName (ItemStack stack)
    {
        int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, materialNames.length);
        return getUnlocalizedName() + "." + materialNames[arr];
    }

    public static final String[] materialNames = new String[] { "Iron", "Gold", "Copper", "Tin", "Aluminum", "Cobalt", "Ardite", "Bronze", "AluBrass", "Manyullyn", "Alumite", "Obsidian", "Steel", "Glass", "Stone", "Villager", "Cow", "Nickel", "Lead", "Silver", "Shiny", "Invar", "Electrum", "Ender", "Slime", "Glue", "PigIron" };

    public static final String[] textureNames = new String[] {
        "iron", "gold", "copper", "tin", "aluminum",
        "cobalt", "ardite", "bronze", "alubrass", "manyullyn",
        "alumite", "obsidian", "steel", "glass", "stone",
        "emerald", "blood", "nickel", "lead", "silver",
        "shiny", "invar", "electrum", "ender", "slime",
        "glue", "pigiron" };

    public static final int IRON = ArrayUtils.indexOf(textureNames, "iron");
    public static final int GOLD = ArrayUtils.indexOf(textureNames, "gold");
    public static final int COPPER = ArrayUtils.indexOf(textureNames, "copper");
    public static final int TIN = ArrayUtils.indexOf(textureNames, "tin");
    public static final int ALUMINUM = ArrayUtils.indexOf(textureNames, "aluminum");
    public static final int COBALT = ArrayUtils.indexOf(textureNames, "cobalt");
    public static final int ARDITE = ArrayUtils.indexOf(textureNames, "ardite");
    public static final int BRONZE = ArrayUtils.indexOf(textureNames, "bronze");
    public static final int ALUBRASS = ArrayUtils.indexOf(textureNames, "alubrass");
    public static final int MANYULLYN = ArrayUtils.indexOf(textureNames, "manyullyn");
    public static final int ALUMITE = ArrayUtils.indexOf(textureNames, "alumite");
    public static final int OBSIDIAN = ArrayUtils.indexOf(textureNames, "obsidian");
    public static final int STEEL = ArrayUtils.indexOf(textureNames, "steel");
    public static final int GLASS = ArrayUtils.indexOf(textureNames, "glass");
    public static final int STONE = ArrayUtils.indexOf(textureNames, "stone");
    public static final int EMERALD = ArrayUtils.indexOf(textureNames, "emerald");
    public static final int BLOOD = ArrayUtils.indexOf(textureNames, "blood");
    public static final int NICKEL = ArrayUtils.indexOf(textureNames, "nickel");
    public static final int LEAD = ArrayUtils.indexOf(textureNames, "lead");
    public static final int SILVER = ArrayUtils.indexOf(textureNames, "silver");
    public static final int SHINY = ArrayUtils.indexOf(textureNames, "shiny");
    public static final int INVAR = ArrayUtils.indexOf(textureNames, "invar");
    public static final int ELECTRUM = ArrayUtils.indexOf(textureNames, "electrum");
    public static final int ENDER = ArrayUtils.indexOf(textureNames, "ender");
    public static final int GLUE = ArrayUtils.indexOf(textureNames, "glue");
    public static final int SLIME = ArrayUtils.indexOf(textureNames, "slime");
    public static final int PIG_IRON = ArrayUtils.indexOf(textureNames, "pigiron");

}
