package aqario.conveyance.common;

import aqario.conveyance.common.block.ConveyanceBlocks;
import aqario.conveyance.common.entity.ConveyanceEntityType;
import aqario.conveyance.common.item.ConveyanceItems;
import aqario.conveyance.common.world.dimension.ConveyanceDimensions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.ServerWorldAccess;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

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

//		ServerPlayNetworking.registerGlobalReceiver(new Identifier(ID, "enter_interior"), (server, player, handler, buf, responseSender) -> {
//			double toPosX = buf.readDouble();
//			double toPosY = buf.readDouble();
//			double toPosZ = buf.readDouble();
//			int toYaw = buf.readInt();
//			ServerWorld galleonWorld = server.getWorld(ConveyanceDimensions.GALLEON);
//			if (galleonWorld == null) {
//				LOGGER.error("Galleon world is null!");
//				return;
//			}
//			System.out.println("Galleon joined for the first time, generating interior...");
//			StructureTemplateManager structureTemplateManager = galleonWorld.getStructureTemplateManager();
//			Optional<Structure> structure = structureTemplateManager.getStructure(new Identifier(ID, "galleon"));
//			if (structure.isPresent()) {
//				StructurePlacementData structurePlacementData = (new StructurePlacementData()).setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(false);
//				BlockPos blockPos = new BlockPos(0, -1, 0);
//				structure.get().place(galleonWorld, blockPos, blockPos, structurePlacementData, RandomGenerator.createLegacy(), 2);
//			}
//			System.out.println("Galleon interior generated!");
////			((AirshipWorldComponent)ExpeditionComponents.AIRSHIP.get(galleonWorld)).setInteriorGenerated(true);
////			AirshipExitEntity airshipExit = new AirshipExitEntity((class_1937)galleonWorld, airshipUUID);
////			airshipExit.method_23327(toPosX - 1.5D, toPosY, toPosZ);
////			galleonWorld.method_8649((class_1297)airshipExit);
////			AirshipCockpitExitEntity airshipCockpitExit = new AirshipCockpitExitEntity((class_1937)galleonWorld, airshipUUID);
////			airshipCockpitExit.method_23327(toPosX + 1.5D, toPosY, toPosZ - 6.0D);
////			galleonWorld.method_8649((class_1297)airshipCockpitExit);
//		});
	}
}
