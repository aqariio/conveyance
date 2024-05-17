package aqario.conveyance.common.block;

import aqario.conveyance.common.Conveyance;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class ConveyanceBlocks {
    public static final Block SPRUCE_HULL = register("spruce_hull", new ImmovablePillarBlock(QuiltBlockSettings.of(Material.WOOD, Blocks.SPRUCE_PLANKS.getDefaultMapColor()).strength(-1.0f, 3600000.0f).sounds(BlockSoundGroup.WOOD).dropsNothing()), ItemGroup.BUILDING_BLOCKS);
    public static final Block DARK_OAK_HULL = register("dark_oak_hull", new ImmovablePillarBlock(QuiltBlockSettings.of(Material.WOOD, Blocks.DARK_OAK_PLANKS.getDefaultMapColor()).strength(-1.0f, 3600000.0f).sounds(BlockSoundGroup.WOOD).dropsNothing()), ItemGroup.BUILDING_BLOCKS);
    public static final Block OAK_BEAM = register("oak_beam", new ImmovablePillarBlock(QuiltBlockSettings.of(Material.WOOD, Blocks.OAK_LOG.getDefaultMapColor()).strength(-1.0f, 3600000.0f).sounds(BlockSoundGroup.WOOD).dropsNothing()), ItemGroup.BUILDING_BLOCKS);
    public static final Block SPRUCE_CABIN_DOOR = registerDoor("spruce_cabin_door", new CabinDoorBlock(QuiltBlockSettings.of(Material.WOOD, Blocks.SPRUCE_PLANKS.getDefaultMapColor()).strength(-1.0f, 3600000.0f).sounds(BlockSoundGroup.WOOD).dropsNothing().nonOpaque()));

    private static Block register(String id, Block block, ItemGroup group) {
        registerBlockItem(id, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(Conveyance.ID, id), block);
    }

    private static Block registerDoor(String id, Block block) {
        registerDoorBlockItem(id, block);
        return Registry.register(Registry.BLOCK, new Identifier(Conveyance.ID, id), block);
    }

    private static void registerDoorBlockItem(String id, Block block) {
        Registry.register(Registry.ITEM, new Identifier(Conveyance.ID, id), new BlockItem(block, new QuiltItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
    }

    private static void registerBlockItem(String id, Block block, ItemGroup group) {
        Registry.register(Registry.ITEM, new Identifier(Conveyance.ID, id), new BlockItem(block, new QuiltItemSettings().group(group)));
    }

    public static void init() {
    }
}
