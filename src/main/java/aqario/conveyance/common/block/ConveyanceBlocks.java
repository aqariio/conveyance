package aqario.conveyance.common.block;

import aqario.conveyance.common.Conveyance;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class ConveyanceBlocks {
    public static final Block SPRUCE_HULL = register("spruce_hull", new PillarBlock(QuiltBlockSettings.create().strength(-1.0f, 3600000.0f).sounds(BlockSoundGroup.WOOD).mapColor(Blocks.SPRUCE_PLANKS.getDefaultMapColor()).dropsNothing().pistonBehavior(PistonBehavior.BLOCK)), ItemGroups.BUILDING_BLOCKS);
    public static final Block DARK_OAK_HULL = register("dark_oak_hull", new PillarBlock(QuiltBlockSettings.create().strength(-1.0f, 3600000.0f).sounds(BlockSoundGroup.WOOD).mapColor(Blocks.DARK_OAK_PLANKS.getDefaultMapColor()).dropsNothing().pistonBehavior(PistonBehavior.BLOCK)), ItemGroups.BUILDING_BLOCKS);
    public static final Block OAK_BEAM = register("oak_beam", new PillarBlock(QuiltBlockSettings.create().strength(-1.0f, 3600000.0f).sounds(BlockSoundGroup.WOOD).mapColor(Blocks.OAK_LOG.getDefaultMapColor()).dropsNothing().pistonBehavior(PistonBehavior.BLOCK)), ItemGroups.BUILDING_BLOCKS);
    public static final Block SPRUCE_CABIN_DOOR = register("spruce_cabin_door", new CabinDoorBlock(QuiltBlockSettings.create().strength(-1.0f, 3600000.0f).mapColor(Blocks.SPRUCE_PLANKS.getDefaultMapColor()).sounds(BlockSoundGroup.WOOD).dropsNothing().nonOpaque().pistonBehavior(PistonBehavior.BLOCK)), ItemGroups.FUNCTIONAL_BLOCKS);

    private static Block register(String id, Block block, RegistryKey<ItemGroup> group) {
        registerBlockItem(id, block, group);
        return Registry.register(Registries.BLOCK, new Identifier(Conveyance.ID, id), block);
    }

    private static void registerBlockItem(String id, Block block, RegistryKey<ItemGroup> group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addItem(block));
        Registry.register(Registries.ITEM, new Identifier(Conveyance.ID, id), new BlockItem(block, new QuiltItemSettings()));
    }

    public static void init() {
    }
}
