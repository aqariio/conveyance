package aqario.conveyance.common.item;

import aqario.conveyance.common.Conveyance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class ConveyanceItems {
    public static final Item GLIDER = register("glider", new GliderItem(new QuiltItemSettings().maxCount(1).group(ItemGroup.TRANSPORTATION)));

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Conveyance.ID, id), item);
    }

    public static void init() {
    }
}
