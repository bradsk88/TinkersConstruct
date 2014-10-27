package tconstruct.tools.items;

import cpw.mods.fml.relauncher.*;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import mantle.items.abstracts.CraftingItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.StatCollector;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder.MaterialSet;
import tconstruct.library.util.IPattern;
import tconstruct.tools.TinkerTools;

public class Pattern extends CraftingItem implements IPattern
{
    public Pattern(String patternType, String folder)
    {
        this(patternName.values().toArray(new String[patternName.size()]), getPatternNames(patternType), folder);
    }

    public Pattern(String[] names, String[] patternTypes, String folder)
    {
        super(names, patternTypes, folder, "tinker", TConstructRegistry.materialTab);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setContainerItem(this);
        this.setMaxStackSize(1);
    }

    protected static String[] getPatternNames (String partType)
    {
        String[] names = new String[patternName.size()];
        int i = 0;
        for (String s : patternName.values())
            names[i++] = partType + s;
        return names;
    }

    private static final Map<String, String> patternName;
    static {

        ImmutableMap.Builder<String, String> b = ImmutableMap.builder();
        b.put(TinkerTools.INGOT_NAME, "ingot");
        b.put(TinkerTools.TOOL_ROD_NAME, "rod");
        b.put(TinkerTools.PICKAXE_HEAD_NAME, "pickaxe");
        b.put(TinkerTools.SHOVEL_HEAD_NAME, "shovel");
        b.put(TinkerTools.AXE_HEAD_NAME, "axe");
        b.put(TinkerTools.SWORD_BLADE_NAME, "swordblade");
        b.put(TinkerTools.LARGE_GUARD_NAME, "largeguard");
        b.put(TinkerTools.MEDIUM_GUARD_NAME, "mediumguard");
        b.put(TinkerTools.CROSS_BAR_NAME, "crossbar");
        b.put(TinkerTools.BINDING_NAME, "binding");
        b.put(TinkerTools.FRY_PAN_HEAD, "frypan");
        b.put(TinkerTools.SIGN_HEAD_NAME, "sign");
        b.put(TinkerTools.KNIFE_BLADE_NAME, "knifeblade");
        b.put(TinkerTools.CHISEL_HEAD_NAME, "chisel");
        b.put(TinkerTools.LARGE_ROD_NAME, "largerod");
        b.put(TinkerTools.TOUGH_BINDING_NAME, "toughbinding");
        b.put(TinkerTools.LARGE_PLATE_NAME, "largeplate");
        b.put(TinkerTools.BROAD_AXE_HEAD_NAME, "broadaxe");
        b.put(TinkerTools.SCYTHE_BLADE_NAME, "scythe");
        b.put(TinkerTools.EXCAVATOR_HEAD_NAME, "excavator");
        b.put(TinkerTools.LARGE_SWORD_BLADE_NAME, "largeblade");
        b.put(TinkerTools.HAMMER_HEAD_NAME, "hammerhead");
        b.put(TinkerTools.FULL_GUARD_NAME, "fullguard");
        b.put(TinkerTools.BOW_STRING_NAME, "bowstring");
        b.put(TinkerTools.FLETCHING_NAME, "fletching");
        b.put(TinkerTools.ARROW_HEAD_NAME, "arrowhead");
        patternName = b.build();
    }


    @Override
    public void getSubItems(Item b, CreativeTabs tab, List list)
    {
        for (int i = 1; i < patternName.size(); i++)
        {
            list.add(new ItemStack(b, 1, i));
        }
    }

    @Override
    public ItemStack getContainerItem (ItemStack stack)
    {
        if (stack.stackSize <= 0)
            return null;
        return stack;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid (ItemStack stack)
    {
        return false;
    }

    /* Tags and information about the pattern */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        float cost = getPatternCost(stack) / 2f;
        if (cost > 0)
        {
            if (cost - (int) cost < 0.1)
                list.add(StatCollector.translateToLocal("pattern1.tooltip") + (int) cost);
            else
                list.add(StatCollector.translateToLocal("pattern2.tooltip") + cost);
        }
    }

    // 2 for full material, 1 for half.
    @Override
    public int getPatternCost (ItemStack pattern)
    {
        switch (pattern.getItemDamage())
        {
        case 0:
            return 2;
        case 1:
            return 1;
        case 2:
            return 2;
        case 3:
            return 2;
        case 4:
            return 2;
        case 5:
            return 2;
        case 6:
            return 1;
        case 7:
            return 1;
        case 8:
            return 1;
        case 9:
            return 1;
        case 10:
            return 2;
        case 11:
            return 2;
        case 12:
            return 1;
        case 13:
            return 1;
        case 14:
            return 6;
        case 15:
            return 6;
        case 16:
            return 16;
        case 17:
            return 16;
        case 18:
            return 16;
        case 19:
            return 16;
        case 20:
            return 16;
        case 21:
            return 16;
        case 22:
            return 6;
        case 23:
            return 6;
        case 24:
            return 2;
        case 25:
            return 2;
        default:
            return 0;
        }
    }

    @Override
    public ItemStack getPatternOutput (ItemStack stack, ItemStack input, MaterialSet set)
    {
        return TConstructRegistry.getPartMapping(this, stack.getItemDamage(), set.materialID);
    }

    public Item getPatternByName(String partName) {
        return new ItemStack(this, patternName.values())
    }
}
