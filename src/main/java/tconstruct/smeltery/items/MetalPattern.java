package tconstruct.smeltery.items;

import java.util.List;

import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import tconstruct.tools.items.Pattern;

public class MetalPattern extends Pattern
{

    public MetalPattern(String patternType, String folder)
    {
        super(PATTERN_NAME, getPatternNames(patternType), folder);
    }

    protected static String[] getPatternNames (String partType)
    {
        String[] names = new String[PATTERN_NAME.length];
        for (int i = 0; i < PATTERN_NAME.length; i++)
            if (!(PATTERN_NAME[i].equals("")))
                names[i] = partType + PATTERN_NAME[i];
            else
                names[i] = "";
        return names;
    }

    public static final int INGOT = 0;
    public static final int FRYING_PAN = 10;
    public static final int GEM = 26;

    private static final String[] PATTERN_NAME = new String[] {
        "ingot", "rod", "pickaxe", "shovel", "axe",
        "swordblade", "largeguard", "mediumguard", "crossbar", "binding",
        "frypan", "sign", "knifeblade", "chisel", "largerod",
        "toughbinding", "largeplate", "broadaxe", "scythe", "excavator",
        "largeblade", "hammerhead", "fullguard", "", "", "arrowhead", "gem" };


    @Override
    public void getSubItems (Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_)
    {
        for (int i = 0; i < PATTERN_NAME.length; i++)
            if (!(PATTERN_NAME[i].equals("")))
                p_150895_3_.add(new ItemStack(p_150895_1_, 1, i));
    }

    public static Optional<Item> getOutputByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

}
