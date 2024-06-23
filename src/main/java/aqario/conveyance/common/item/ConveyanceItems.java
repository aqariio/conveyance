package aqario.conveyance.common.item;

import aqario.conveyance.common.Conveyance;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class ConveyanceItems {
    public static final Item GLIDER = register("glider", new GliderItem(new QuiltItemSettings().maxCount(1)), ItemGroups.TOOLS_AND_UTILITIES);
    public static final Item MONOPLANE = register("monoplane", new MonoplaneItem(new QuiltItemSettings().maxCount(1)), ItemGroups.TOOLS_AND_UTILITIES);

    private static Item register(String id, Item item, RegistryKey<ItemGroup> group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addItem(item));
        return Registry.register(Registries.ITEM, new Identifier(Conveyance.ID, id), item);
    }

    public static void init() {
    }
}
