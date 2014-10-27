package tconstruct.tools.items;

import mantle.items.abstracts.CraftingItem;

import org.apache.commons.lang3.ArrayUtils;

import tconstruct.library.TConstructRegistry;

public class MaterialItem extends CraftingItem
{

    private static String[] craftingTextures = new String[] { "paperstack", "slimecrystal", "searedbrick",
        "cobaltingot", "arditeingot", "manyullyningot", "mossball", "lavacrystal", "necroticbone",
        "alubrassingot", "alumiteingot", "blueslimecrystal", "", "nugget_alubrass", "silkycloth", "silkyjewel",
        "nugget_cobalt", "nugget_ardite", "nugget_manyullyn", "nugget_alumite", "pigironingot", "nugget_pigiron",
        "glueball", "searedbrick_nether", "ardite_dust", "cobalt_dust", "manyullyn_dust", "alubrass_dust" };

    public static final String[] DICT_STRINGS = { "paperStack", "greenSlimeCrystal", "searedBrick",
        "ingotCobalt", "ingotArdite", "ingotManyullyn", "mossBall", "lavaCrystal", "necroticBone",
        "ingotAluminumBrass", "ingotAlumite", "blueSlimeCrystal", "nuggetAluminumBrass", "silkyCloth", "silkyJewel",
        "nuggetCobalt", "nuggetArdite", "nuggetManyullyn", "nuggetAlumite", "ingotPigIron", "nuggetPigIron",
        "glueball", "searedBrick", "dustArdite", "dustCobalt", "dustManyullyn", "dustAluminumBrass" };

    private static final String[] materialNames = new String[] { "PaperStack", "SlimeCrystal", "SearedBrick",
        "CobaltIngot", "ArditeIngot", "ManyullynIngot", "Mossball", "LavaCrystal", "NecroticBone",
        "AluBrassIngot", "AlumiteIngot", "BlueSlimeCrystal", "EssenceCrystal", "AluBrassNugget", "SilkyCloth", "SilkyJewel",
        "CobaltNugget", "ArditeNugget", "ManyullynNugget", "AlumiteNugget", "PigIronIngot", "PigIronNugget",
        "GlueBall", "SearedBrick", "ArditeDust", "CobaltDust", "ManyullynDust", "AluBrassDust" };

    public static final int COBALT_INGOT = ArrayUtils.indexOf(craftingTextures, "cobaltingot");
    public static final int ARDITE_INGOT = ArrayUtils.indexOf(craftingTextures, "arditeingot");
    public static final int MANYULLYN_INGOT = ArrayUtils.indexOf(craftingTextures, "manyullyningot");
    public static final int ALUBRASS_INGOT = ArrayUtils.indexOf(craftingTextures, "alubrassingot");
    public static final int ALUMITE_INGOT = ArrayUtils.indexOf(craftingTextures, "alumiteingot");
    public static final int COBALT_NUGGET = ArrayUtils.indexOf(craftingTextures, "nugget_cobalt");
    public static final int ARDITE_NUGGET = ArrayUtils.indexOf(craftingTextures, "nugget_ardite");
    public static final int MANYULLYN_NUGGET = ArrayUtils.indexOf(craftingTextures, "nugget_manyullyn");
    public static final int ALUMITE_NUGGET = ArrayUtils.indexOf(craftingTextures, "nugget_alumite");
    public static final int PIG_IRON_INGOT = ArrayUtils.indexOf(craftingTextures, "pigironingot");
    public static final int PIG_IRON_NUGGET = ArrayUtils.indexOf(craftingTextures, "nugget_pigiron");
    public static final int GLUE_BALL = ArrayUtils.indexOf(craftingTextures, "glueball");
    public static final int SEARED_BRICK = ArrayUtils.indexOf(craftingTextures, "searedbrick");
    public static final int ARDITE_DUST = ArrayUtils.indexOf(craftingTextures, "ardite_dust");
    public static final int COBALT_DUST = ArrayUtils.indexOf(craftingTextures, "cobalt_dust");
    public static final int MANYULLYN_DUST = ArrayUtils.indexOf(craftingTextures, "manyullyn_dust");
    public static final int ALUBRASS_DUST = ArrayUtils.indexOf(craftingTextures, "alubrass_dust");
    public static final int ALUBRASS_NUGGET = ArrayUtils.indexOf(craftingTextures, "nugget_alubrass");
    public static final int NECROTIC_BONE = ArrayUtils.indexOf(craftingTextures, "necroticbone");
    public static final int MOSS_BALL = ArrayUtils.indexOf(craftingTextures, "mossball");
    public static final int PAPER_STACK = ArrayUtils.indexOf(craftingTextures, "paperstack");
    public static final int LAVA_CRYSTAL = ArrayUtils.indexOf(craftingTextures, "lavacrystal");
    public static final int SILKY_CLOTH = ArrayUtils.indexOf(craftingTextures, "silkycloth");
    public static final int SILKY_JEWEL = ArrayUtils.indexOf(craftingTextures, "silkyjewel");
    public static final int SLIME_CRYSTAL = ArrayUtils.indexOf(craftingTextures, "slimecrystal");
    public static final int BLUE_SLIME_CRYSTAL = ArrayUtils.indexOf(craftingTextures, "blueslimecrystal");
    public static final int SEARED_BRICK_NETHER = ArrayUtils.indexOf(craftingTextures, "searedbrick_nether");

    public MaterialItem()
    {
        super(materialNames, getTextures(), "materials/", "tinker", TConstructRegistry.materialTab);
    }

    private static String[] getTextures ()
    {
        String[] names = new String[craftingTextures.length];
        for (int i = 0; i < craftingTextures.length; i++)
        {
            if (craftingTextures[i].equals(""))
                names[i] = "";
            else
                names[i] = "material_" + craftingTextures[i];
        }
        return names;
    }

}
