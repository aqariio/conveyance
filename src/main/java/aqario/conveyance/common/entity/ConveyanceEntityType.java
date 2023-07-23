package aqario.conveyance.common.entity;

import aqario.conveyance.common.Conveyance;
import aqario.conveyance.common.entity.vehicle.BiplaneEntity;
import aqario.conveyance.common.entity.vehicle.GalleonEntity;
import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import net.minecraft.entity.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

public class ConveyanceEntityType {
    public static final EntityType<GalleonEntity> GALLEON = Registry.register(Registry.ENTITY_TYPE,
		new Identifier(Conveyance.ID, "galleon"),
		QuiltEntityTypeBuilder.create(SpawnGroup.MISC, GalleonEntity::new)
			.setDimensions(EntityDimensions.fixed(1, 1)).build());

	public static final EntityType<MonoplaneEntity> MONOPLANE = Registry.register(Registry.ENTITY_TYPE,
		new Identifier(Conveyance.ID, "monoplane"),
		QuiltEntityTypeBuilder.create()
			.entityFactory(MonoplaneEntity::new)
			.spawnGroup(SpawnGroup.MISC)
			.setDimensions(EntityDimensions.fixed(12.0F, 2.5F))
			.build()
	);

	public static final EntityType<BiplaneEntity> BIPLANE = Registry.register(Registry.ENTITY_TYPE,
		new Identifier(Conveyance.ID, "biplane"),
		QuiltEntityTypeBuilder.create(SpawnGroup.MISC, BiplaneEntity::new)
			.setDimensions(EntityDimensions.fixed(4.0F, 2.0F)).build());

	private static <T extends Entity> EntityType<T> register(String id, EntityType<T> type) {
		return Registry.register(Registry.ENTITY_TYPE, id, type);
	}

    public static void init() {
    }
}
