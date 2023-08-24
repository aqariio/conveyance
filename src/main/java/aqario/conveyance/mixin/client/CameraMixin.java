package aqario.conveyance.mixin.client;

import aqario.conveyance.common.entity.vehicle.BiplaneEntity;
import aqario.conveyance.common.entity.vehicle.VehicleEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

	@Inject(method = "update", at = @At("TAIL"))
	public void updateInject(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
		if (thirdPerson && focusedEntity.getVehicle() instanceof VehicleEntity) {
			this.moveBy(-this.clipToSpace(12.0), 0.0, 0.0);
		}
	}

	@Shadow
	protected abstract void moveBy(double x, double y, double z);

	@Shadow
	protected abstract double clipToSpace(double desiredCameraDistance);
}
