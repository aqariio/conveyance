package aqario.conveyance.client.renderer;

import aqario.conveyance.common.entity.vehicle.GalleonEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class GalleonEntityRenderer extends EntityRenderer<GalleonEntity> {
    public GalleonEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(GalleonEntity entity) {
        return null;
    }
}
