package aqario.conveyance.client;

import aqario.conveyance.client.model.BiplaneEntityModel;
import aqario.conveyance.client.model.GalleonEntityModel;
import aqario.conveyance.client.model.MonoplaneEntityModel;
import aqario.conveyance.client.renderer.BiplaneEntityRenderer;
import aqario.conveyance.client.renderer.GalleonEntityRenderer;
import aqario.conveyance.client.renderer.MonoplaneEntityRenderer;
import aqario.conveyance.client.sound.PropellerLoop;
import aqario.conveyance.common.Conveyance;
import aqario.conveyance.common.entity.ConveyanceEntityType;
import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public class ConveyanceClient implements ClientModInitializer {
    public static final EntityModelLayer GALLEON = new EntityModelLayer(new Identifier(Conveyance.ID, "galleon"), "main");
    public static final EntityModelLayer MONOPLANE = new EntityModelLayer(new Identifier(Conveyance.ID, "monoplane"), "main");
    public static final EntityModelLayer BIPLANE = new EntityModelLayer(new Identifier(Conveyance.ID, "biplane"), "main");
    public static PropellerLoop propellerSoundLoop;

    @Override
    public void onInitializeClient(ModContainer mod) {
        EntityRendererRegistry.register(ConveyanceEntityType.GALLEON, GalleonEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(GALLEON, GalleonEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ConveyanceEntityType.MONOPLANE, MonoplaneEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MONOPLANE, MonoplaneEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ConveyanceEntityType.BIPLANE, BiplaneEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(BIPLANE, BiplaneEntityModel::getTexturedModelData);

        ClientTickEvents.START.register((client) -> {
            if (client.player != null) {
                if (propellerSoundLoop == null) {
                    propellerSoundLoop = new PropellerLoop(client.player);
                }
                if (client.world != null) {
                    float closestDistance;
                    MonoplaneEntity closestPlane = null;
                    closestDistance = Float.MAX_VALUE;

                    for (MonoplaneEntity plane : client.world.getEntitiesByClass(MonoplaneEntity.class, client.player.getBoundingBox().expand(400.0), (plane) -> true)) {
                        float currentDistance = plane.distanceTo(client.player);
                        if (currentDistance < closestDistance) {
                            closestPlane = plane;
                            closestDistance = currentDistance;
                        }
                    }
                    if (closestPlane != null/* && !(client.player.getRootVehicle() instanceof MonoplaneEntity)*/) {
                        if (!client.getSoundManager().isPlaying(propellerSoundLoop)) {
                            client.getSoundManager().play(propellerSoundLoop);
                        }

                        float engineSpeedMultiplier = (200.0F - closestPlane.getEngineSpeed()) / 200.0F;
                        propellerSoundLoop.setVolume(2.0F - closestDistance / 200.0F - engineSpeedMultiplier);
                        propellerSoundLoop.setPitch(1.0F/* - engineSpeedMultiplier / 2.0F*/);
                    }
                    else {
                        if (client.getSoundManager().isPlaying(propellerSoundLoop)) {
                            client.getSoundManager().stop(propellerSoundLoop);
                        }
                    }
                }
            }
        });
    }
}
