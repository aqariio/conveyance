package aqario.conveyance.common.entity;

import aqario.conveyance.common.Conveyance;
import aqario.conveyance.common.entity.vehicle.BiplaneEntity;
import aqario.conveyance.common.entity.vehicle.GalleonEntity;
import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

public class ConveyanceEntityType {
    public static final EntityType<GalleonEntity> GALLEON = register(
        "galleon",
        QuiltEntityTypeBuilder.create()
            .entityFactory(GalleonEntity::new)
            .spawnGroup(SpawnGroup.MISC)
            .setDimensions(EntityDimensions.fixed(1, 1))
    );

    public static final EntityType<MonoplaneEntity> MONOPLANE = register(
        "monoplane",
        QuiltEntityTypeBuilder.create()
            .entityFactory(MonoplaneEntity::new)
            .spawnGroup(SpawnGroup.MISC)
            .setDimensions(EntityDimensions.fixed(0.0F, 0.0F))
            .maxChunkTrackingRange(128)
    );

    public static final EntityType<BiplaneEntity> BIPLANE = register(
        "biplane",
        QuiltEntityTypeBuilder.create()
            .entityFactory(BiplaneEntity::new)
            .spawnGroup(SpawnGroup.MISC)
            .setDimensions(EntityDimensions.fixed(4.0F, 2.0F))
    );

    private static <T extends Entity> EntityType<T> register(String id, QuiltEntityTypeBuilder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(Conveyance.ID, id), type.build());
    }

    public static void init() {
    }
}
