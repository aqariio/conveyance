package aqario.conveyance.client.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class BiplaneEntityModel extends EntityModel<Entity> {
    private final ModelPart plane;

    public BiplaneEntityModel(ModelPart root) {
        this.plane = root.getChild("plane");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData plane = modelPartData.addChild("plane", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 18.0F, 0.0F));

        ModelPartData fuselage = plane.addChild("fuselage", ModelPartBuilder.create().uv(0, 54).cuboid(-10.0F, -32.0F, -39.0F, 20.0F, 22.0F, 64.0F, new Dilation(0.0F))
            .uv(120, 178).cuboid(-7.0F, -31.0F, 25.0F, 14.0F, 18.0F, 34.0F, new Dilation(0.0F))
            .uv(182, 196).cuboid(-4.0F, -30.0F, 59.0F, 8.0F, 12.0F, 34.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -5.0F));

        ModelPartData wing = plane.addChild("wing", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -5.0F));

        ModelPartData upper = wing.addChild("upper", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right_aileron_r1 = upper.addChild("right_aileron_r1", ModelPartBuilder.create().uv(160, 130).cuboid(-64.0F, -2.0F, 0.0F, 64.0F, 2.0F, 10.0F, new Dilation(0.0F))
            .uv(0, 0).cuboid(-80.0F, -3.0F, -24.0F, 90.0F, 3.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(-10.0F, -42.0F, 1.0F, 0.0F, 0.0F, 0.0349F));

        ModelPartData left_aileron_r1 = upper.addChild("left_aileron_r1", ModelPartBuilder.create().uv(160, 142).cuboid(0.0F, -2.0F, 0.0F, 64.0F, 2.0F, 10.0F, new Dilation(0.0F))
            .uv(0, 27).cuboid(-10.0F, -3.0F, -24.0F, 90.0F, 3.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(10.0F, -42.0F, 1.0F, 0.0F, 0.0F, -0.0349F));

        ModelPartData lower = wing.addChild("lower", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right_aileron_r2 = lower.addChild("right_aileron_r2", ModelPartBuilder.create().uv(120, 166).cuboid(-64.0F, -2.0F, 0.0F, 64.0F, 2.0F, 10.0F, new Dilation(0.0F))
            .uv(104, 54).cuboid(-80.0F, -3.0F, -24.0F, 80.0F, 3.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(-10.0F, -10.0F, 13.0F, 0.0F, 0.0F, 0.0873F));

        ModelPartData left_aileron_r2 = lower.addChild("left_aileron_r2", ModelPartBuilder.create().uv(168, 108).cuboid(0.0F, -2.0F, 0.0F, 64.0F, 2.0F, 10.0F, new Dilation(0.0F))
            .uv(104, 81).cuboid(0.0F, -3.0F, -24.0F, 80.0F, 3.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(10.0F, -10.0F, 13.0F, 0.0F, 0.0F, -0.0873F));

        ModelPartData brace = wing.addChild("brace", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -36.0F, 0.0F));

        ModelPartData right_back_r1 = brace.addChild("right_back_r1", ModelPartBuilder.create().uv(120, 178).cuboid(-141.0F, -11.5F, 6.0F, 2.0F, 29.0F, 2.0F, new Dilation(0.0F))
            .uv(52, 54).cuboid(-141.0F, -16.5F, -6.0F, 2.0F, 30.0F, 2.0F, new Dilation(0.0F))
            .uv(128, 178).cuboid(-1.0F, -11.5F, 6.0F, 2.0F, 29.0F, 2.0F, new Dilation(0.0F))
            .uv(52, 86).cuboid(-1.0F, -16.5F, -6.0F, 2.0F, 30.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(69.0F, 4.5F, -4.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData tail = plane.addChild("tail", ModelPartBuilder.create().uv(0, 54).cuboid(-1.0F, -48.0F, 81.0F, 2.0F, 28.0F, 24.0F, new Dilation(0.0F))
            .uv(0, 140).cuboid(-34.0F, -26.0F, 77.0F, 68.0F, 2.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -5.0F));

        ModelPartData propeller = plane.addChild("propeller", ModelPartBuilder.create().uv(204, 0).cuboid(-5.0F, -15.0F, 7.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
            .uv(0, 166).cuboid(-30.0F, -40.0F, 14.0F, 60.0F, 60.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -11.0F, -61.0F));

        ModelPartData wheels = plane.addChild("wheels", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -5.0F));

        ModelPartData left = wheels.addChild("left", ModelPartBuilder.create().uv(0, 27).cuboid(0.0F, 3.0F, -3.0F, 2.0F, 8.0F, 8.0F, new Dilation(0.0F))
            .uv(28, 54).cuboid(-2.0F, -6.0F, 0.0F, 2.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(18.0F, -5.0F, 0.0F));

        ModelPartData right = wheels.addChild("right", ModelPartBuilder.create().uv(0, 0).cuboid(-20.0F, -2.0F, -3.0F, 2.0F, 8.0F, 8.0F, new Dilation(0.0F))
            .uv(14, 54).cuboid(-18.0F, -11.0F, 0.0F, 2.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData back = wheels.addChild("back", ModelPartBuilder.create().uv(0, 54).cuboid(-1.0F, -21.0F, 77.0F, 2.0F, 5.0F, 5.0F, new Dilation(0.0F))
            .uv(0, 0).cuboid(-2.0F, -24.0F, 79.0F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 6.0F, 5.0F));
        return TexturedModelData.of(modelData, 512, 512);
    }

    @Override
    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        plane.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
