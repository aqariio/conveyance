package aqario.conveyance.client.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class MonoplaneEntityModel extends EntityModel<Entity> {
	private final ModelPart plane;

	public MonoplaneEntityModel(ModelPart root) {
		this.plane = root.getChild("plane");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData plane = modelPartData.addChild("plane", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 18.0F, 0.0F));

		ModelPartData fuselage = plane.addChild("fuselage", ModelPartBuilder.create().uv(192, 79).cuboid(-9.0F, -34.0F, -6.0F, 18.0F, 8.0F, 24.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-10.0F, -26.0F, -57.0F, 20.0F, 22.0F, 81.0F, new Dilation(0.0F))
				.uv(121, 137).cuboid(-7.0F, -32.0F, 18.0F, 14.0F, 22.0F, 40.0F, new Dilation(0.0F))
				.uv(0, 155).cuboid(-4.0F, -32.0F, 58.0F, 8.0F, 16.0F, 34.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -5.0F));

		ModelPartData wing = plane.addChild("wing", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -5.0F));

		ModelPartData right_aileron_r1 = wing.addChild("right_aileron_r1", ModelPartBuilder.create().uv(121, 61).mirrored().cuboid(-64.0F, -2.0F, 0.0F, 64.0F, 2.0F, 16.0F, new Dilation(0.0F)).mirrored(false)
				.uv(121, 0).mirrored().cuboid(-80.0F, -3.0F, -32.0F, 80.0F, 3.0F, 32.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-10.0F, -4.0F, 8.0F, 0.0F, 0.0F, 0.0873F));

		ModelPartData left_aileron_r1 = wing.addChild("left_aileron_r1", ModelPartBuilder.create().uv(121, 61).cuboid(0.0F, -2.0F, 0.0F, 64.0F, 2.0F, 16.0F, new Dilation(0.0F))
				.uv(121, 0).cuboid(0.0F, -3.0F, -32.0F, 80.0F, 3.0F, 32.0F, new Dilation(0.0F)), ModelTransform.of(10.0F, -4.0F, 8.0F, 0.0F, 0.0F, -0.0873F));

		ModelPartData tail = plane.addChild("tail", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -52.0F, 80.0F, 2.0F, 32.0F, 24.0F, new Dilation(0.0F))
				.uv(121, 35).cuboid(-34.0F, -31.0F, 76.0F, 68.0F, 2.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -5.0F));

		ModelPartData propeller = plane.addChild("propeller", ModelPartBuilder.create().uv(0, 209).cuboid(-8.0F, -12.0F, -13.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
				.uv(65, 206).cuboid(-30.0F, -34.0F, -4.0F, 60.0F, 60.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -11.0F, -61.0F));

		ModelPartData wheels = plane.addChild("wheels", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -5.0F));

		ModelPartData left = wheels.addChild("left", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 3.0F, -4.0F, 2.0F, 8.0F, 8.0F, new Dilation(0.0F))
				.uv(30, 0).mirrored().cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(18.0F, -5.0F, 0.0F));

		ModelPartData right = wheels.addChild("right", ModelPartBuilder.create().uv(0, 0).cuboid(-20.0F, -2.0F, -4.0F, 2.0F, 8.0F, 8.0F, new Dilation(0.0F))
				.uv(30, 0).cuboid(-18.0F, -5.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData back = wheels.addChild("back", ModelPartBuilder.create().uv(40, 0).cuboid(-1.0F, -19.0F, 76.0F, 2.0F, 5.0F, 5.0F, new Dilation(0.0F))
				.uv(56, 0).cuboid(-2.0F, -22.0F, 78.0F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 6.0F, 5.0F));
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
