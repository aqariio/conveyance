package aqario.conveyance.common.world.dimension;

import aqario.conveyance.common.Conveyance;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ConveyanceDimensions {
    public static final RegistryKey<World> GALLEON = RegistryKey.of(RegistryKeys.WORLD, new Identifier(Conveyance.ID, "galleon"));
    public static final RegistryKey<DimensionType> GALLEON_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, GALLEON.getValue());

    public static void init() {
    }
}
