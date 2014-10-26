package tconstruct.smeltery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mantle.blocks.BlockUtils;
import mantle.blocks.abstracts.MultiServantLogic;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.Smeltery;
import tconstruct.library.util.IPattern;
import tconstruct.smeltery.blocks.BloodBlock;
import tconstruct.smeltery.blocks.CastingChannelBlock;
import tconstruct.smeltery.blocks.GlassBlockConnected;
import tconstruct.smeltery.blocks.GlassBlockConnectedMeta;
import tconstruct.smeltery.blocks.GlassPaneConnected;
import tconstruct.smeltery.blocks.GlassPaneStained;
import tconstruct.smeltery.blocks.GlueBlock;
import tconstruct.smeltery.blocks.GlueFluid;
import tconstruct.smeltery.blocks.LavaTankBlock;
import tconstruct.smeltery.blocks.PigIronMoltenBlock;
import tconstruct.smeltery.blocks.SearedBlock;
import tconstruct.smeltery.blocks.SearedSlab;
import tconstruct.smeltery.blocks.SmelteryBlock;
import tconstruct.smeltery.blocks.SpeedBlock;
import tconstruct.smeltery.blocks.SpeedSlab;
import tconstruct.smeltery.blocks.TConstructFluid;
import tconstruct.smeltery.blocks.TankAirBlock;
import tconstruct.smeltery.itemblocks.CastingChannelItem;
import tconstruct.smeltery.itemblocks.GlassBlockItem;
import tconstruct.smeltery.itemblocks.GlassPaneItem;
import tconstruct.smeltery.itemblocks.LavaTankItemBlock;
import tconstruct.smeltery.itemblocks.MetalItemBlock;
import tconstruct.smeltery.itemblocks.SearedSlabItem;
import tconstruct.smeltery.itemblocks.SearedTableItemBlock;
import tconstruct.smeltery.itemblocks.SmelteryItemBlock;
import tconstruct.smeltery.itemblocks.SpeedBlockItem;
import tconstruct.smeltery.itemblocks.SpeedSlabItem;
import tconstruct.smeltery.itemblocks.StainedGlassClearItem;
import tconstruct.smeltery.itemblocks.StainedGlassClearPaneItem;
import tconstruct.smeltery.items.FilledBucket;
import tconstruct.smeltery.items.MetalPattern;
import tconstruct.smeltery.logic.AdaptiveDrainLogic;
import tconstruct.smeltery.logic.AdaptiveSmelteryLogic;
import tconstruct.smeltery.logic.CastingBasinLogic;
import tconstruct.smeltery.logic.CastingChannelLogic;
import tconstruct.smeltery.logic.CastingTableLogic;
import tconstruct.smeltery.logic.FaucetLogic;
import tconstruct.smeltery.logic.LavaTankLogic;
import tconstruct.smeltery.logic.SmelteryDrainLogic;
import tconstruct.smeltery.logic.SmelteryLogic;
import tconstruct.smeltery.logic.TankAirLogic;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.TinkerTools.MaterialID;
import tconstruct.tools.blocks.MultiBrick;
import tconstruct.tools.items.MaterialItem;
import tconstruct.util.config.PHConstruct;
import tconstruct.world.TinkerWorld;
import tconstruct.world.blocks.TMetalBlock;
import ca.bradj.orecore.item.OreCoreItems;
import ca.bradj.orecore.item.gold.Gold;
import ca.bradj.orecore.item.iron.Iron;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers' Smeltery", description = "Liquid metals, casting, and the multiblock structure.")
public class TinkerSmeltery {

    //Delays: ie the amount of time the tap on the smeltery shows its "pour" animation.
    private static final int GLUE_TEMP = 200;
    private static final int METAL_MELT_DELAY = 50;
    private static final int CAST_DELAY = 80;
    private static final int BUCKET_FILL_DELAY = 10;
    private static final int BASIN_CASTING_MELT_DELAY = 100;

    private static final int BUCKET_SIZE = 1000; //The amount of fluid that fits in a bucket
    private static final int MAX_NUM_COMPRESSED_MATERIALS_FOR_SMELTERY = 4;

    //The amount of heat used when melting various items
    private static final int GLUE_HEAT_USE = 125;
    private static final int PIG_IRON_HEAT_USE = 610;
    private static final int EMERALD_HEAT_USE = 575;
    private static final int STONE_HEAT_USE = 800;
    private static final int ENDER_HEAT_USE = 500;
    private static final int ELECTRUM_HEAT_USE = 400;
    private static final int INVAR_HEAT_USE = 400;
    private static final int PLATINUM_HEAT_USE = 400;
    private static final int SILVER_HEAT_USE = 400;
    private static final int LEAD_HEAT_USE = 400;
    private static final int NICKEL_HEAT_USE = 400;
    private static final int STEEL_HEAT_USE = 700;
    private static final int BRONZE_HEAT_USE = 500;
    private static final int MANYULLYN_HEAT_USE = 750;
    private static final int ALUMITE_HEAT_USE = 800;
    private static final int BRASS_HEAT_USE = 350;
    private static final int ARDITE_HEAT_USE = 650;
    private static final int COBALT_HEAT_USE = 650;
    private static final int ALUMINUM_HEAT_USE = 350;
    private static final int COPPER_HEAT_USE = 550;
    private static final int WATER_HEAT_USE = 20;
    private static final int SAND_HEAT_USE = 175;
    private static final int GLASS_HEAT_USE = 625;
    private static final int OBSIDIAN_HEAT_USE = 800;
    private static final int HORSE_ARMOR_HEAT_USE = 100;
    private static final int IRON_HEAT_USE = 600;
    private static final int GOLD_HEAT_USE = 400;
    private static final int TIN_HEAT_USE = 400;
    private static final int ANVIL_HEAT_USE = 200;
    private static final int ARMOR_MELT_USE = 50;
    private static final int DUST_HEAT_USE = -75;
    private static final int BLOCK_HEAT_USE = 100;
    private static final int INGOT_HEAT_USE = -50;
    private static final int NUGGET_HEAT_USE = -100;
    private static final int ICE_HEAT_USE = 75;
    private static final int NETHER_ORE_HEAT_USE = 75;
    private static final int DENSE_ORE_HEAT_USE = 75;
    private static final int POOR_ORE_HEAT_USE = 75;

    //The amount of fluid created when various items are melted
    private static final int ANVIL_FLUID_OUTPUT = TConstruct.ingotLiquidValue * 31;
    private static final int BOOTS_FLUID_OUTPUT = TConstruct.ingotLiquidValue * 4;
    private static final int CAULDRON_FLUID_OUTPUT = TConstruct.ingotLiquidValue * 7;
    private static final int LEGGINGS_FLUID_OUTPUT = TConstruct.ingotLiquidValue * 7;
    private static final int CHESTPLATE_FLUID_OUTPUT = TConstruct.ingotLiquidValue * 8;
    private static final int HELMET_FLUID_OUTPUT = TConstruct.ingotLiquidValue * 5;
    private static final int PICKAXE_FLUID_OUTPUT = TConstruct.ingotLiquidValue * 3;
    private static final int AXE_FLUID_OUTPUT = TConstruct.ingotLiquidValue * 3;
    private static final int HORSE_ARMOR_FLUID_OUTPUT = TConstruct.ingotLiquidValue * 8;
    private static final int SNOW_LAYER_WATER_OUTPUT = 250;
    private static final int SNOW_WATER_OUTPUT = 500;
    private static final int ICE_WATER_OUTPUT = 1000;
    private static final int POOR_ORE_FLUID_OUTPUT = (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsPerOre);
    private static final int DENSE_ORE_FLUID_OUTPUT = (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre * 3);
    private static final int NETHER_ORE_FLUID_OUTPUT = (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre * 2);
    private static final int OBSIDIAN_DUST_FLUID_OUTPUT = (int) (TConstruct.ingotLiquidValue * 0.25);
    private static final int OBSIDIAN_FLUID_OUTPUT = TConstruct.ingotLiquidValue * 2;
    private static final int OBSIDIAN_POOR_ORE_FLUID_OUTPUT = (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsPerOre * 1.5f);

    //Requirements for casting items
    private static final int EMERALD_FLUID_REQ = 640;
    private static final int ENDER_PEARL_FLUID_REQ = 250;

    private static final int L = 12; //Standard luminosity
    private static final int D = 3000; //Standard Density
    private static final int V = 6000; //Standard Viscosity
    private static final int T = 1300; //Standard Temperature

    private static final int FULL_GRID = 9;
    private static final int NO_META = 0;

    @SidedProxy(clientSide = "tconstruct.smeltery.SmelteryProxyClient", serverSide = "tconstruct.smeltery.SmelteryProxyCommon")
    public static SmelteryProxyCommon proxy;

    public static Item metalPattern;
    // public static Item armorPattern;
    public static Item buckets;
    public static Block smeltery;
    public static Block lavaTank;
    public static Block searedBlock;
    public static Block castingChannel;
    public static Block tankAir;
    public static Block smelteryNether;
    public static Block lavaTankNether;
    public static Block searedBlockNether;
    public static Block searedSlab;
    public static Block glueBlock;
    public static Block clearGlass;
    // public static Block stainedGlass;
    public static Block stainedGlassClear;
    public static Block glassPane;
    // public static Block stainedGlassPane;
    public static Block stainedGlassClearPane;
    public static Block glassMagicSlab;
    public static Block stainedGlassMagicSlab;
    public static Block stainedGlassClearMagicSlab;
    // Liquids
    public static Material liquidMetal;
    public static Fluid moltenIronFluid;
    public static Fluid moltenGoldFluid;
    public static Fluid moltenCopperFluid;
    public static Fluid moltenTinFluid;
    public static Fluid moltenAluminumFluid;
    public static Fluid moltenCobaltFluid;
    public static Fluid moltenArditeFluid;
    public static Fluid moltenBronzeFluid;
    public static Fluid moltenAlubrassFluid;
    public static Fluid moltenManyullynFluid;
    public static Fluid moltenAlumiteFluid;
    public static Fluid moltenObsidianFluid;
    public static Fluid moltenSteelFluid;
    public static Fluid moltenGlassFluid;
    public static Fluid moltenStoneFluid;
    public static Fluid moltenEmeraldFluid;
    public static Fluid moltenNickelFluid;
    public static Fluid moltenLeadFluid;
    public static Fluid moltenSilverFluid;
    public static Fluid moltenShinyFluid;
    public static Fluid moltenInvarFluid;
    public static Fluid moltenElectrumFluid;
    public static Fluid moltenEnderFluid;
    public static TConstructFluid moltenIron;
    public static Block moltenGold;
    public static Block moltenCopper;
    public static Block moltenTin;
    public static Block moltenAluminum;
    public static Block moltenCobalt;
    public static Block moltenArdite;
    public static Block moltenBronze;
    public static Block moltenAlubrass;
    public static Block moltenManyullyn;
    public static Block moltenAlumite;
    public static Block moltenObsidian;
    public static Block moltenSteel;
    public static Block moltenGlass;
    public static Block moltenStone;
    public static Block moltenEmerald;
    public static Block moltenNickel;
    public static Block moltenLead;
    public static Block moltenSilver;
    public static Block moltenShiny;
    public static Block moltenInvar;
    public static Block moltenElectrum;
    public static Block moltenEnder;
    // Glue
    public static Fluid glueFluid;
    public static Block glueFluidBlock;
    // Pigiron
    public static Fluid pigIronFluid;
    public static Block pigIronFluidBlock;
    public static Fluid[] fluids = new Fluid[26];
    public static Block[] fluidBlocks = new Block[25];
    public static FluidStack[] liquids;
    public static Block speedSlab;
    // InfiBlocks
    public static Block speedBlock;
    public static Fluid bloodFluid;
    public static Block blood;

