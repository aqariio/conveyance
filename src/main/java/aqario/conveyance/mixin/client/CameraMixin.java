package aqario.conveyance.mixin.client;

import aqario.conveyance.client.renderer.PlaneCamera;
import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import aqario.conveyance.common.entity.vehicle.VehicleEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements PlaneCamera {
    @Shadow
    protected abstract void moveBy(double x, double y, double z);

    @Shadow
    protected abstract double clipToSpace(double desiredCameraDistance);

    @Shadow
    private float pitch;

    @Shadow
    private float yaw;

    @Shadow
    @Final
    private Quaternionf rotation;

    @Shadow
    @Final
    private Vector3f horizontalPlane;

    @Shadow
    @Final
    private Vector3f verticalPlane;

    @Shadow
    @Final
    private Vector3f diagonalPlane;

    @Unique
    private float planePitch;

    @Unique
    private float roll;

    @Inject(method = "update", at = @At("TAIL"))
    public void conveyance$zoomOutCamera(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (thirdPerson && focusedEntity.getRootVehicle() instanceof VehicleEntity) {
            this.moveBy(-this.clipToSpace(12.0), 0.0, 0.0);
        }
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0, shift = At.Shift.AFTER))
    public void conveyance$rollCamera(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (focusedEntity.getRootVehicle() instanceof MonoplaneEntity plane) {
            this.setRotation(focusedEntity.getYaw(tickDelta), focusedEntity.getPitch(tickDelta), plane.getRoll(tickDelta));
            this.planePitch = plane.getPitch(tickDelta);
        }
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 1, shift = At.Shift.AFTER))
    public void conveyance$thirdPersonRollCamera(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (focusedEntity.getRootVehicle() instanceof MonoplaneEntity plane) {
            this.setRotation(focusedEntity.getYaw(tickDelta) + 180.0F, -focusedEntity.getPitch(tickDelta), -plane.getRoll(tickDelta));
            this.planePitch = -plane.getPitch(tickDelta);
        }
    }

    @Unique
    protected void setRotation(float yaw, float pitch, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.rotation.rotationYXZ(-yaw * (float) (Math.PI / 180.0), pitch * (float) (Math.PI / 180.0), roll * (float) (Math.PI / 180.0));
        this.horizontalPlane.set(0.0F, 0.0F, 1.0F).rotate(this.rotation);
        this.verticalPlane.set(0.0F, 1.0F, 0.0F).rotate(this.rotation);
        this.diagonalPlane.set(1.0F, 0.0F, 0.0F).rotate(this.rotation);
    }

    @Override
    public float conveyance$getPitch() {
        return this.planePitch;
    }

    @Override
    public float conveyance$getRoll() {
        return this.roll;
    }
}
