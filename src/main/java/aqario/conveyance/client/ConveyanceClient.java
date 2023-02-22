package aqario.conveyance.client;

import aqario.conveyance.client.model.GalleonEntityModel;
import aqario.conveyance.client.model.MonoplaneEntityModel;
import aqario.conveyance.client.renderer.GalleonEntityRenderer;
import aqario.conveyance.client.renderer.MonoplaneEntityRenderer;
import aqario.conveyance.common.Conveyance;
import aqario.conveyance.common.entity.ConveyanceEntityType;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class ConveyanceClient implements ClientModInitializer {
    public static final EntityModelLayer GALLEON = new EntityModelLayer(new Identifier(Conveyance.ID, "galleon"), "main");
	public static final EntityModelLayer MONOPLANE = new EntityModelLayer(new Identifier(Conveyance.ID, "monoplane"), "main");
    @Override
    public void onInitializeClient(ModContainer mod) {
        EntityRendererRegistry.register(ConveyanceEntityType.GALLEON, GalleonEntityRenderer::new);
		EntityRendererRegistry.register(ConveyanceEntityType.MONOPLANE, MonoplaneEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(GALLEON, GalleonEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MONOPLANE, MonoplaneEntityModel::getTexturedModelData);
    }
}