    @Handler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TinkerSmelteryEvents());

        TinkerSmeltery.buckets = new FilledBucket(BlockUtils.getBlockFromItem(TinkerSmeltery.buckets));
        GameRegistry.registerItem(TinkerSmeltery.buckets, "buckets");

        TinkerSmeltery.searedSlab = new SearedSlab().setBlockName("SearedSlab");
        TinkerSmeltery.searedSlab.stepSound = Block.soundTypeStone;

        TinkerSmeltery.speedSlab = new SpeedSlab().setBlockName("SpeedSlab");
        TinkerSmeltery.speedSlab.stepSound = Block.soundTypeStone;

        TinkerSmeltery.glueBlock = new GlueBlock().setBlockName("GlueBlock").setCreativeTab(TConstructRegistry.blockTab);

        // Smeltery
        TinkerSmeltery.smeltery = new SmelteryBlock().setBlockName("Smeltery");
        TinkerSmeltery.smelteryNether = new SmelteryBlock("nether").setBlockName("Smeltery");
        TinkerSmeltery.lavaTank = new LavaTankBlock().setBlockName("LavaTank");
        TinkerSmeltery.lavaTank.setStepSound(Block.soundTypeGlass);
        TinkerSmeltery.lavaTankNether = new LavaTankBlock("nether").setStepSound(Block.soundTypeGlass).setBlockName("LavaTank");

        TinkerSmeltery.searedBlock = new SearedBlock().setBlockName("SearedBlock");
        TinkerSmeltery.searedBlockNether = new SearedBlock("nether").setBlockName("SearedBlock");

        TinkerSmeltery.castingChannel = (new CastingChannelBlock()).setBlockName("CastingChannel");

        TinkerSmeltery.tankAir = new TankAirBlock(Material.leaves).setBlockUnbreakable().setBlockName("tconstruct.tank.air");

        // Liquids
        TinkerSmeltery.liquidMetal = new MaterialLiquid(MapColor.tntColor);

        TinkerSmeltery.moltenIronFluid = new Fluid("iron.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isIronPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenIronFluid);
        TinkerSmeltery.moltenIron = new TConstructFluid(TinkerSmeltery.moltenIronFluid, Material.lava, "liquid_iron");
        TinkerSmeltery.moltenIron.setBlockName("fluid.molten.iron");
        GameRegistry.registerBlock(TinkerSmeltery.moltenIron, "fluid.molten.iron");
        if (isIronPreReg) {
            TinkerSmeltery.moltenIronFluid = FluidRegistry.getFluid("iron.molten");
            Block regMoltenIronBlock = TinkerSmeltery.moltenIronFluid.getBlock();
            if (regMoltenIronBlock != null) {
                TinkerSmeltery.moltenIron.suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenIron = (TConstructFluid) regMoltenIronBlock;
            } else
                TinkerSmeltery.moltenIronFluid.setBlock(TinkerSmeltery.moltenIron);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenIronFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenIronFluid, BUCKET_SIZE), new ItemStack(
                    TinkerSmeltery.buckets, 1, FilledBucket.IRON), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenGoldFluid = defineMoltenGold();
        boolean isGoldPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenGoldFluid);
        TinkerSmeltery.moltenGold = new TConstructFluid(TinkerSmeltery.moltenGoldFluid, Material.lava, "liquid_gold")
                .setBlockName("fluid.molten.gold");
        GameRegistry.registerBlock(TinkerSmeltery.moltenGold, "fluid.molten.gold");
        if (isGoldPreReg) {
            TinkerSmeltery.moltenGoldFluid = FluidRegistry.getFluid("gold.molten");
            Block regMoltenGoldBlock = TinkerSmeltery.moltenGoldFluid.getBlock();
            if (regMoltenGoldBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenGold).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenGold = regMoltenGoldBlock;
            } else
                TinkerSmeltery.moltenGoldFluid.setBlock(TinkerSmeltery.moltenGold);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenGoldFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenGoldFluid, BUCKET_SIZE), new ItemStack(
                    TinkerSmeltery.buckets, 1, FilledBucket.GOLD), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenCopperFluid = new Fluid("copper.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isCopperPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenCopperFluid);
        TinkerSmeltery.moltenCopper = new TConstructFluid(TinkerSmeltery.moltenCopperFluid, Material.lava, "liquid_copper")
                .setBlockName("fluid.molten.copper");
        GameRegistry.registerBlock(TinkerSmeltery.moltenCopper, "fluid.molten.copper");
        if (isCopperPreReg) {
            TinkerSmeltery.moltenCopperFluid = FluidRegistry.getFluid("copper.molten");
            Block regMoltenCopperBlock = TinkerSmeltery.moltenCopperFluid.getBlock();
            if (regMoltenCopperBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenCopper).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenCopper = regMoltenCopperBlock;
            } else
                TinkerSmeltery.moltenCopperFluid.setBlock(TinkerSmeltery.moltenCopper);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenCopperFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenCopperFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.COPPER), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenTinFluid = new Fluid("tin.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isTinPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenTinFluid);
        TinkerSmeltery.moltenTin = new TConstructFluid(TinkerSmeltery.moltenTinFluid, Material.lava, "liquid_tin").setBlockName("fluid.molten.tin");
        GameRegistry.registerBlock(TinkerSmeltery.moltenTin, "fluid.molten.tin");
        if (isTinPreReg) {
            TinkerSmeltery.moltenTinFluid = FluidRegistry.getFluid("tin.molten");
            Block regMoltenTinBlock = TinkerSmeltery.moltenTinFluid.getBlock();
            if (regMoltenTinBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenTin).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenTin = regMoltenTinBlock;
            } else
                TinkerSmeltery.moltenTinFluid.setBlock(TinkerSmeltery.moltenTin);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenTinFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenTinFluid, BUCKET_SIZE), new ItemStack(
                    TinkerSmeltery.buckets, 1, FilledBucket.TIN), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenAluminumFluid = new Fluid("aluminum.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isAluminumPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenAluminumFluid);
        TinkerSmeltery.moltenAluminum = new TConstructFluid(TinkerSmeltery.moltenAluminumFluid, Material.lava, "liquid_aluminum")
                .setBlockName("fluid.molten.aluminum");
        GameRegistry.registerBlock(TinkerSmeltery.moltenAluminum, "fluid.molten.aluminum");
        if (isAluminumPreReg) {
            TinkerSmeltery.moltenAluminumFluid = FluidRegistry.getFluid("aluminum.molten");
            Block regMoltenAluminumBlock = TinkerSmeltery.moltenAluminumFluid.getBlock();
            if (regMoltenAluminumBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenAluminum).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenAluminum = regMoltenAluminumBlock;
            } else
                TinkerSmeltery.moltenAluminumFluid.setBlock(TinkerSmeltery.moltenAluminum);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenAluminumFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenAluminumFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.ALUMINUM), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenCobaltFluid = new Fluid("cobalt.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isCobaltPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenCobaltFluid);
        TinkerSmeltery.moltenCobalt = new TConstructFluid(TinkerSmeltery.moltenCobaltFluid, Material.lava, "liquid_cobalt")
                .setBlockName("fluid.molten.cobalt");
        GameRegistry.registerBlock(TinkerSmeltery.moltenCobalt, "fluid.molten.cobalt");
        if (isCobaltPreReg) {
            TinkerSmeltery.moltenCobaltFluid = FluidRegistry.getFluid("cobalt.molten");
            Block regMoltenCobaltBlock = TinkerSmeltery.moltenCobaltFluid.getBlock();
            if (regMoltenCobaltBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenCobalt).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenCobalt = regMoltenCobaltBlock;
            } else
                TinkerSmeltery.moltenCobaltFluid.setBlock(TinkerSmeltery.moltenCobalt);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenCobaltFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenCobaltFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.COBALT), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenArditeFluid = new Fluid("ardite.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isArditePreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenArditeFluid);
        TinkerSmeltery.moltenArdite = new TConstructFluid(TinkerSmeltery.moltenArditeFluid, Material.lava, "liquid_ardite")
                .setBlockName("fluid.molten.ardite");
        GameRegistry.registerBlock(TinkerSmeltery.moltenArdite, "fluid.molten.ardite");
        if (isArditePreReg) {
            TinkerSmeltery.moltenArditeFluid.setBlock(TinkerSmeltery.moltenArdite);
            Block regMoltenArditeBlock = TinkerSmeltery.moltenArditeFluid.getBlock();
            if (regMoltenArditeBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenArdite).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenArdite = regMoltenArditeBlock;
            } else
                TinkerSmeltery.moltenArditeFluid = FluidRegistry.getFluid("ardite.molten");
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenArditeFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenArditeFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.ARDITE), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenBronzeFluid = new Fluid("bronze.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isBronzeFluid = !FluidRegistry.registerFluid(TinkerSmeltery.moltenBronzeFluid);
        TinkerSmeltery.moltenBronze = new TConstructFluid(TinkerSmeltery.moltenBronzeFluid, Material.lava, "liquid_bronze")
                .setBlockName("fluid.molten.bronze");
        GameRegistry.registerBlock(TinkerSmeltery.moltenBronze, "fluid.molten.bronze");
        if (isBronzeFluid) {
            TinkerSmeltery.moltenBronzeFluid = FluidRegistry.getFluid("bronze.molten");
            Block regMoltenBronzeBlock = TinkerSmeltery.moltenBronzeFluid.getBlock();
            if (regMoltenBronzeBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenBronze).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenBronze = regMoltenBronzeBlock;
            } else
                TinkerSmeltery.moltenBronzeFluid.setBlock(TinkerSmeltery.moltenBronze);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenBronzeFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenBronzeFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.BRONZE), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenAlubrassFluid = new Fluid("aluminumbrass.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isAlubrassPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenAlubrassFluid);
        TinkerSmeltery.moltenAlubrass = new TConstructFluid(TinkerSmeltery.moltenAlubrassFluid, Material.lava, "liquid_alubrass")
                .setBlockName("fluid.molten.alubrass");
        GameRegistry.registerBlock(TinkerSmeltery.moltenAlubrass, "fluid.molten.alubrass");
        if (isAlubrassPreReg) {
            TinkerSmeltery.moltenAlubrassFluid = FluidRegistry.getFluid("aluminumbrass.molten");
            Block regMoltenAlubrassBlock = TinkerSmeltery.moltenAlubrassFluid.getBlock();
            if (regMoltenAlubrassBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenAlubrass).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenAlubrass = regMoltenAlubrassBlock;
            } else
                TinkerSmeltery.moltenAlubrassFluid.setBlock(TinkerSmeltery.moltenAlubrass);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenAlubrassFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenAlubrassFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.ALUBRASS), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenManyullynFluid = new Fluid("manyullyn.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isManyullynPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenManyullynFluid);
        TinkerSmeltery.moltenManyullyn = new TConstructFluid(TinkerSmeltery.moltenManyullynFluid, Material.lava, "liquid_manyullyn")
                .setBlockName("fluid.molten.manyullyn");
        GameRegistry.registerBlock(TinkerSmeltery.moltenManyullyn, "fluid.molten.manyullyn");
        if (isManyullynPreReg) {
            TinkerSmeltery.moltenManyullynFluid = FluidRegistry.getFluid("manyullyn.molten");
            Block regMoltenManyullyn = TinkerSmeltery.moltenManyullynFluid.getBlock();
            if (regMoltenManyullyn != null) {
                ((TConstructFluid) TinkerSmeltery.moltenManyullyn).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenManyullyn = regMoltenManyullyn;
            } else
                TinkerSmeltery.moltenManyullynFluid.setBlock(TinkerSmeltery.moltenManyullyn);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenManyullynFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenManyullynFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.MANYULLYN), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenAlumiteFluid = new Fluid("alumite.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isAlumitePreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenAlumiteFluid);
        TinkerSmeltery.moltenAlumite = new TConstructFluid(TinkerSmeltery.moltenAlumiteFluid, Material.lava, "liquid_alumite")
                .setBlockName("fluid.molten.alumite");
        GameRegistry.registerBlock(TinkerSmeltery.moltenAlumite, "fluid.molten.alumite");
        if (isAlumitePreReg) {
            TinkerSmeltery.moltenAlumiteFluid = FluidRegistry.getFluid("alumite.molten");
            Block regMoltenAlumiteBlock = TinkerSmeltery.moltenAlumiteFluid.getBlock();
            if (regMoltenAlumiteBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenAlumite).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenAlumite = regMoltenAlumiteBlock;
            } else
                TinkerSmeltery.moltenAlumiteFluid.setBlock(TinkerSmeltery.moltenAlumite);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenAlumiteFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenAlumiteFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.ALUMITE), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenObsidianFluid = new Fluid("obsidian.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isObsidianPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenObsidianFluid);
        TinkerSmeltery.moltenObsidian = new TConstructFluid(TinkerSmeltery.moltenObsidianFluid, Material.lava, "liquid_obsidian")
                .setBlockName("fluid.molten.obsidian");
        GameRegistry.registerBlock(TinkerSmeltery.moltenObsidian, "fluid.molten.obsidian");
        if (isObsidianPreReg) {
            TinkerSmeltery.moltenObsidianFluid = FluidRegistry.getFluid("obsidian.molten");
            Block regMoltenObsidianBlock = TinkerSmeltery.moltenObsidianFluid.getBlock();
            if (regMoltenObsidianBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenObsidian).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenObsidian = regMoltenObsidianBlock;
            } else
                TinkerSmeltery.moltenObsidianFluid.setBlock(TinkerSmeltery.moltenObsidian);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenObsidianFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenObsidianFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.OBSIDIAN), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenSteelFluid = new Fluid("steel.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isSteelPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenSteelFluid);
        TinkerSmeltery.moltenSteel = new TConstructFluid(TinkerSmeltery.moltenSteelFluid, Material.lava, "liquid_steel")
                .setBlockName("fluid.molten.steel");
        GameRegistry.registerBlock(TinkerSmeltery.moltenSteel, "fluid.molten.steel");
        if (isSteelPreReg) {
            TinkerSmeltery.moltenSteelFluid = FluidRegistry.getFluid("steel.molten");
            Block regMoltenSteelBlock = TinkerSmeltery.moltenSteelFluid.getBlock();
            if (regMoltenSteelBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenSteel).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenSteel = regMoltenSteelBlock;
            } else
                TinkerSmeltery.moltenSteelFluid.setBlock(TinkerSmeltery.moltenSteel);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenSteelFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenSteelFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.STEEL), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenGlassFluid = new Fluid("glass.molten").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isGlassPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenGlassFluid);
        TinkerSmeltery.moltenGlass = new TConstructFluid(TinkerSmeltery.moltenGlassFluid, Material.lava, "liquid_glass", true)
                .setBlockName("fluid.molten.glass");
        GameRegistry.registerBlock(TinkerSmeltery.moltenGlass, "fluid.molten.glass");
        if (isGlassPreReg) {
            TinkerSmeltery.moltenGlassFluid = FluidRegistry.getFluid("glass.molten");
            Block regMoltenGlassBlock = TinkerSmeltery.moltenGlassFluid.getBlock();
            if (regMoltenGlassBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenGlass).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenGlass = regMoltenGlassBlock;
            } else
                TinkerSmeltery.moltenGlassFluid.setBlock(TinkerSmeltery.moltenGlass);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenGlassFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenGlassFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.GLASS), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenStoneFluid = new Fluid("stone.seared").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isStonePreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenStoneFluid);
        TinkerSmeltery.moltenStone = new TConstructFluid(TinkerSmeltery.moltenStoneFluid, Material.lava, "liquid_stone").setBlockName("molten.stone");
        GameRegistry.registerBlock(TinkerSmeltery.moltenStone, "molten.stone");
        if (isStonePreReg) {
            TinkerSmeltery.moltenStoneFluid = FluidRegistry.getFluid("stone.seared");
            Block regMoltenStoneBlock = TinkerSmeltery.moltenStoneFluid.getBlock();
            if (regMoltenStoneBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenStone).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenStone = regMoltenStoneBlock;
            } else
                TinkerSmeltery.moltenStoneFluid.setBlock(TinkerSmeltery.moltenStone);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenStoneFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenStoneFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.STONE), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenEmeraldFluid = new Fluid("emerald.liquid").setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        boolean isEmeraldPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenEmeraldFluid);
        TinkerSmeltery.moltenEmerald = new TConstructFluid(TinkerSmeltery.moltenEmeraldFluid, Material.lava, "liquid_villager")
                .setBlockName("molten.emerald");
        GameRegistry.registerBlock(TinkerSmeltery.moltenEmerald, "molten.emerald");
        if (isEmeraldPreReg) {
            TinkerSmeltery.moltenEmeraldFluid = FluidRegistry.getFluid("emerald.liquid");
            Block regMoltenEmeraldBlock = TinkerSmeltery.moltenEmeraldFluid.getBlock();
            if (regMoltenEmeraldBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenEmerald).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenEmerald = regMoltenEmeraldBlock;
            } else
                TinkerSmeltery.moltenEmeraldFluid.setBlock(TinkerSmeltery.moltenEmerald);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenEmeraldFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenEmeraldFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.EMERALD), new ItemStack(Items.bucket)));

        TinkerSmeltery.bloodFluid = new Fluid("blood").setDensity(D).setViscosity(V).setTemperature(T);
        boolean isBloodPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.bloodFluid);
        TinkerSmeltery.blood = new BloodBlock(TinkerSmeltery.bloodFluid, Material.water, "liquid_cow").setBlockName("liquid.blood");
        GameRegistry.registerBlock(TinkerSmeltery.blood, "liquid.blood");
        if (isBloodPreReg) {
            TinkerSmeltery.bloodFluid = FluidRegistry.getFluid("blood");
            Block regBloodBlock = TinkerSmeltery.bloodFluid.getBlock();
            if (regBloodBlock != null) {
                ((TConstructFluid) TinkerSmeltery.blood).suppressOverwritingFluidIcons();
                TinkerSmeltery.blood = regBloodBlock;
            } else
                TinkerSmeltery.bloodFluid.setBlock(TinkerSmeltery.blood);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.bloodFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.bloodFluid, BUCKET_SIZE), new ItemStack(
                    TinkerSmeltery.buckets, 1, FilledBucket.BLOOD), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenNickelFluid = new Fluid("nickel.molten").setDensity(D).setViscosity(V).setTemperature(T);
        boolean isNickelPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenNickelFluid);
        TinkerSmeltery.moltenNickel = new TConstructFluid(TinkerSmeltery.moltenNickelFluid, Material.lava, "liquid_ferrous")
                .setBlockName("fluid.molten.nickel");
        GameRegistry.registerBlock(TinkerSmeltery.moltenNickel, "fluid.molten.nickel");
        if (isNickelPreReg) {
            TinkerSmeltery.moltenNickelFluid = FluidRegistry.getFluid("nickel.molten");
            Block regMoltenNickleBlock = TinkerSmeltery.moltenNickelFluid.getBlock();
            if (regMoltenNickleBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenNickel).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenNickel = regMoltenNickleBlock;
            } else
                TinkerSmeltery.moltenNickelFluid.setBlock(TinkerSmeltery.moltenNickel);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenNickelFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenNickelFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.NICKEL), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenLeadFluid = new Fluid("lead.molten").setDensity(D).setViscosity(V).setTemperature(T);
        boolean isLeadPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenLeadFluid);
        TinkerSmeltery.moltenLead = new TConstructFluid(TinkerSmeltery.moltenLeadFluid, Material.lava, "liquid_lead")
                .setBlockName("fluid.molten.lead");
        GameRegistry.registerBlock(TinkerSmeltery.moltenLead, "fluid.molten.lead");
        if (isLeadPreReg) {
            TinkerSmeltery.moltenLeadFluid = FluidRegistry.getFluid("lead.molten");
            Block regMoltenLeadBlock = TinkerSmeltery.moltenLeadFluid.getBlock();
            if (regMoltenLeadBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenLead).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenLead = regMoltenLeadBlock;
            } else
                TinkerSmeltery.moltenLeadFluid.setBlock(TinkerSmeltery.moltenLead);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenLeadFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenLeadFluid, BUCKET_SIZE), new ItemStack(
                    TinkerSmeltery.buckets, 1, FilledBucket.LEAD), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenSilverFluid = new Fluid("silver.molten").setDensity(D).setViscosity(V).setTemperature(T);
        boolean isSilverPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenSilverFluid);
        TinkerSmeltery.moltenSilver = new TConstructFluid(TinkerSmeltery.moltenSilverFluid, Material.lava, "liquid_silver")
                .setBlockName("fluid.molten.silver");
        GameRegistry.registerBlock(TinkerSmeltery.moltenSilver, "fluid.molten.silver");
        if (isSilverPreReg) {
            TinkerSmeltery.moltenSilverFluid = FluidRegistry.getFluid("silver.molten");
            Block regMoltenSilverBlock = TinkerSmeltery.moltenSilverFluid.getBlock();
            if (regMoltenSilverBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenSilver).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenSilver = regMoltenSilverBlock;
            } else
                TinkerSmeltery.moltenSilverFluid.setBlock(TinkerSmeltery.moltenSilver);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenSilverFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenSilverFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.SILVER), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenShinyFluid = new Fluid("platinum.molten").setDensity(D).setViscosity(V).setTemperature(T);
        boolean isShinyPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenShinyFluid);
        TinkerSmeltery.moltenShiny = new TConstructFluid(TinkerSmeltery.moltenShinyFluid, Material.lava, "liquid_shiny")
                .setBlockName("fluid.molten.shiny");
        GameRegistry.registerBlock(TinkerSmeltery.moltenShiny, "fluid.molten.shiny");
        if (isShinyPreReg) {
            TinkerSmeltery.moltenShinyFluid = FluidRegistry.getFluid("platinum.molten");
            Block regMoltenShinyBlock = TinkerSmeltery.moltenShinyFluid.getBlock();
            if (regMoltenShinyBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenShiny).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenShiny = regMoltenShinyBlock;
            } else
                TinkerSmeltery.moltenShinyFluid.setBlock(TinkerSmeltery.moltenShiny);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenShinyFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenShinyFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.SHINY), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenInvarFluid = new Fluid("invar.molten").setDensity(D).setViscosity(V).setTemperature(T);
        boolean isInvarPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenInvarFluid);
        TinkerSmeltery.moltenInvar = new TConstructFluid(TinkerSmeltery.moltenInvarFluid, Material.lava, "liquid_invar")
                .setBlockName("fluid.molten.invar");
        GameRegistry.registerBlock(TinkerSmeltery.moltenInvar, "fluid.molten.invar");
        if (isInvarPreReg) {
            TinkerSmeltery.moltenInvarFluid = FluidRegistry.getFluid("invar.molten");
            Block regMoltenInvarBlock = TinkerSmeltery.moltenInvarFluid.getBlock();
            if (regMoltenInvarBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenInvar).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenInvar = regMoltenInvarBlock;
            } else
                TinkerSmeltery.moltenInvarFluid.setBlock(TinkerSmeltery.moltenInvar);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenInvarFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenInvarFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.INVAR), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenElectrumFluid = new Fluid("electrum.molten").setDensity(D).setViscosity(V).setTemperature(T);
        boolean isElectrumPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenElectrumFluid);
        TinkerSmeltery.moltenElectrum = new TConstructFluid(TinkerSmeltery.moltenElectrumFluid, Material.lava, "liquid_electrum")
                .setBlockName("fluid.molten.electrum");
        GameRegistry.registerBlock(TinkerSmeltery.moltenElectrum, "fluid.molten.electrum");
        if (isElectrumPreReg) {
            TinkerSmeltery.moltenElectrumFluid = FluidRegistry.getFluid("electrum.molten");
            Block regMoltenElectrumBlock = TinkerSmeltery.moltenElectrumFluid.getBlock();
            if (regMoltenElectrumBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenElectrum).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenElectrum = regMoltenElectrumBlock;
            } else
                TinkerSmeltery.moltenElectrumFluid.setBlock(TinkerSmeltery.moltenElectrum);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenElectrumFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenElectrumFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.ELECTRUM), new ItemStack(Items.bucket)));

        TinkerSmeltery.moltenEnderFluid = new Fluid("ender").setDensity(D).setViscosity(V);
        boolean isEnderPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.moltenEnderFluid);
        TinkerSmeltery.moltenEnder = new TConstructFluid(TinkerSmeltery.moltenEnderFluid, Material.water, "liquid_ender").setBlockName("fluid.ender");
        GameRegistry.registerBlock(TinkerSmeltery.moltenEnder, "fluid.ender");
        if (isEnderPreReg) {
            TinkerSmeltery.moltenEnderFluid = FluidRegistry.getFluid("ender");
            Block regMoltenEnderBlock = TinkerSmeltery.moltenEnderFluid.getBlock();
            if (regMoltenEnderBlock != null) {
                ((TConstructFluid) TinkerSmeltery.moltenEnder).suppressOverwritingFluidIcons();
                TinkerSmeltery.moltenEnder = regMoltenEnderBlock;
            } else
                TinkerSmeltery.moltenEnderFluid.setBlock(TinkerSmeltery.moltenEnder);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.moltenEnderFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.moltenEnderFluid, BUCKET_SIZE),
                    new ItemStack(TinkerSmeltery.buckets, 1, FilledBucket.ENDER), new ItemStack(Items.bucket)));

        // Glue
        TinkerSmeltery.glueFluid = new Fluid("glue").setDensity(D * 2).setViscosity(V).setTemperature(GLUE_TEMP);
        boolean isGluePreReg = !FluidRegistry.registerFluid(TinkerSmeltery.glueFluid);
        TinkerSmeltery.glueFluidBlock = new GlueFluid(TinkerSmeltery.glueFluid, Material.water).setCreativeTab(TConstructRegistry.blockTab)
                .setStepSound(TinkerWorld.slimeStep).setBlockName("liquid.glue");
        GameRegistry.registerBlock(TinkerSmeltery.glueFluidBlock, "liquid.glue");
        if (isGluePreReg) {
            TinkerSmeltery.glueFluid = FluidRegistry.getFluid("glue");
            Block regGlueFluidBlock = TinkerSmeltery.glueFluid.getBlock();
            if (regGlueFluidBlock != null) {
                ((GlueFluid) TinkerSmeltery.glueFluidBlock).suppressOverwritingFluidIcons();
                TinkerSmeltery.glueFluidBlock = regGlueFluidBlock;
            } else
                TinkerSmeltery.glueFluid.setBlock(TinkerSmeltery.glueFluidBlock);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.glueFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.glueFluid, BUCKET_SIZE), new ItemStack(
                    TinkerSmeltery.buckets, 1, FilledBucket.GLUE), new ItemStack(Items.bucket)));

        // PigIron
        TinkerSmeltery.pigIronFluid = new Fluid("pigiron.molten").setDensity(D).setViscosity(V).setTemperature(T);
        boolean isPigIronPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.pigIronFluid);
        TinkerSmeltery.pigIronFluidBlock = new PigIronMoltenBlock(pigIronFluid, Material.water, "liquid_pigiron")
                .setBlockName("fluid.molten.pigiron");
        GameRegistry.registerBlock(TinkerSmeltery.pigIronFluidBlock, "fluid.molten.pigiron");
        if (isPigIronPreReg) {
            TinkerSmeltery.pigIronFluid = FluidRegistry.getFluid("pigiron.molten");
            Block regPigIronFluid = TinkerSmeltery.pigIronFluid.getBlock();
            if (regPigIronFluid != null) {
                ((PigIronMoltenBlock) TinkerSmeltery.pigIronFluidBlock).suppressOverwritingFluidIcons();
                TinkerSmeltery.pigIronFluidBlock = regPigIronFluid;
            } else
                TinkerSmeltery.pigIronFluid.setBlock(TinkerSmeltery.pigIronFluidBlock);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.pigIronFluid, BUCKET_SIZE), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.pigIronFluid, BUCKET_SIZE), new ItemStack(
                    TinkerSmeltery.buckets, 1, FilledBucket.PIG_IRON), new ItemStack(Items.bucket)));

        TinkerSmeltery.fluids = new Fluid[] { TinkerSmeltery.moltenIronFluid, TinkerSmeltery.moltenGoldFluid, TinkerSmeltery.moltenCopperFluid,
                TinkerSmeltery.moltenTinFluid, TinkerSmeltery.moltenAluminumFluid, TinkerSmeltery.moltenCobaltFluid,
                TinkerSmeltery.moltenArditeFluid, TinkerSmeltery.moltenBronzeFluid, TinkerSmeltery.moltenAlubrassFluid,
                TinkerSmeltery.moltenManyullynFluid, TinkerSmeltery.moltenAlumiteFluid, TinkerSmeltery.moltenObsidianFluid,
                TinkerSmeltery.moltenSteelFluid, TinkerSmeltery.moltenGlassFluid, TinkerSmeltery.moltenStoneFluid, TinkerSmeltery.moltenEmeraldFluid,
                TinkerSmeltery.bloodFluid, TinkerSmeltery.moltenNickelFluid, TinkerSmeltery.moltenLeadFluid, TinkerSmeltery.moltenSilverFluid,
                TinkerSmeltery.moltenShinyFluid, TinkerSmeltery.moltenInvarFluid, TinkerSmeltery.moltenElectrumFluid,
                TinkerSmeltery.moltenEnderFluid, TinkerWorld.blueSlimeFluid, TinkerSmeltery.glueFluid, TinkerSmeltery.pigIronFluid };

        TinkerSmeltery.speedBlock = new SpeedBlock().setBlockName("SpeedBlock");

        // Glass
        TinkerSmeltery.clearGlass = new GlassBlockConnected("clear", false).setBlockName("GlassBlock");
        TinkerSmeltery.clearGlass.stepSound = Block.soundTypeGlass;
        TinkerSmeltery.glassPane = new GlassPaneConnected("clear", false);
        TinkerSmeltery.stainedGlassClear = new GlassBlockConnectedMeta("stained", true, "white", "orange", "magenta", "light_blue", "yellow", "lime",
                "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black").setBlockName("GlassBlock.StainedClear");
        TinkerSmeltery.stainedGlassClear.stepSound = Block.soundTypeGlass;
        TinkerSmeltery.stainedGlassClearPane = new GlassPaneStained();

        GameRegistry.registerBlock(TinkerSmeltery.searedSlab, SearedSlabItem.class, "SearedSlab");
        GameRegistry.registerBlock(TinkerSmeltery.speedSlab, SpeedSlabItem.class, "SpeedSlab");

        GameRegistry.registerBlock(TinkerSmeltery.glueBlock, "GlueBlock");
        OreDictionary.registerOre("blockRubber", new ItemStack(TinkerSmeltery.glueBlock));

        // Smeltery stuff
        GameRegistry.registerBlock(TinkerSmeltery.smeltery, SmelteryItemBlock.class, "Smeltery");
        GameRegistry.registerBlock(TinkerSmeltery.smelteryNether, SmelteryItemBlock.class, "SmelteryNether");
        if (PHConstruct.newSmeltery) {
            GameRegistry.registerTileEntity(AdaptiveSmelteryLogic.class, "TConstruct.Smeltery");
            GameRegistry.registerTileEntity(AdaptiveDrainLogic.class, "TConstruct.SmelteryDrain");
        } else {
            GameRegistry.registerTileEntity(SmelteryLogic.class, "TConstruct.Smeltery");
            GameRegistry.registerTileEntity(SmelteryDrainLogic.class, "TConstruct.SmelteryDrain");
        }
        GameRegistry.registerTileEntity(MultiServantLogic.class, "TConstruct.Servants");
        GameRegistry.registerBlock(TinkerSmeltery.lavaTank, LavaTankItemBlock.class, "LavaTank");
        GameRegistry.registerBlock(TinkerSmeltery.lavaTankNether, LavaTankItemBlock.class, "LavaTankNether");
        GameRegistry.registerTileEntity(LavaTankLogic.class, "TConstruct.LavaTank");

        GameRegistry.registerBlock(TinkerSmeltery.searedBlock, SearedTableItemBlock.class, "SearedBlock");
        GameRegistry.registerBlock(TinkerSmeltery.searedBlockNether, SearedTableItemBlock.class, "SearedBlockNether");
        GameRegistry.registerTileEntity(CastingTableLogic.class, "CastingTable");
        GameRegistry.registerTileEntity(FaucetLogic.class, "Faucet");
        GameRegistry.registerTileEntity(CastingBasinLogic.class, "CastingBasin");

        GameRegistry.registerBlock(TinkerSmeltery.castingChannel, CastingChannelItem.class, "CastingChannel");
        GameRegistry.registerTileEntity(CastingChannelLogic.class, "CastingChannel");

        GameRegistry.registerBlock(TinkerSmeltery.tankAir, "TankAir");
        GameRegistry.registerTileEntity(TankAirLogic.class, "tconstruct.tank.air");

        GameRegistry.registerBlock(TinkerSmeltery.speedBlock, SpeedBlockItem.class, "SpeedBlock");

        // Glass
        GameRegistry.registerBlock(TinkerSmeltery.clearGlass, GlassBlockItem.class, "GlassBlock");
        GameRegistry.registerBlock(TinkerSmeltery.glassPane, GlassPaneItem.class, "GlassPane");
        GameRegistry.registerBlock(TinkerSmeltery.stainedGlassClear, StainedGlassClearItem.class, "GlassBlock.StainedClear");
        GameRegistry.registerBlock(TinkerSmeltery.stainedGlassClearPane, StainedGlassClearPaneItem.class, "GlassPaneClearStained");

        // Items
        TinkerSmeltery.metalPattern = new MetalPattern("cast_", "materials/").setUnlocalizedName("tconstruct.MetalPattern");
        GameRegistry.registerItem(TinkerSmeltery.metalPattern, "metalPattern");
        TConstructRegistry.addItemToDirectory("metalPattern", TinkerSmeltery.metalPattern);
        String[] patternTypes = { "ingot", "toolRod", "pickaxeHead", "shovelHead", "hatchetHead", "swordBlade", "wideGuard", "handGuard", "crossbar",
                "binding", "frypanHead", "signHead", "knifeBlade", "chiselHead", "toughRod", "toughBinding", "largePlate", "broadAxeHead",
                "scytheHead", "excavatorHead", "largeBlade", "hammerHead", "fullGuard" };
        for (int i = 0; i < patternTypes.length; i++) {
            TConstructRegistry.addItemStackToDirectory(patternTypes[i] + "Cast", new ItemStack(TinkerSmeltery.metalPattern, 1, i));
        }
    }

    private Fluid defineMoltenGold() {
        Fluid fluid = new Fluid("gold.molten");
        fluid.setLuminosity(L).setDensity(D).setViscosity(V).setTemperature(T);
        return fluid;
    }

    @Handler
    public void init(FMLInitializationEvent event) {
        proxy.initialize();
        TinkerSmeltery.fluidBlocks = new Block[] { TinkerSmeltery.moltenIron, TinkerSmeltery.moltenGold, TinkerSmeltery.moltenCopper,
                TinkerSmeltery.moltenTin, TinkerSmeltery.moltenAluminum, TinkerSmeltery.moltenCobalt, TinkerSmeltery.moltenArdite,
                TinkerSmeltery.moltenBronze, TinkerSmeltery.moltenAlubrass, TinkerSmeltery.moltenManyullyn, TinkerSmeltery.moltenAlumite,
                TinkerSmeltery.moltenObsidian, TinkerSmeltery.moltenSteel, TinkerSmeltery.moltenGlass, TinkerSmeltery.moltenStone,
                TinkerSmeltery.moltenEmerald, TinkerSmeltery.blood, TinkerSmeltery.moltenNickel, TinkerSmeltery.moltenLead,
                TinkerSmeltery.moltenSilver, TinkerSmeltery.moltenShiny, TinkerSmeltery.moltenInvar, TinkerSmeltery.moltenElectrum,
                TinkerSmeltery.moltenEnder, TinkerWorld.slimePool, TinkerSmeltery.glueFluidBlock, TinkerSmeltery.pigIronFluidBlock };

        FluidType.registerFluidType("Water", Blocks.snow, NO_META, WATER_HEAT_USE, FluidRegistry.getFluid("water"), false);
        FluidType.registerFluidType("Iron", Blocks.iron_block, NO_META, IRON_HEAT_USE, TinkerSmeltery.moltenIronFluid, true);
        FluidType.registerFluidType("Gold", Blocks.gold_block, NO_META, GOLD_HEAT_USE, TinkerSmeltery.moltenGoldFluid, false);
        FluidType.registerFluidType("Tin", OreCoreItems.tinBlock, NO_META, TIN_HEAT_USE, TinkerSmeltery.moltenTinFluid, false);
        FluidType.registerFluidType("Copper", OreCoreItems.copperBlock, NO_META, COPPER_HEAT_USE, TinkerSmeltery.moltenCopperFluid, true);
        FluidType.registerFluidType("Aluminum", OreCoreItems.aluminumBlock, NO_META, ALUMINUM_HEAT_USE, TinkerSmeltery.moltenAluminumFluid, false);
        FluidType.registerFluidType("NaturalAluminum", OreCoreItems.bauxite, NO_META, ALUMINUM_HEAT_USE, TinkerSmeltery.moltenAluminumFluid, false);
        FluidType.registerFluidType("Cobalt", TinkerWorld.metalBlock, TMetalBlock.COBALT, COBALT_HEAT_USE, TinkerSmeltery.moltenCobaltFluid, true);
        FluidType.registerFluidType("Ardite", TinkerWorld.metalBlock, TMetalBlock.ARDITE, ARDITE_HEAT_USE, TinkerSmeltery.moltenArditeFluid, true);
        FluidType.registerFluidType("AluminumBrass", OreCoreItems.brassBlock, NO_META, BRASS_HEAT_USE, TinkerSmeltery.moltenAlubrassFluid, false);
        FluidType.registerFluidType("Alumite", TinkerWorld.metalBlock, TMetalBlock.ALUMITE, ALUMITE_HEAT_USE, TinkerSmeltery.moltenAlumiteFluid, true);
        FluidType.registerFluidType("Manyullyn", TinkerWorld.metalBlock, TMetalBlock.MANYULLYN, MANYULLYN_HEAT_USE, TinkerSmeltery.moltenManyullynFluid, true);
        FluidType.registerFluidType("Bronze", OreCoreItems.bronzeBlock, NO_META, BRONZE_HEAT_USE, TinkerSmeltery.moltenBronzeFluid, true);
        FluidType.registerFluidType("Steel", OreCoreItems.steelBlock, NO_META, STEEL_HEAT_USE, TinkerSmeltery.moltenSteelFluid, true);
        FluidType.registerFluidType("Nickel", OreCoreItems.nickelBlock, NO_META, NICKEL_HEAT_USE, TinkerSmeltery.moltenNickelFluid, false);
        FluidType.registerFluidType("Lead", OreCoreItems.leadBlock, NO_META, LEAD_HEAT_USE, TinkerSmeltery.moltenLeadFluid, false);
        FluidType.registerFluidType("Silver", OreCoreItems.silverBlock, NO_META, SILVER_HEAT_USE, TinkerSmeltery.moltenSilverFluid, false);
        FluidType.registerFluidType("Platinum", TinkerWorld.metalBlock, TMetalBlock.PLATINUM, PLATINUM_HEAT_USE, TinkerSmeltery.moltenShinyFluid, false);
        FluidType.registerFluidType("Invar", TinkerWorld.metalBlock, TMetalBlock.INVAR, INVAR_HEAT_USE, TinkerSmeltery.moltenInvarFluid, false);
        FluidType.registerFluidType("Electrum", TinkerWorld.metalBlock, NO_META, ELECTRUM_HEAT_USE, TinkerSmeltery.moltenElectrumFluid, false);
        FluidType.registerFluidType("Obsidian", Blocks.obsidian, NO_META, OBSIDIAN_HEAT_USE, TinkerSmeltery.moltenObsidianFluid, true);
        FluidType.registerFluidType("Ender", TinkerWorld.metalBlock, TMetalBlock.ENDER, ENDER_HEAT_USE, TinkerSmeltery.moltenEnderFluid, false);
        FluidType.registerFluidType("Glass", Blocks.sand, NO_META, SAND_HEAT_USE, TinkerSmeltery.moltenGlassFluid, false);
        FluidType.registerFluidType("Stone", Blocks.stone, NO_META, STONE_HEAT_USE, TinkerSmeltery.moltenStoneFluid, true);
        FluidType.registerFluidType("Emerald", Blocks.emerald_block, NO_META, EMERALD_HEAT_USE, TinkerSmeltery.moltenEmeraldFluid, false);
        FluidType.registerFluidType("PigIron", TinkerWorld.meatBlock, NO_META, PIG_IRON_HEAT_USE, TinkerSmeltery.pigIronFluid, true);
        FluidType.registerFluidType("Glue", TinkerSmeltery.glueBlock, NO_META, GLUE_HEAT_USE, TinkerSmeltery.glueFluid, false);

        craftingTableRecipes();
        addRecipesForSmeltery();
        addRecipesForTableCasting();
        addRecipesForBasinCasting();
    }

    @Handler
    public void postInit(FMLPostInitializationEvent evt) {
        addOreDictionarySmelteryRecipes();
        modIntegration();
    }

    private void craftingTableRecipes() {

        String[] patSurround = { "###", "#m#", "###" };

        // stained Glass Recipes
        String[] dyeTypes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink",
                "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite" };
        String color = "";
        for (int i = 0; i < 16; i++) {
            color = dyeTypes[15 - i];
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClear, FULL_GRID - 1, i), patSurround, 'm', color, '#',
                    TinkerSmeltery.clearGlass));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClear, 1, i), color, TinkerSmeltery.clearGlass));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClear, FULL_GRID - 1, i), patSurround, 'm', color, '#',
                    new ItemStack(TinkerSmeltery.stainedGlassClear, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClear, 1, i), color, new ItemStack(
                    TinkerSmeltery.stainedGlassClear, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClearPane, FULL_GRID - 1, i), patSurround, 'm', color, '#',
                    TinkerSmeltery.glassPane));
            GameRegistry
                    .addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClearPane, 1, i), color, TinkerSmeltery.glassPane));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClearPane, FULL_GRID - 1, i), patSurround, 'm', color, '#',
                    new ItemStack(TinkerSmeltery.stainedGlassClearPane, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClearPane, 1, i), color, new ItemStack(
                    TinkerSmeltery.stainedGlassClearPane, 1, Short.MAX_VALUE)));
        }

        // Glass Recipes
        GameRegistry.addRecipe(new ItemStack(Items.glass_bottle, 3), new Object[] { "# #", " # ", '#', TinkerSmeltery.clearGlass });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.daylight_detector), new Object[] { "GGG", "QQQ", "WWW", 'G', "blockGlass",
                'Q', Items.quartz, 'W', "slabWood" }));
        GameRegistry.addRecipe(new ItemStack(Blocks.beacon, 1), new Object[] { "GGG", "GSG", "OOO", 'G', TinkerSmeltery.clearGlass, 'S',
                Items.nether_star, 'O', Blocks.obsidian });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.glassPane, 16, 0), "GGG", "GGG", 'G', TinkerSmeltery.clearGlass));

        // Smeltery Components Recipes
        ItemStack searedBrick = new ItemStack(TinkerTools.materials, 1, MaterialItem.SEARED_BRICK);
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smeltery, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); // Controller
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smeltery, 1, 1), "b b", "b b", "b b", 'b', searedBrick); // Drain
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smeltery, 1, 2), "bb", "bb", 'b', searedBrick); // Bricks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTank, 1, 0), patSurround, '#', searedBrick, 'm', "blockGlass")); // Tank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTank, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g',
                "blockGlass")); // Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTank, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g',
                "blockGlass")); // Window
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlock, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); // Table
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlock, 1, 1), "b b", " b ", 'b', searedBrick); // Faucet
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlock, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); // Basin
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); // Channel

        searedBrick = new ItemStack(TinkerTools.materials, 1, MaterialItem.SEARED_BRICK);
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smelteryNether, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); // Controller
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smelteryNether, 1, 1), "b b", "b b", "b b", 'b', searedBrick); // Drain
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smelteryNether, 1, 2), "bb", "bb", 'b', searedBrick); // Bricks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTankNether, 1, 0), patSurround, '#', searedBrick, 'm',
                "blockGlass")); // Tank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTankNether, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g',
                "blockGlass")); // Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTankNether, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g',
                "blockGlass")); // Window
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlockNether, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); // Table
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlockNether, 1, 1), "b b", " b ", 'b', searedBrick); // Faucet
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlockNether, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); // Basin
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); // Channel

        // Slab Smeltery Components Recipes
        for (int i = 0; i < 7; i++) {
            GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.speedSlab, 6, i), "bbb", 'b', new ItemStack(TinkerSmeltery.speedBlock, 1, i));
        }
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 0), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 1), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 4));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 2), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 5));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 3), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 6));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 4), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 8));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 5), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 9));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 6), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 10));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 7), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 11));
    }

    public void addOreDictionarySmelteryRecipes() {
        List<FluidType> exceptions = Arrays.asList(new FluidType[] { FluidType.getFluidType("Water"), FluidType.getFluidType("Stone"),
                FluidType.getFluidType("Emerald"), FluidType.getFluidType("Ender"), FluidType.getFluidType("Glass"), FluidType.getFluidType("Slime"),
                FluidType.getFluidType("Obsidian") });
        Iterator<?> iter = FluidType.fluidTypes.entrySet().iterator();
        while (iter.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry pairs = (Map.Entry) iter.next();
            FluidType ft = (FluidType) pairs.getValue();
            if (exceptions.contains(ft))
                continue;
            String fluidTypeName = (String) pairs.getKey();

            // Nuggets
            Smeltery.addDictionaryMelting("nugget" + fluidTypeName, ft, NUGGET_HEAT_USE, TConstruct.nuggetLiquidValue);

            // Ingots, Dust
            registerIngotCasting(ft, "ingot" + fluidTypeName);
            Smeltery.addDictionaryMelting("ingot" + fluidTypeName, ft, INGOT_HEAT_USE, TConstruct.ingotLiquidValue);
            Smeltery.addDictionaryMelting("dust" + fluidTypeName, ft, DUST_HEAT_USE, TConstruct.ingotLiquidValue);

            // Factorization support
            Smeltery.addDictionaryMelting("crystalline" + fluidTypeName, ft, INGOT_HEAT_USE, TConstruct.ingotLiquidValue);

            // Ores
            Smeltery.addDictionaryMelting("ore" + fluidTypeName, ft, 0, (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre));

            // NetherOres support
            Smeltery.addDictionaryMelting("oreNether" + fluidTypeName, ft, NETHER_ORE_HEAT_USE, NETHER_ORE_FLUID_OUTPUT);

            // DenseOres support
            Smeltery.addDictionaryMelting("denseore" + fluidTypeName, ft, DENSE_ORE_HEAT_USE, DENSE_ORE_FLUID_OUTPUT);

            // DenseOres support
            Smeltery.addDictionaryMelting("orePoor" + fluidTypeName, ft, POOR_ORE_HEAT_USE, POOR_ORE_FLUID_OUTPUT);

            // Blocks
            registerBlockCasting(ft, "block" + fluidTypeName);
            Smeltery.addDictionaryMelting("block" + fluidTypeName, ft, BLOCK_HEAT_USE, TConstruct.blockLiquidValue);

            if (ft.isToolpart) {
                TinkerTools.registerPatternMaterial("ingot" + fluidTypeName, 2, fluidTypeName);
                TinkerTools.registerPatternMaterial("block" + fluidTypeName, 2 * FULL_GRID, fluidTypeName);
            }
        }
        // Obsidian, different dust amount
        addObsidianSmelting();

        // Compressed materials. max 4x because it's too much otherwise.
        for (int i = 1; i <= MAX_NUM_COMPRESSED_MATERIALS_FOR_SMELTERY; i++) {
            Smeltery.addDictionaryMelting("compressedCobblestone" + i + "x", FluidType.getFluidType("Stone"), 0, TConstruct.stoneLiquidValue
                    * (int) Math.pow(FULL_GRID, i));
        }
        Smeltery.addDictionaryMelting("compressedSand1x", FluidType.getFluidType("Glass"), SAND_HEAT_USE, FluidContainerRegistry.BUCKET_VOLUME * FULL_GRID);
        Smeltery.addDictionaryMelting("compressedSand2x", FluidType.getFluidType("Glass"), SAND_HEAT_USE, FluidContainerRegistry.BUCKET_VOLUME * FULL_GRID * FULL_GRID);
    }

    private void addObsidianSmelting() {
        FluidType ft = FluidType.getFluidType("Obsidian");
        String fluidTypeName = "Obsidian";
        Smeltery.addDictionaryMelting("nugget" + fluidTypeName, ft, NUGGET_HEAT_USE, TConstruct.nuggetLiquidValue);

        // Ingots, Dust
        registerIngotCasting(ft, "ingot" + fluidTypeName);
        Smeltery.addDictionaryMelting("ingot" + fluidTypeName, ft, INGOT_HEAT_USE, TConstruct.ingotLiquidValue);
        Smeltery.addDictionaryMelting("dust" + fluidTypeName, ft, DUST_HEAT_USE, OBSIDIAN_DUST_FLUID_OUTPUT);

        // Factorization support
        Smeltery.addDictionaryMelting("crystalline" + fluidTypeName, ft, INGOT_HEAT_USE, TConstruct.ingotLiquidValue);

        // Ores
        Smeltery.addDictionaryMelting("ore" + fluidTypeName, ft, 0, (TConstruct.ingotLiquidValue * (int) PHConstruct.ingotsPerOre));

        // Poor ores
        Smeltery.addDictionaryMelting("orePoor" + fluidTypeName, ft, 0, OBSIDIAN_POOR_ORE_FLUID_OUTPUT);

        // NetherOres support
        Smeltery.addDictionaryMelting("oreNether" + fluidTypeName, ft, NETHER_ORE_HEAT_USE, NETHER_ORE_FLUID_OUTPUT);

        // Blocks
        Smeltery.addDictionaryMelting("block" + fluidTypeName, ft, BLOCK_HEAT_USE, TConstruct.blockLiquidValue);

        if (ft.isToolpart) {
            TinkerTools.registerPatternMaterial("ingot" + fluidTypeName, 2, fluidTypeName);
            TinkerTools.registerPatternMaterial("block" + fluidTypeName, 2 * FULL_GRID, fluidTypeName);
        }
    }

    private void addRecipesForTableCasting() {
        /* Smeltery */
        ItemStack ingotcast = new ItemStack(TinkerSmeltery.metalPattern, 1, MetalPattern.INGOT);
        ItemStack gemcast = new ItemStack(TinkerSmeltery.metalPattern, 1, MetalPattern.GEM);
        LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
        // Blank
        tableCasting.addCastingRecipe(new ItemStack(TinkerTools.blankPattern, 1, 1), new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), CAST_DELAY);
        tableCasting.addCastingRecipe(new ItemStack(TinkerTools.blankPattern, 1, 2), new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), CAST_DELAY);
        tableCasting.addCastingRecipe(gemcast, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(Items.emerald), CAST_DELAY);
        tableCasting.addCastingRecipe(gemcast, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(Items.emerald), CAST_DELAY);

        // Ingots
        tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, MaterialItem.SEARED_BRICK), new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.ingotLiquidValue / 4), ingotcast, CAST_DELAY); // stone

        // Misc
        tableCasting.addCastingRecipe(new ItemStack(Items.emerald), new FluidStack(TinkerSmeltery.moltenEmeraldFluid, EMERALD_FLUID_REQ), gemcast, CAST_DELAY);
        tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, MaterialItem.GLUE_BALL), new FluidStack(TinkerSmeltery.glueFluid, TConstruct.ingotLiquidValue), null, METAL_MELT_DELAY);
        tableCasting.addCastingRecipe(new ItemStack(TinkerWorld.strangeFood, 1, 1), new FluidStack(TinkerSmeltery.bloodFluid, 160), null, METAL_MELT_DELAY);

        // Buckets
        ItemStack bucket = new ItemStack(Items.bucket);

        Item thermalBucket = GameRegistry.findItem("ThermalFoundation", "bucket");

        for (int sc = 0; sc < 26; sc++) {
            if (TinkerSmeltery.fluids[sc] != null) {
                // TE support
                if (fluids[sc] == TinkerSmeltery.moltenEnderFluid && thermalBucket != null)
                    // bucket of resonant ender instead of liquified ender
                    tableCasting.addCastingRecipe(
                            new ItemStack(thermalBucket, 1, 2), new FluidStack(TinkerSmeltery.fluids[sc], FluidContainerRegistry.BUCKET_VOLUME), bucket, true, BUCKET_FILL_DELAY);
                else
                    tableCasting.addCastingRecipe(
                            new ItemStack(TinkerSmeltery.buckets, 1, sc), new FluidStack(TinkerSmeltery.fluids[sc], FluidContainerRegistry.BUCKET_VOLUME), bucket, true, BUCKET_FILL_DELAY);
            }
        }

        // water and lava bucket
        tableCasting.addCastingRecipe(
                new ItemStack(Items.water_bucket), new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, BUCKET_FILL_DELAY);
        tableCasting.addCastingRecipe(
                new ItemStack(Items.lava_bucket), new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, BUCKET_FILL_DELAY);

        // Clear glass pane casting
        tableCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.glassPane), new FluidStack(TinkerSmeltery.moltenGlassFluid, 250), null, CAST_DELAY);

        // Metal toolpart casting
        TinkerSmeltery.liquids = new FluidStack[] { new FluidStack(TinkerSmeltery.moltenIronFluid, 1),
                new FluidStack(TinkerSmeltery.moltenCopperFluid, 1), new FluidStack(TinkerSmeltery.moltenCobaltFluid, 1),
                new FluidStack(TinkerSmeltery.moltenArditeFluid, 1), new FluidStack(TinkerSmeltery.moltenManyullynFluid, 1),
                new FluidStack(TinkerSmeltery.moltenBronzeFluid, 1), new FluidStack(TinkerSmeltery.moltenAlumiteFluid, 1),
                new FluidStack(TinkerSmeltery.moltenObsidianFluid, 1), new FluidStack(TinkerSmeltery.moltenSteelFluid, 1),
                new FluidStack(TinkerSmeltery.pigIronFluid, 1) };
        int[] liquidDamage = new int[] { 2, 13, 10, 11, 12, 14, 15, 6, 16, 18 }; // ItemStack damage value
        int fluidAmount = 0;
        Fluid fs = null;

        for (int iter = 0; iter < TinkerTools.patternOutputs.length; iter++) {
            if (TinkerTools.patternOutputs[iter] != null) {
                ItemStack cast = new ItemStack(TinkerSmeltery.metalPattern, 1, iter + 1);

                tableCasting.addCastingRecipe(cast, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(
                        TinkerTools.patternOutputs[iter], 1, Short.MAX_VALUE), false, METAL_MELT_DELAY);
                tableCasting.addCastingRecipe(cast, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(
                        TinkerTools.patternOutputs[iter], 1, Short.MAX_VALUE), false, METAL_MELT_DELAY);

                for (int iterTwo = 0; iterTwo < TinkerSmeltery.liquids.length; iterTwo++) {
                    fs = TinkerSmeltery.liquids[iterTwo].getFluid();
                    fluidAmount = ((IPattern) TinkerSmeltery.metalPattern).getPatternCost(cast) * TConstruct.ingotLiquidValue / 2;
                    ItemStack metalCast = new ItemStack(TinkerTools.patternOutputs[iter], 1, liquidDamage[iterTwo]);
                    tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, METAL_MELT_DELAY);
                    Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                }
            }
        }

        tableCasting.addCastingRecipe(new ItemStack(Items.ender_pearl), new FluidStack(TinkerSmeltery.moltenEnderFluid, ENDER_PEARL_FLUID_REQ),
                new ItemStack(TinkerSmeltery.metalPattern, 1, MetalPattern.FRYING_PAN), METAL_MELT_DELAY);
        tableCasting.addCastingRecipe(new ItemStack(Items.ender_pearl), new FluidStack(TinkerSmeltery.moltenEnderFluid, ENDER_PEARL_FLUID_REQ),
                new ItemStack(TinkerSmeltery.metalPattern, 1, MetalPattern.GEM), METAL_MELT_DELAY);

        ItemStack[] ingotShapes = { new ItemStack(Items.brick), new ItemStack(Items.netherbrick), new ItemStack(TinkerTools.materials, 1, MaterialItem.SEARED_BRICK),
                new ItemStack(TinkerTools.materials, 1, MaterialItem.SEARED_BRICK) };
        for (int i = 0; i < ingotShapes.length; i++) {
            tableCasting.addCastingRecipe(ingotcast, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), ingotShapes[i],
                    false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(ingotcast, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), ingotShapes[i],
                    false, METAL_MELT_DELAY);
        }

        ItemStack fullguardCast = new ItemStack(TinkerSmeltery.metalPattern, 1, MaterialItem.COBALT_NUGGET);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(
                TinkerTools.fullGuard, 1, Short.MAX_VALUE), false, METAL_MELT_DELAY);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(
                TinkerTools.fullGuard, 1, Short.MAX_VALUE), false, METAL_MELT_DELAY);

        // Golden Food Stuff
        // 9 gold nuggets
        FluidStack goldAmount = new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.nuggetLiquidValue * (FULL_GRID - 1));
        tableCasting.addCastingRecipe(new ItemStack(Items.golden_carrot, 1), goldAmount, new ItemStack(Items.carrot), true, METAL_MELT_DELAY);
        tableCasting.addCastingRecipe(new ItemStack(Items.speckled_melon, 1), goldAmount, new ItemStack(Items.melon), true, METAL_MELT_DELAY);
        // 8 gold ingots
        goldAmount = new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * (FULL_GRID - 1));
        tableCasting.addCastingRecipe(new ItemStack(Items.golden_apple, 1), goldAmount, new ItemStack(Items.apple), true, METAL_MELT_DELAY);
        if (TinkerWorld.goldHead != null)
            tableCasting.addCastingRecipe(new ItemStack(TinkerWorld.goldHead), goldAmount, new ItemStack(Items.skull, 1, 3), true, METAL_MELT_DELAY);

        // Ensure TConstruct and OreCore ingots are always first. Otherwise you might get
        // ingots from other mods from casting
        if (PHConstruct.tconComesFirst && TinkerTools.materials != null) {
            tableCasting.addCastingRecipe(
                    new ItemStack(OreCoreItems.copperIngot), new FluidStack(moltenCopperFluid, TConstruct.ingotLiquidValue), ingotcast, false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(
                    new ItemStack(OreCoreItems.tinIngot), new FluidStack(moltenTinFluid, TConstruct.ingotLiquidValue), ingotcast, false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(
                    new ItemStack(OreCoreItems.aluminumIngot), new FluidStack(moltenAluminumFluid, TConstruct.ingotLiquidValue), ingotcast, false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(
                    new ItemStack(TinkerTools.materials, 1, MaterialItem.COBALT_INGOT), new FluidStack(moltenCobaltFluid, TConstruct.ingotLiquidValue), ingotcast, false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(
                    new ItemStack(TinkerTools.materials, 1, MaterialItem.ARDITE_INGOT), new FluidStack(moltenArditeFluid, TConstruct.ingotLiquidValue), ingotcast, false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(
                    new ItemStack(TinkerTools.materials, 1, MaterialItem.MANYULLYN_INGOT), new FluidStack(moltenManyullynFluid, TConstruct.ingotLiquidValue), ingotcast, false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(
                    new ItemStack(OreCoreItems.bronzeIngot, 1), new FluidStack(moltenBronzeFluid, TConstruct.ingotLiquidValue), ingotcast, false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(
                    new ItemStack(TinkerTools.materials, 1, MaterialItem.ALUBRASS_INGOT), new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), ingotcast, false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(
                    new ItemStack(TinkerTools.materials, 1, MaterialItem.ALUMITE_INGOT), new FluidStack(moltenAlumiteFluid, TConstruct.ingotLiquidValue), ingotcast, false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(
                    new ItemStack(OreCoreItems.steelIngot), new FluidStack(moltenSteelFluid, TConstruct.ingotLiquidValue), ingotcast, false, METAL_MELT_DELAY);
        }
    }

    protected void addRecipesForBasinCasting() {
        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
        // Block Casting
        basinCasting.addCastingRecipe(new ItemStack(Blocks.iron_block), new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.blockLiquidValue),
                null, true, BASIN_CASTING_MELT_DELAY); // Iron
        basinCasting.addCastingRecipe(new ItemStack(Blocks.gold_block), new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.blockLiquidValue),
                null, true, BASIN_CASTING_MELT_DELAY); // gold
        if (PHConstruct.tconComesFirst) {
            basinCasting.addCastingRecipe(new ItemStack(OreCoreItems.copperBlock, 1),
                    new FluidStack(TinkerSmeltery.moltenCopperFluid, TConstruct.blockLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // copper
            basinCasting.addCastingRecipe(new ItemStack(OreCoreItems.tinBlock, 1),
                    new FluidStack(TinkerSmeltery.moltenTinFluid, TConstruct.blockLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // tin
            basinCasting.addCastingRecipe(new ItemStack(OreCoreItems.aluminumBlock, 1),
                    new FluidStack(TinkerSmeltery.moltenAluminumFluid, TConstruct.blockLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // aluminum
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, MetalItemBlock.COBALT),
                    new FluidStack(TinkerSmeltery.moltenCobaltFluid, TConstruct.blockLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // cobalt
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 1),
                    new FluidStack(TinkerSmeltery.moltenArditeFluid, TConstruct.blockLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // ardite
            basinCasting.addCastingRecipe(new ItemStack(OreCoreItems.bronzeBlock, 1),
                    new FluidStack(TinkerSmeltery.moltenBronzeFluid, TConstruct.blockLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // bronze
            basinCasting.addCastingRecipe(new ItemStack(OreCoreItems.brassBlock, 1),
                    new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.blockLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // albrass
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, MetalItemBlock.MANYULLYN),
                    new FluidStack(TinkerSmeltery.moltenManyullynFluid, TConstruct.blockLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // manyullyn
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, MetalItemBlock.ALUMITE),
                    new FluidStack(TinkerSmeltery.moltenAlumiteFluid, TConstruct.blockLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // alumite
            basinCasting.addCastingRecipe(new ItemStack(OreCoreItems.steelBlock, 1),
                    new FluidStack(TinkerSmeltery.moltenSteelFluid, TConstruct.blockLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // steel
        }
        basinCasting.addCastingRecipe(new ItemStack(Blocks.obsidian),
                new FluidStack(TinkerSmeltery.moltenObsidianFluid, TConstruct.oreLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // obsidian
        basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.clearGlass, 1, 0),
                new FluidStack(TinkerSmeltery.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME), null, true, BASIN_CASTING_MELT_DELAY); // glass
        basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.smeltery, 1, SmelteryBlock.DRAIN_OUT),
                new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.ingotLiquidValue), null, true, BASIN_CASTING_MELT_DELAY); // seared
        // stone
        basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.smeltery, 1, SmelteryBlock.DRAIN_BASIN),
                new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.chunkLiquidValue), new ItemStack(Blocks.cobblestone), true, BASIN_CASTING_MELT_DELAY);
        basinCasting.addCastingRecipe(new ItemStack(Blocks.emerald_block),
                new FluidStack(TinkerSmeltery.moltenEmeraldFluid, EMERALD_FLUID_REQ * FULL_GRID), null, true, BASIN_CASTING_MELT_DELAY); // emerald
        basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.speedBlock, 1, SpeedBlock.BROWNSTONE_ROUGH),
                new FluidStack(TinkerSmeltery.moltenTinFluid, TConstruct.nuggetLiquidValue), new ItemStack(Blocks.gravel), true, BASIN_CASTING_MELT_DELAY); // brownstone
        if (PHConstruct.craftEndstone) {
            basinCasting.addCastingRecipe(new ItemStack(Blocks.end_stone),
                    new FluidStack(TinkerSmeltery.moltenEnderFluid, 50), new ItemStack(Blocks.obsidian), true, BASIN_CASTING_MELT_DELAY);
            basinCasting.addCastingRecipe(new ItemStack(Blocks.end_stone),
                    new FluidStack(TinkerSmeltery.moltenEnderFluid, 250), new ItemStack(Blocks.sandstone), true, BASIN_CASTING_MELT_DELAY);
        }
        basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, MetalItemBlock.ENDER),
                new FluidStack(TinkerSmeltery.moltenEnderFluid, BUCKET_SIZE), null, true, BASIN_CASTING_MELT_DELAY); // ender
        basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.glueBlock), new FluidStack(TinkerSmeltery.glueFluid, TConstruct.blockLiquidValue),
                null, true, BASIN_CASTING_MELT_DELAY); // glue

        // basinCasting.addCastingRecipe(new ItemStack(slimeGel, 1, 0), new
        // FluidStack(blueSlimeFluid, FluidContainerRegistry.BUCKET_VOLUME),
        // null, true, 100);

        // Armor casts
        /*
         * FluidRenderProperties frp = new
         * FluidRenderProperties(Applications.BASIN.minHeight, 0.65F,
         * Applications.BASIN); FluidStack aluFlu = new
         * FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue *
         * 10); FluidStack gloFlu = new FluidStack(TRepo.moltenGoldFluid,
         * TConstruct.ingotLiquidValue * 10); ItemStack[] armor = { new
         * ItemStack(helmetWood), new ItemStack(chestplateWood), new
         * ItemStack(leggingsWood), new ItemStack(bootsWood) }; for (int sc = 0;
         * sc < armor.length; sc++) { basinCasting.addCastingRecipe(new
         * ItemStack(armorPattern, 1, sc), aluFlu, armor[sc], 50, frp);
         * basinCasting.addCastingRecipe(new ItemStack(armorPattern, 1, sc),
         * gloFlu, armor[sc], 50, frp); }
         */
    }

    protected static void addRecipesForSmeltery() {
        // Smeltery fuels
        Smeltery.addSmelteryFuel(FluidRegistry.LAVA, 1300, 80); // lava lasts 4 seconds per 15mb
        // register pyrotheum if it's present
        Fluid pyrotheum = FluidRegistry.getFluid("pyrotheum");
        if (pyrotheum != null)
            Smeltery.addSmelteryFuel(pyrotheum, 5000, 70); // pyrotheum lasts 3.5 seconds per 15mb

        // BLOOD FOR THE BLOOD GOD
        if (TinkerWorld.meatBlock != null) {
            Smeltery.addMelting(new ItemStack(Items.rotten_flesh), TinkerWorld.meatBlock, 0, 200, new FluidStack(bloodFluid, 5));
            Smeltery.addMelting(new ItemStack(TinkerWorld.strangeFood, 1, 1), TinkerWorld.meatBlock, 0, 80, new FluidStack(bloodFluid, 160));
        }

        // Alloy Smelting
        Smeltery.addAlloyMixing(
                new FluidStack(TinkerSmeltery.moltenBronzeFluid, (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsBronzeAlloy)),
                new FluidStack(TinkerSmeltery.moltenCopperFluid, TConstruct.nuggetLiquidValue * 3),
                    new FluidStack(TinkerSmeltery.moltenTinFluid, TConstruct.nuggetLiquidValue)); // Bronze
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenAlubrassFluid, (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsAluminumBrassAlloy)),
                new FluidStack(TinkerSmeltery.moltenAluminumFluid, TConstruct.nuggetLiquidValue * 3),
                new FluidStack(TinkerSmeltery.moltenCopperFluid, TConstruct.nuggetLiquidValue * 1)); // Aluminum
                                                                                                                                        // Brass
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenAlumiteFluid, (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsAlumiteAlloy)),
                new FluidStack(TinkerSmeltery.moltenAluminumFluid, TConstruct.nuggetLiquidValue * 5),
                new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.nuggetLiquidValue * 2), new FluidStack(TinkerSmeltery.moltenObsidianFluid, TConstruct.nuggetLiquidValue * 2)); // Alumite
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenManyullynFluid,
                (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsManyullynAlloy)), new FluidStack(TinkerSmeltery.moltenCobaltFluid,
                TConstruct.nuggetLiquidValue), new FluidStack(TinkerSmeltery.moltenArditeFluid, TConstruct.nuggetLiquidValue)); // Manyullyn
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.pigIronFluid, (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsPigironAlloy)),
                new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.nuggetLiquidValue),
                new FluidStack(TinkerSmeltery.moltenEmeraldFluid, 640),
                new FluidStack(TinkerSmeltery.bloodFluid, 80)); // Pigiron
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenObsidianFluid, TConstruct.oreLiquidValue),
                new FluidStack(FluidRegistry.LAVA, BUCKET_SIZE), new FluidStack(FluidRegistry.WATER, BUCKET_SIZE)); // Obsidian
        // Stone parts
        FluidType stone = FluidType.getFluidType("Stone");
        for (int sc = 0; sc < TinkerTools.patternOutputs.length; sc++) {
            if (TinkerTools.patternOutputs[sc] != null) {
                Smeltery.addMelting(stone, new ItemStack(TinkerTools.patternOutputs[sc], 1, 1), 1,
                        ((FULL_GRID - 1) * ((IPattern) TinkerTools.woodPattern).getPatternCost(new ItemStack(TinkerTools.woodPattern, 1, sc + 1))) / 2);
            }
        }

        FluidType iron = FluidType.getFluidType("Iron");
        FluidType gold = FluidType.getFluidType("Gold");
        FluidType steel = FluidType.getFluidType("Steel");

        // Chunks
        Smeltery.addMelting(FluidType.getFluidType("Stone"), new ItemStack(TinkerTools.toolShard, 1, 1), 0, 4);
        Smeltery.addMelting(iron, new ItemStack(TinkerTools.toolShard, 1, 2), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Obsidian"), new ItemStack(TinkerTools.toolShard, 1, MaterialID.Obsidian), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Cobalt"), new ItemStack(TinkerTools.toolShard, 1, MaterialID.Cobalt), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Ardite"), new ItemStack(TinkerTools.toolShard, 1, MaterialID.Ardite), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Manyullyn"), new ItemStack(TinkerTools.toolShard, 1, MaterialID.Manyullyn), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Copper"), new ItemStack(TinkerTools.toolShard, 1, MaterialID.Copper), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Bronze"), new ItemStack(TinkerTools.toolShard, 1, MaterialID.Bronze), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Alumite"), new ItemStack(TinkerTools.toolShard, 1, MaterialID.Alumite), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(steel, new ItemStack(TinkerTools.toolShard, 1, MaterialID.Steel), 0, TConstruct.chunkLiquidValue);

        // Items
        Smeltery.addMelting(FluidType.getFluidType("AluminumBrass"), new ItemStack(TinkerTools.blankPattern, 4, 1), INGOT_HEAT_USE, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(gold, new ItemStack(TinkerTools.blankPattern, 4, 2), INGOT_HEAT_USE, TConstruct.ingotLiquidValue * 2);
        Smeltery.addMelting(FluidType.getFluidType("Glue"), new ItemStack(TinkerTools.materials, 1, MaterialItem.GLUE_BALL), 0, TConstruct.ingotLiquidValue);

        Smeltery.addMelting(FluidType.getFluidType("Ender"), new ItemStack(Items.ender_pearl, 4), 0, 250);
        Smeltery.addMelting(TinkerWorld.metalBlock, MetalItemBlock.ENDER, 50, new FluidStack(moltenEnderFluid, BUCKET_SIZE));
        Smeltery.addMelting(FluidType.getFluidType("Water"), new ItemStack(Items.snowball, 1, 0), 0, 125);
        Smeltery.addMelting(iron, new ItemStack(Items.flint_and_steel, 1, 0), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Items.compass, 1, 0), 0, TConstruct.ingotLiquidValue * 4);
        Smeltery.addMelting(iron, new ItemStack(Items.bucket), 0, TConstruct.ingotLiquidValue * 3);
        Smeltery.addMelting(iron, new ItemStack(Items.minecart), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(iron, new ItemStack(Items.chest_minecart), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(iron, new ItemStack(Items.furnace_minecart), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(iron, new ItemStack(Items.hopper_minecart), 50, TConstruct.ingotLiquidValue * 10);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_door), 0, TConstruct.ingotLiquidValue * 6);
        Smeltery.addMelting(iron, new ItemStack(Items.cauldron), 0, CAULDRON_FLUID_OUTPUT);
        Smeltery.addMelting(iron, new ItemStack(Items.shears), 0, TConstruct.ingotLiquidValue * 2);
        Smeltery.addMelting(FluidType.getFluidType("Emerald"), new ItemStack(Items.emerald), INGOT_HEAT_USE, 640);

        Smeltery.addMelting(FluidType.getFluidType("Ardite"), new ItemStack(TinkerTools.materials, 1, MaterialItem.ARDITE_DUST), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Cobalt"), new ItemStack(TinkerTools.materials, 1, MaterialItem.COBALT_DUST), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Aluminum"), new ItemStack(OreCoreItems.aluminumDust, 1), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Manyullyn"), new ItemStack(TinkerTools.materials, 1, MaterialItem.MANYULLYN_DUST), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("AluminumBrass"), new ItemStack(TinkerTools.materials, 1, MaterialItem.ALUBRASS_DUST), 0, TConstruct.ingotLiquidValue);

        // Blocks melt as themselves!
        // Ore
        Smeltery.addMelting(Blocks.iron_ore, 0, IRON_HEAT_USE, new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(Blocks.gold_ore, 0, GOLD_HEAT_USE, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addDictionaryMelting(Iron.IRON_GRAVEL_DICT, iron, IRON_HEAT_USE, TConstruct.ingotLiquidValue * 2);
        Smeltery.addDictionaryMelting(Gold.GOLD_GRAVEL_DICT, gold, GOLD_HEAT_USE, TConstruct.ingotLiquidValue * 2);

        // Blocks
        Smeltery.addMelting(Blocks.iron_block, 0, IRON_HEAT_USE, new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue * FULL_GRID));
        Smeltery.addMelting(Blocks.gold_block, 0, GOLD_HEAT_USE, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * FULL_GRID));
        Smeltery.addMelting(Blocks.obsidian, 0, OBSIDIAN_HEAT_USE, new FluidStack(TinkerSmeltery.moltenObsidianFluid, OBSIDIAN_FLUID_OUTPUT));
        Smeltery.addMelting(Blocks.ice, 0, ICE_HEAT_USE, new FluidStack(FluidRegistry.getFluid("water"), ICE_WATER_OUTPUT));
        Smeltery.addMelting(Blocks.snow, 0, ICE_HEAT_USE, new FluidStack(FluidRegistry.getFluid("water"), SNOW_WATER_OUTPUT));
        Smeltery.addMelting(Blocks.snow_layer, 0, ICE_HEAT_USE, new FluidStack(FluidRegistry.getFluid("water"), SNOW_LAYER_WATER_OUTPUT));
        Smeltery.addMelting(Blocks.sand, 0, GLASS_HEAT_USE, new FluidStack(TinkerSmeltery.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Blocks.glass, 0, GLASS_HEAT_USE, new FluidStack(TinkerSmeltery.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Blocks.glass_pane, 0, GLASS_HEAT_USE, new FluidStack(TinkerSmeltery.moltenGlassFluid, 250));
        Smeltery.addMelting(Blocks.stone, 0, STONE_HEAT_USE, new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.stoneLiquidValue));
        Smeltery.addMelting(Blocks.cobblestone, 0, STONE_HEAT_USE, new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.stoneLiquidValue));
        Smeltery.addMelting(Blocks.emerald_block, 0, STONE_HEAT_USE, new FluidStack(TinkerSmeltery.moltenEmeraldFluid, 640 * 9));
        Smeltery.addMelting(Blocks.emerald_ore, 0, STONE_HEAT_USE, new FluidStack(TinkerSmeltery.moltenEmeraldFluid, 640 * 2)); // the ore also is done here
        Smeltery.addMelting(TinkerSmeltery.glueBlock, 0, GLUE_HEAT_USE * 2, new FluidStack(TinkerSmeltery.glueFluid, TConstruct.blockLiquidValue));
        Smeltery.addMelting(TinkerTools.craftedSoil, 1, 600, new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.ingotLiquidValue / 4));

        Smeltery.addMelting(TinkerSmeltery.clearGlass, 0, 500, new FluidStack(TinkerSmeltery.moltenGlassFluid, BUCKET_SIZE));
        Smeltery.addMelting(TinkerSmeltery.glassPane, 0, 350, new FluidStack(TinkerSmeltery.moltenGlassFluid, 250));

        for (int i = 0; i < 16; i++) {
            Smeltery.addMelting(TinkerSmeltery.stainedGlassClear, i, 500, new FluidStack(TinkerSmeltery.moltenGlassFluid, BUCKET_SIZE));
            Smeltery.addMelting(TinkerSmeltery.stainedGlassClearPane, i, 350, new FluidStack(TinkerSmeltery.moltenGlassFluid, 250));
        }

        // Bricks
        Smeltery.addMelting(TinkerTools.multiBrick, MultiBrick.IRON, IRON_HEAT_USE, new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TinkerTools.multiBrickFancy, MultiBrick.IRON, IRON_HEAT_USE, new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TinkerTools.multiBrick, MultiBrick.GOLD, GOLD_HEAT_USE, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TinkerTools.multiBrickFancy, MultiBrick.GOLD, GOLD_HEAT_USE, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TinkerTools.multiBrick, MultiBrick.OBSIDIAN, OBSIDIAN_HEAT_USE, new FluidStack(TinkerSmeltery.moltenObsidianFluid, OBSIDIAN_FLUID_OUTPUT));
        Smeltery.addMelting(TinkerTools.multiBrickFancy, MultiBrick.OBSIDIAN, OBSIDIAN_HEAT_USE, new FluidStack(TinkerSmeltery.moltenObsidianFluid, OBSIDIAN_FLUID_OUTPUT));

        addVanillaBlockMelting(iron, gold);
        addVanillaArmorMelting(iron, gold, steel);
        addVanillaItemMelting(iron, gold);
    }


    private static void addVanillaBlockMelting(FluidType iron, FluidType gold) {
        // Vanilla blocks
        Smeltery.addMelting(iron, new ItemStack(Blocks.iron_bars), 0, TConstruct.ingotLiquidValue * 6 / 16);
        Smeltery.addMelting(iron, new ItemStack(Blocks.heavy_weighted_pressure_plate), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(gold, new ItemStack(Blocks.light_weighted_pressure_plate, 4), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Blocks.rail), 0, TConstruct.ingotLiquidValue * 6 / 16);
        Smeltery.addMelting(gold, new ItemStack(Blocks.golden_rail), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Blocks.detector_rail), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Blocks.activator_rail), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Obsidian"), new ItemStack(Blocks.enchanting_table), 0, TConstruct.ingotLiquidValue * 4);
        // Smeltery.addMelting(iron, new ItemStack(Blocks.cauldron),
        // 0, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(iron, new ItemStack(Blocks.anvil, 1, 0), ANVIL_HEAT_USE, ANVIL_FLUID_OUTPUT);
        Smeltery.addMelting(iron, new ItemStack(Blocks.anvil, 1, 1), ANVIL_HEAT_USE, ANVIL_FLUID_OUTPUT);
        Smeltery.addMelting(iron, new ItemStack(Blocks.anvil, 1, 2), ANVIL_HEAT_USE, ANVIL_FLUID_OUTPUT);
        Smeltery.addMelting(iron, new ItemStack(Blocks.hopper), 0, TConstruct.ingotLiquidValue * 5);
    }
    private static void addVanillaArmorMelting(FluidType iron, FluidType gold, FluidType steel) {
        // Vanilla Armor
        Smeltery.addMelting(iron, new ItemStack(Items.iron_helmet, 1, 0), ARMOR_MELT_USE, HELMET_FLUID_OUTPUT);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_chestplate, 1, 0), ARMOR_MELT_USE, CHESTPLATE_FLUID_OUTPUT);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_leggings, 1, 0), ARMOR_MELT_USE, LEGGINGS_FLUID_OUTPUT);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_boots, 1, 0), ARMOR_MELT_USE, BOOTS_FLUID_OUTPUT);

        Smeltery.addMelting(gold, new ItemStack(Items.golden_helmet, 1, 0), ARMOR_MELT_USE, HELMET_FLUID_OUTPUT);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_chestplate, 1, 0), ARMOR_MELT_USE, CHESTPLATE_FLUID_OUTPUT);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_leggings, 1, 0), ARMOR_MELT_USE, LEGGINGS_FLUID_OUTPUT);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_boots, 1, 0), ARMOR_MELT_USE, TConstruct.ingotLiquidValue * 4);

        Smeltery.addMelting(steel, new ItemStack(Items.chainmail_helmet, 1, 0), 25, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(steel, new ItemStack(Items.chainmail_chestplate, 1, 0), 50, TConstruct.oreLiquidValue);
        Smeltery.addMelting(steel, new ItemStack(Items.chainmail_leggings, 1, 0), 50, TConstruct.oreLiquidValue);
        Smeltery.addMelting(steel, new ItemStack(Items.chainmail_boots, 1, 0), 25, TConstruct.ingotLiquidValue);

        Smeltery.addMelting(iron, new ItemStack(Items.iron_horse_armor, 1), HORSE_ARMOR_HEAT_USE, HORSE_ARMOR_FLUID_OUTPUT);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_horse_armor, 1), HORSE_ARMOR_HEAT_USE, HORSE_ARMOR_FLUID_OUTPUT);
    }

    private static void addVanillaItemMelting(FluidType iron, FluidType gold) {
        Smeltery.addMelting(iron, new ItemStack(Items.iron_hoe, 1, NO_META), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_sword, 1, NO_META), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_shovel, 1, NO_META), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_pickaxe, 1, NO_META), 0, PICKAXE_FLUID_OUTPUT);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_axe, 1, NO_META), 0, AXE_FLUID_OUTPUT);

        Smeltery.addMelting(gold, new ItemStack(Items.golden_hoe, 1, NO_META), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_sword, 1, NO_META), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_shovel, 1, NO_META), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_pickaxe, 1, NO_META), 0, PICKAXE_FLUID_OUTPUT);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_axe, 1, NO_META), 0, AXE_FLUID_OUTPUT);

        Smeltery.addMelting(gold, new ItemStack(Items.golden_apple, 1, 0), 250, TConstruct.ingotLiquidValue * 8);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_apple, 1, 1), 600, TConstruct.ingotLiquidValue * 72);
        Smeltery.addMelting(gold, new ItemStack(Items.speckled_melon, 1, NO_META), -20, TConstruct.ingotLiquidValue * 8 / 9);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_carrot, 1, NO_META), -20, TConstruct.ingotLiquidValue * 8 / 9);
    }

    private void registerIngotCasting(FluidType ft, String name) {
        ItemStack pattern = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
        LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
        for (ItemStack ore : OreDictionary.getOres(name)) {
            tableCasting.addCastingRecipe(pattern, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue),
                    new ItemStack(ore.getItem(), 1, ore.getItemDamage()), false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(pattern, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2),
                    new ItemStack(ore.getItem(), 1, ore.getItemDamage()), false, METAL_MELT_DELAY);
            tableCasting.addCastingRecipe(new ItemStack(ore.getItem(), 1, ore.getItemDamage()),
                    new FluidStack(ft.fluid, TConstruct.ingotLiquidValue), pattern, CAST_DELAY);
        }
    }

    private void registerBlockCasting(FluidType ft, String name) {
        for (ItemStack ore : OreDictionary.getOres(name)) {
            TConstructRegistry.getBasinCasting().addCastingRecipe(new ItemStack(ore.getItem(), 1, ore.getItemDamage()),
                    new FluidStack(ft.fluid, TConstruct.blockLiquidValue), BASIN_CASTING_MELT_DELAY);
        }
    }

    public void modIntegration() {
        /* Natura */
        Block taintedSoil = GameRegistry.findBlock("Natura", "soil.tainted");
        Block heatSand = GameRegistry.findBlock("Natura", "heatsand");
        if (taintedSoil != null && heatSand != null)
            GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.craftedSoil, 2, 6), Items.nether_wart, taintedSoil, heatSand);

        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
        ArrayList<ItemStack> ores;

        // TE alloys
        Smeltery.addAlloyMixing(
                new FluidStack(TinkerSmeltery.moltenInvarFluid, TConstruct.ingotLiquidValue * 3),
                new FluidStack( TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue * 2),
                new FluidStack(TinkerSmeltery.moltenNickelFluid, TConstruct.ingotLiquidValue * 1)); // Invar
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenElectrumFluid, TConstruct.ingotLiquidValue * 2), new FluidStack(
                TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue), new FluidStack(TinkerSmeltery.moltenSilverFluid,
                TConstruct.ingotLiquidValue)); // Electrum

        // TE is handled by the oredicted variant already
        /* Thermal Expansion 3 Metals */
        /*
         * ArrayList<ItemStack> ores = OreDictionary.getOres("ingotNickel"); if
         * (ores.size() > 0) { tableCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenNickelFluid,
         * TConstruct.ingotLiquidValue), ingotcast, 80); } ores =
         * OreDictionary.getOres("ingotLead"); if (ores.size() > 0) {
         * tableCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenLeadFluid,
         * TConstruct.ingotLiquidValue), ingotcast, 80); } ores =
         * OreDictionary.getOres("ingotSilver"); if (ores.size() > 0) {
         * tableCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenSilverFluid,
         * TConstruct.ingotLiquidValue), ingotcast, 80); } ores =
         * OreDictionary.getOres("ingotPlatinum"); if (ores.size() > 0) {
         * tableCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenShinyFluid,
         * TConstruct.ingotLiquidValue), ingotcast, 80); } ores =
         * OreDictionary.getOres("ingotInvar"); if (ores.size() > 0) {
         * tableCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenInvarFluid,
         * TConstruct.ingotLiquidValue), ingotcast, 80);
         * Smeltery.addAlloyMixing(new
         * FluidStack(TinkerSmeltery.moltenInvarFluid,
         * TConstruct.ingotLiquidValue * 3), new
         * FluidStack(TinkerSmeltery.moltenIronFluid,
         * TConstruct.ingotLiquidValue * 2), new
         * FluidStack(TinkerSmeltery.moltenNickelFluid,
         * TConstruct.ingotLiquidValue * 1)); // Invar } ores =
         * OreDictionary.getOres("ingotElectrum"); if (ores.size() > 0) {
         * tableCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenElectrumFluid,
         * TConstruct.ingotLiquidValue), ingotcast, 80);
         * Smeltery.addAlloyMixing(new
         * FluidStack(TinkerSmeltery.moltenElectrumFluid,
         * TConstruct.ingotLiquidValue * 2), new
         * FluidStack(TinkerSmeltery.moltenGoldFluid,
         * TConstruct.ingotLiquidValue), new
         * FluidStack(TinkerSmeltery.moltenSilverFluid,
         * TConstruct.ingotLiquidValue)); // Electrum }
         *
         * ores = OreDictionary.getOres("blockNickel"); if (ores.size() > 0) {
         * basinCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenNickelFluid,
         * TConstruct.blockLiquidValue), null, 100); } ores =
         * OreDictionary.getOres("blockLead"); if (ores.size() > 0) {
         * basinCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenLeadFluid,
         * TConstruct.blockLiquidValue), null, 100); } ores =
         * OreDictionary.getOres("blockSilver"); if (ores.size() > 0) {
         * basinCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenSilverFluid,
         * TConstruct.blockLiquidValue), null, 100); } ores =
         * OreDictionary.getOres("blockPlatinum"); if (ores.size() > 0) {
         * basinCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenShinyFluid,
         * TConstruct.blockLiquidValue), null, 100); } ores =
         * OreDictionary.getOres("blockInvar"); if (ores.size() > 0) {
         * basinCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenInvarFluid,
         * TConstruct.blockLiquidValue), null, 100); } ores =
         * OreDictionary.getOres("blockElectrum"); if (ores.size() > 0) {
         * basinCasting.addCastingRecipe(ores.get(0), new
         * FluidStack(TinkerSmeltery.moltenElectrumFluid,
         * TConstruct.blockLiquidValue), null, 100); }
         */
        /* Extra Utilities */
        ores = OreDictionary.getOres("compressedGravel1x");
        if (ores.size() > 0) {
            basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.speedBlock, FULL_GRID), new FluidStack(TinkerSmeltery.moltenTinFluid,
                    TConstruct.ingotLiquidValue), ores.get(0), true, BASIN_CASTING_MELT_DELAY);
            basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.speedBlock, FULL_GRID), new FluidStack(TinkerSmeltery.moltenElectrumFluid,
                    TConstruct.ingotLiquidValue / 3), ores.get(0), true, BASIN_CASTING_MELT_DELAY);
        }
        ores = OreDictionary.getOres("compressedGravel2x"); // Higher won't save
                                                            // properly
        if (ores.size() > 0) {
            basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.speedBlock, 81), new FluidStack(TinkerSmeltery.moltenTinFluid,
                    TConstruct.blockLiquidValue), ores.get(0), true, BASIN_CASTING_MELT_DELAY);
            basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.speedBlock, 81), new FluidStack(TinkerSmeltery.moltenElectrumFluid,
                    TConstruct.blockLiquidValue / 3), ores.get(0), true, BASIN_CASTING_MELT_DELAY);
        }

        /* Rubber */
        ores = OreDictionary.getOres("itemRubber");
        if (ores.size() > 0) {
            FurnaceRecipes.smelting().func_151394_a(new ItemStack(TinkerTools.materials, 1, MaterialItem.GLUE_BALL), ores.get(0), 0.2f);
        }
    }
}
