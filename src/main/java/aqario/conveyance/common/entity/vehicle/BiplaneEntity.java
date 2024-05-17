package aqario.conveyance.common.entity.vehicle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BiplaneEntity extends VehicleEntity {
    private static final float GRAVITATIONAL_CONSTANT = -0.04F;
    private float velocityDecay;
    private float yawVelocityDecay;
    private float yawVelocity;
    private float velocityMovement;
    private float yawMovement;
    private int lerpSteps;
    private double x;
    private double y;
    private double z;
    private double planeYaw;
    private double planePitch;
    private float pitchMovement;
    //	public final InterpolatedFloat pressingInterpolatedX;
//	public final InterpolatedFloat pressingInterpolatedY;
//	public final InterpolatedFloat pressingInterpolatedZ;
    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingForward;
    private boolean pressingBack;

    public float roll;
    public float prevRoll;

    public BiplaneEntity(EntityType<? extends BiplaneEntity> type, World world) {
        super(type, world);
        this.stepHeight = 0.55f;
    }


    public float getRoll() {
        return roll;
    }

    public float getRoll(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevRoll, getRoll());
    }

    @Override
    public boolean collidesWith(Entity other) {
        return canCollide(this, other);
    }

    public static boolean canCollide(Entity entity, Entity other) {
        return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public double getMountedHeightOffset() {
        return 1;
    }

    public float getMountedXOffset() {
        return 0.8F;
    }

    @Nullable
    @Override
    public Entity getPrimaryPassenger() {
        return this.getFirstPassenger();
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public Direction getMovementDirection() {
        return getHorizontalFacing().rotateYClockwise();
    }

    private float getTakeoffVelocity() {
        return 0.6F;
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float g = (float) ((this.isRemoved() ? 0.01F : this.getMountedHeightOffset()) + passenger.getHeightOffset());

        int size = getPassengerList().size() - 1;
        Vec3d vec3d = new Vec3d(this.getMountedXOffset(), 0.0, 0.0).rotateY(-this.getYaw() * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
        passenger.setPosition(this.getX() + vec3d.x, this.getY() + (double) g, this.getZ() + vec3d.z);
        passenger.setYaw(passenger.getYaw() + this.yawVelocity);
        passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);
        this.copyEntityData(passenger);
        if (passenger instanceof AnimalEntity && size > 1) {
            int angle = passenger.getId() % 2 == 0 ? 90 : 270;
            passenger.setBodyYaw(((AnimalEntity) passenger).bodyYaw + (float) angle);
            passenger.setHeadYaw(passenger.getHeadYaw() + (float) angle);
        }
    }

    @Override
    public void onPassengerLookAround(Entity passenger) {
        this.copyEntityData(passenger);
    }

    protected void copyEntityData(Entity entity) {
        entity.setBodyYaw(this.getYaw());
        float f = MathHelper.wrapDegrees(entity.getYaw() - this.getYaw());
        float g = MathHelper.clamp(f, -105.0F, 105.0F);
        entity.prevYaw += g - f;
        entity.setYaw(entity.getYaw() + g - f);
        entity.setHeadYaw(entity.getYaw());
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.getStackInHand(hand).isEmpty() && player.isSneaking()) {
//			if (!(player.getAbilities()).creativeMode) {
//				player.setStackInHand(hand, ConveyanceItems.MONOPLANE.getDefaultStack());
//			}
//			this.remove(RemovalReason.DISCARDED);
            return ActionResult.SUCCESS;
        }
        else {
            if (!this.world.isClient) {
                return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
            }
            else {
                return ActionResult.SUCCESS;
            }
        }
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.planeYaw = yaw;
        this.planePitch = pitch;
        this.lerpSteps = 10;
    }

    @Override
    public void tick() {
        if (world.isClient() && !getPassengerList().isEmpty()) {
            Entity pilot = getPassengerList().get(0);
            MinecraftClient client = MinecraftClient.getInstance();
            if (pilot instanceof ClientPlayerEntity) {
//				setInputs(
//						getMovementMultiplier(
//								client.options.rightKey.isPressed(),
//								client.options.leftKey.isPressed()
//						),
//						getMovementMultiplier(
//								client.options.forwardKey.isPressed(),
//								client.options.backKey.isPressed()
//						),
//						getMovementMultiplier(
//								client.options.jumpKey.isPressed(),
//								client.options.sprintKey.isPressed()
//						)
//				);
                this.setInputs(client.options.leftKey.isPressed(), client.options.rightKey.isPressed(), client.options.forwardKey.isPressed(), client.options.backKey.isPressed());
            }
        }
        super.tick();
        lerpTick();
        if (this.isLogicalSideForUpdatingMovement()) {
            updateVelocity();
            if (this.world.isClient) {
                updateController();
            }
            move(MovementType.SELF, getVelocity());
        }
        this.checkBlockCollision();

//		if (world.isClient) {
//			pressingInterpolatedX.update(movementX);
//			pressingInterpolatedY.update(movementY);
//			pressingInterpolatedZ.update(movementZ);
//		}
    }

    private void lerpTick() {
        if (this.isLogicalSideForUpdatingMovement()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double lerpX = this.getX() + (this.x - this.getX()) / (double) this.lerpSteps;
            double lerpY = this.getY() + (this.y - this.getY()) / (double) this.lerpSteps;
            double lerpZ = this.getZ() + (this.z - this.getZ()) / (double) this.lerpSteps;
            double g = MathHelper.wrapDegrees(this.planeYaw - (double) this.getYaw());
            this.setYaw(this.getYaw() + (float) g / (float) this.lerpSteps);
            this.setPitch(this.getPitch() + (float) (this.planePitch - (double) this.getPitch()) / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(lerpX, lerpY, lerpZ);
            this.setRotation(this.getYaw(), this.getPitch());
        }
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }

    protected float getGravity() {
        Vec3d direction = new Vec3d(MathHelper.sin(-this.getYaw() * (float) (Math.PI / 180.0)), 0.0, MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)));
        float speed = (float) ((float) getVelocity().length() * (1.0f - Math.abs(direction.getY())));
        return Math.max(0.0f, 1.0f - speed * 1.5f) * GRAVITATIONAL_CONSTANT;
    }

    private void updateController() {
//		if (this.hasPassengers()) {
//			float velocity = 0.0F;
////			if (this.pressingLeft) {
////				--this.yawVelocity;
////			}
////
////			if (this.pressingRight) {
////				++this.yawVelocity;
////			}
//
//			this.yawVelocity += this.yawMovement;
//
////			if (this.pressingRight != this.pressingLeft && !this.pressingForward && !this.pressingBack) {
////				velocity += 0.005F;
////			}
//
//			this.setYaw(this.getYaw() + this.yawVelocity);
////			if (this.pressingForward) {
////				velocity += 0.04F;
////			}
////
////			if (this.pressingBack) {
////				velocity -= 0.005F;
////			}
//
//			velocity += this.velocityMovement * 10.04F;
//
//			this.setVelocity(
//					this.getVelocity()
//							.add(
//									MathHelper.sin(-this.getYaw() * (float) (Math.PI / 180.0)) * velocity, 0.0, MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * velocity
//							)
//			);
//		}
        if (this.hasPassengers()) {
            float velocity = 0.0F;
            if (this.getVelocity().length() > 0) {
                if (this.pressingLeft) {
                    this.yawVelocity -= 0.02F;
                }

                if (this.pressingRight) {
                    this.yawVelocity += 0.02F;
                }
            }

            this.setYaw(this.getYaw() + this.yawVelocity);
            if (this.pressingForward) {
                velocity += 0.01F;
            }

            if (this.pressingBack && this.isOnGround() && this.getVelocity().length() > 0) {
                velocity -= 0.005F;
            }

            this.setVelocity(
                this.getVelocity()
                    .add(
                        MathHelper.sin(-this.getYaw() * (float) (Math.PI / 180.0)) * velocity, 0.0, MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * velocity
                    )
            );
            if (this.getVelocity().length() < this.getTakeoffVelocity()) {
                this.setPitch(this.getPitch() - velocity);
            }
        }
    }

    public void updateVelocity() {
        double gravity = this.hasNoGravity() ? 0.0 : this.getGravity();

        if (this.isOnGround()) {
            this.velocityDecay = 0.995F;
            this.yawVelocityDecay = 0.8F;
//			setPitch((getPitch() + 11.0f) * 0.9f - 11.0f);
        }
        this.velocityDecay = 0.995F;
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double) this.velocityDecay, vec3d.y + gravity, vec3d.z * (double) this.velocityDecay);
        if (this.getVelocity().length() < this.getTakeoffVelocity() && this.getPitch() < (getPitch() + 11.0f) * 0.9f - 11.0f) {
            this.setPitch(this.getPitch() / this.velocityDecay);
        }
        this.yawVelocity *= this.velocityDecay;
    }

    public void setInputs(boolean pressingLeft, boolean pressingRight, boolean pressingForward, boolean pressingBack) {
        this.pressingLeft = pressingLeft;
        this.pressingRight = pressingRight;
        this.pressingForward = pressingForward;
        this.pressingBack = pressingBack;
    }

    public void setInputs(float yaw, float velocity, float pitch) {
        this.yawMovement = yaw;
        this.velocityMovement = velocity;
        this.pitchMovement = pitch;
    }
}
