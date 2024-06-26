package aqario.conveyance.mixin.client;

import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Axis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {
    @Inject(method = "setupTransforms", at = @At("TAIL"))
    public void conveyance$bindPassengerRotation(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo ci) {
        if (entity.getRootVehicle() != entity && entity.getRootVehicle() instanceof MonoplaneEntity plane) {
            matrices.multiply(Axis.X_POSITIVE.rotationDegrees(-plane.getPitch(tickDelta)));
            matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(-plane.getRoll(tickDelta)));
        }
    }
}
