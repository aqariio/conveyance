package aqario.conveyance.mixin.client;

import aqario.conveyance.client.renderer.PlaneCamera;
import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Axis;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    private Camera camera;

    @Shadow @Final
    MinecraftClient client;

    @Inject(
        method = "renderWorld",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;",
            ordinal = 2
        )
    )
    public void conveyance$rotateCamera(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        Entity entity = camera.getFocusedEntity();
        if (entity != null && entity.getRootVehicle() instanceof MonoplaneEntity plane) {
            // rotate camera
//            matrices.multiply(Axis.X_POSITIVE.rotationDegrees(((PlaneCamera) camera).conveyance$getPitch()));
            matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(((PlaneCamera) camera).conveyance$getRoll()));

            // eye offset
            float eye = entity.getStandingEyeHeight();

            // transform eye offset to match plane rotation
            Vector3f offset = new Vector3f(0, -eye, 0);
            Quaternionf quaternion = Axis.X_POSITIVE.rotationDegrees(0.0f);
            quaternion.mul(Axis.Y_POSITIVE.rotationDegrees(-plane.getYaw(tickDelta)));
            quaternion.mul(Axis.X_POSITIVE.rotationDegrees(plane.getPitch(tickDelta)));
            quaternion.mul(Axis.Z_POSITIVE.rotationDegrees(plane.getRoll(tickDelta)));
            offset.rotate(quaternion);

            // camera offset
            matrices.multiply(Axis.X_POSITIVE.rotationDegrees(camera.getPitch()));
            matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(camera.getYaw() + 180.0f));
            matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(((PlaneCamera) camera).conveyance$getRoll()));
            matrices.translate(offset.x(), offset.y() + eye, offset.z());
            matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(-((PlaneCamera) camera).conveyance$getRoll()));
            matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(-camera.getYaw() - 180.0f));
            matrices.multiply(Axis.X_POSITIVE.rotationDegrees(-camera.getPitch()));
        }
    }
}
