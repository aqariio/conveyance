package aqario.conveyance.client;

import aqario.conveyance.client.model.GalleonEntityModel;
import aqario.conveyance.client.renderer.GalleonEntityRenderer;
import aqario.conveyance.common.Conveyance;
import aqario.conveyance.common.entity.ConveyanceEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class ConveyanceClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_GALLEON_LAYER = new EntityModelLayer(new Identifier(Conveyance.ID, "galleon"), "main");
    @Override
    public void onInitializeClient(ModContainer mod) {
        EntityRendererRegistry.register(ConveyanceEntities.GALLEON, GalleonEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_GALLEON_LAYER, GalleonEntityModel::getTexturedModelData);
    }
}
