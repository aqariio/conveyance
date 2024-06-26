package aqario.conveyance.client.renderer;

import aqario.conveyance.client.ConveyanceClient;
import aqario.conveyance.client.model.MonoplaneEntityModel;
import aqario.conveyance.common.Conveyance;
import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;

public class MonoplaneEntityRenderer extends EntityRenderer<MonoplaneEntity> {
    public static final Identifier TEXTURE = new Identifier(Conveyance.ID, "textures/entity/monoplane.png");
    private final MonoplaneEntityModel model;

    public MonoplaneEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new MonoplaneEntityModel(ctx.getPart(ConveyanceClient.MONOPLANE));
    }

    @Override
    public void render(MonoplaneEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        matrices.translate(0.0, 1.2, 0.0);
        matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(-yaw));
        matrices.multiply(Axis.X_POSITIVE.rotationDegrees(180.0F + entity.getPitch(tickDelta)));
        matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(-entity.getRoll(tickDelta)));
        matrices.translate(0.0, 0.0, -1.0);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(this.getTexture(entity)));
        this.model.setAngles(entity, tickDelta, 0.0F, -0.1F, 0.0F, 0.0F);
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(MonoplaneEntity entity) {
        return TEXTURE;
    }
}
