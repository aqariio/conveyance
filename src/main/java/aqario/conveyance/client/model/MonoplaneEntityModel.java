package aqario.conveyance.client.model;

import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class MonoplaneEntityModel extends EntityModel<MonoplaneEntity> {
    private final ModelPart plane;
    private final ModelPart propeller;
    private final ModelPart elevator;
    private final ModelPart rudder;
    private final ModelPart backWheel;
    private final ModelPart leftAileron;
    private final ModelPart rightAileron;

    public MonoplaneEntityModel(ModelPart root) {
        this.plane = root.getChild("plane");
        this.propeller = plane.getChild("propeller");
        this.elevator = plane.getChild("tail").getChild("elevator");
        this.rudder = plane.getChild("tail").getChild("rudder");
        this.backWheel = plane.getChild("landingGear").getChild("backWheel");
        this.leftAileron = plane.getChild("wings").getChild("leftWing").getChild("leftAileron");
        this.rightAileron = plane.getChild("wings").getChild("rightWing").getChild("rightAileron");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData plane = modelPartData.addChild("plane", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 6.0F, 0.0F));

        ModelPartData fuselage = plane.addChild("fuselage", ModelPartBuilder.create().uv(192, 79).cuboid(-9.0F, -34.0F, -6.0F, 18.0F, 8.0F, 24.0F, new Dilation(0.0F))
            .uv(0, 0).cuboid(-10.0F, -26.0F, -57.0F, 20.0F, 22.0F, 81.0F, new Dilation(0.0F))
            .uv(121, 137).cuboid(-7.0F, -32.0F, 18.0F, 14.0F, 22.0F, 40.0F, new Dilation(0.0F))
            .uv(0, 155).cuboid(-4.0F, -31.0F, 58.0F, 8.0F, 16.0F, 34.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.0F, -5.0F));

        ModelPartData dashboard_r1 = fuselage.addChild("dashboard_r1", ModelPartBuilder.create().uv(62, 126).cuboid(-10.0F, -22.0F, 0.0F, 20.0F, 22.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, -5.0F, 0.0873F, 0.0F, 0.0F));

        ModelPartData seat = fuselage.addChild("seat", ModelPartBuilder.create().uv(345, 26).cuboid(-7.0F, 1.0F, -7.0F, 14.0F, 2.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -13.0F, 9.0F));

        ModelPartData backrest_r1 = seat.addChild("backrest_r1", ModelPartBuilder.create().uv(345, 8).cuboid(-7.0F, -16.0F, 0.0F, 14.0F, 16.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.0F, 4.0F, -0.1222F, 0.0F, 0.0F));

        ModelPartData headrest_r1 = seat.addChild("headrest_r1", ModelPartBuilder.create().uv(345, 0).cuboid(-4.0F, -22.0F, 0.0F, 8.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.0F, 4.0F, -0.0698F, 0.0F, 0.0F));

        ModelPartData wings = plane.addChild("wings", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, -5.0F));

        ModelPartData leftWing = wings.addChild("leftWing", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData wing_r1 = leftWing.addChild("wing_r1", ModelPartBuilder.create().uv(121, 0).cuboid(0.0F, -3.0F, -28.0F, 80.0F, 3.0F, 32.0F, new Dilation(0.0F)), ModelTransform.of(10.0F, -4.0F, 4.0F, 0.0F, 0.0F, -0.0873F));

        ModelPartData leftAileron = leftWing.addChild("leftAileron", ModelPartBuilder.create().uv(121, 61).cuboid(0.0F, -1.0F, 0.0F, 64.0F, 2.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(10.0F, -5.0F, 8.0F, 0.0F, 0.0F, -0.0873F));

        ModelPartData rightWing = wings.addChild("rightWing", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData wing_r2 = rightWing.addChild("wing_r2", ModelPartBuilder.create().uv(121, 0).mirrored().cuboid(-80.0F, -3.0F, -28.0F, 80.0F, 3.0F, 32.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-10.0F, -4.0F, 4.0F, 0.0F, 0.0F, 0.0873F));

        ModelPartData rightAileron = rightWing.addChild("rightAileron", ModelPartBuilder.create().uv(121, 61).mirrored().cuboid(-64.0F, -1.0F, 0.0F, 64.0F, 2.0F, 16.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-10.0F, -5.0F, 8.0F, 0.0F, 0.0F, 0.0873F));

        ModelPartData tail = plane.addChild("tail", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, -5.0F));

        ModelPartData rudder = tail.addChild("rudder", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -16.0F, 0.0F, 2.0F, 32.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -36.0F, 80.0F));

        ModelPartData elevator = tail.addChild("elevator", ModelPartBuilder.create().uv(121, 35).cuboid(-34.0F, -1.0F, 0.0F, 68.0F, 2.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -29.0F, 76.0F));

        ModelPartData propeller = plane.addChild("propeller", ModelPartBuilder.create().uv(0, 209).cuboid(-7.0F, -7.0F, -11.0F, 14.0F, 14.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -11.0F, -61.0F));

        ModelPartData blade_r1 = propeller.addChild("blade_r1", ModelPartBuilder.create().uv(65, 206).cuboid(-30.0F, -30.0F, 0.0F, 60.0F, 60.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData landingGear = plane.addChild("landingGear", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, -5.0F));

        ModelPartData leftWheel = landingGear.addChild("leftWheel", ModelPartBuilder.create().uv(30, 0).mirrored().cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 16.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(12.0F, -5.0F, -16.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData wheel_r1 = leftWheel.addChild("wheel_r1", ModelPartBuilder.create().uv(53, 57).cuboid(-1.0F, -6.0F, -6.0F, 2.0F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 15.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

        ModelPartData rightWheel = landingGear.addChild("rightWheel", ModelPartBuilder.create().uv(30, 0).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 16.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, -5.0F, -16.0F, 0.0F, 0.0F, 0.2182F));

        ModelPartData wheel_r2 = rightWheel.addChild("wheel_r2", ModelPartBuilder.create().uv(53, 57).cuboid(0.0F, -6.0F, -6.0F, 2.0F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 15.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

        ModelPartData backWheel = landingGear.addChild("backWheel", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, 3.0F, -2.0F, 2.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -15.0F, 83.0F));

        ModelPartData axle_r1 = backWheel.addChild("axle_r1", ModelPartBuilder.create().uv(56, 0).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.5F, 0.0F, 0.5F, 0.2182F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 512, 512);
    }

    @Override
    public void setAngles(MonoplaneEntity plane, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.propeller.roll -= 2.432F;
        this.elevator.pitch = -plane.getPitchInput() / 4;
        this.rudder.yaw = -plane.getYawInput() / 2;
        if (plane.isOnGround()) {
            this.backWheel.yaw = -plane.getYawVelocity() / 2;
        }
        this.leftAileron.pitch = -plane.getRollInput() / 8;
        this.rightAileron.pitch = plane.getRollInput() / 8;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        plane.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
