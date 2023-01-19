package aqario.conveyance.common;

import aqario.conveyance.common.block.ConveyanceBlocks;
import aqario.conveyance.common.entity.ConveyanceEntities;
import aqario.conveyance.common.item.ConveyanceItems;
import aqario.conveyance.common.world.dimension.ConveyanceWorld;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Conveyance implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Conveyance");
	public static final String ID = "conveyance";

	@Override
	public void onInitialize(ModContainer mod) {
		ConveyanceEntities.init();
		ConveyanceBlocks.init();
		ConveyanceItems.init();
		ConveyanceWorld.init();
		LOGGER.info("Loading {}!", mod.metadata().name());
	}
}
