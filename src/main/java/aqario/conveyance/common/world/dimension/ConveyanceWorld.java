package aqario.conveyance.common.world.dimension;

import aqario.conveyance.common.Conveyance;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ConveyanceWorld {
    public static final RegistryKey<World> GALLEON = RegistryKey.of(Registry.WORLD_KEY, new Identifier(Conveyance.ID, "galleon"));
    public static final RegistryKey<DimensionType> GALLEON_TYPE = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, GALLEON.getValue());

    public static void init() {
    }
}
