package aqario.conveyance.common;

import aqario.conveyance.common.block.ConveyanceBlocks;
import aqario.conveyance.common.entity.ConveyanceEntityType;
import aqario.conveyance.common.item.ConveyanceItems;
import aqario.conveyance.common.world.dimension.ConveyanceDimensions;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Conveyance implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Conveyance");
	public static final String ID = "conveyance";

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Loading {}", mod.metadata().name());
		ConveyanceBlocks.init();
		ConveyanceEntityType.init();
		ConveyanceItems.init();
		ConveyanceDimensions.init();
	}
}
