package aqario.conveyance.common.entity;

import aqario.conveyance.common.Conveyance;
import aqario.conveyance.common.entity.vehicle.GalleonEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

public class ConveyanceEntities {
    public static final EntityType<GalleonEntity> GALLEON = Registry.register(Registry.ENTITY_TYPE, new Identifier(Conveyance.ID, "galleon"), QuiltEntityTypeBuilder.create(SpawnGroup.MISC, GalleonEntity::new).setDimensions(EntityDimensions.fixed(1, 1)).build());

    public static void init() {
    }
}
