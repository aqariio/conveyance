package aqario.conveyance.common.entity.vehicle;

import aqario.conveyance.common.entity.damage.ConveyanceDamageTypes;
import aqario.conveyance.common.entity.part.MonoplaneMultipartEntity;
import aqario.conveyance.common.entity.part.MonoplanePart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.quiltmc.qsl.entity.multipart.api.EntityPart;

import java.text.DecimalFormat;
import java.util.List;

public class MonoplaneEntity extends VehicleEntity implements MonoplaneMultipartEntity {
    private static final float GRAVITATIONAL_CONSTANT = 0.04F;
    private final EntityDimensions visibleDimensions;
    private final MonoplanePart[] parts;
    public final MonoplanePart nose;
    public final MonoplanePart fuselage1;
    public final MonoplanePart fuselage2;
    public final MonoplanePart fuselage3;
    private final MonoplanePart fuselage4;
    private final MonoplanePart cockpit;
    private final MonoplanePart tail1;
    private final MonoplanePart tail2;
    private final MonoplanePart tail3;
    private final MonoplanePart leftWing;
    private final MonoplanePart rightWing;
    private final MonoplanePart leftWheel;
    private final MonoplanePart rightWheel;
    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingForward;
    private boolean pressingBack;
    private boolean pressingEngineUp;
    private boolean pressingEngineDown;
    public float prevRoll;
    private float roll;
    public float bodyYaw;
    private float velocityDecay;
    private float pitchVelocity;
    private float yawVelocity;
    private float rollVelocity;
    private float pitchInput;
    private float yawInput;
    private float rollInput;
    private double waterLevel;
    private double x;
    private double y;
    private double z;
    private double clientYaw;
    private double clientPitch;
    private int lerpSteps;

    public MonoplaneEntity(EntityType<? extends MonoplaneEntity> type, World world) {
        super(type, world);
        this.nose = new MonoplanePart(this, 1.4F, 1.4F, new Vec3d(0.0, -0.5, 3.75), new Vec3d(0.0, 0.0, 0.0), MonoplanePart.ModuleType.PROPELLER);
        this.fuselage1 = new MonoplanePart(this, 1.4F, 1.4F, new Vec3d(0.0, -0.5, 2.75), new Vec3d(0.0, 0.0, 0.0));
        this.fuselage2 = new MonoplanePart(this, 1.4F, 1.4F, new Vec3d(0.0, -0.5, 1.75), new Vec3d(0.0, 0.0, 0.0));
        this.fuselage3 = new MonoplanePart(this, 1.4F, 1.4F, new Vec3d(0.0, -0.5, 0.75), new Vec3d(0.0, 0.0, 0.0));
        this.cockpit = new MonoplanePart(this, 1.8F, 1.8F, new Vec3d(0.0, -0.5, -0.25), new Vec3d(0.0, 0.0, 0.0), MonoplanePart.ModuleType.COCKPIT);
        this.fuselage4 = new MonoplanePart(this, 1.4F, 1.4F, new Vec3d(0.0, -0.5, -0.75), new Vec3d(0.0, 0.0, 0.0));
        this.tail1 = new MonoplanePart(this, 1.4F, 1.4F, new Vec3d(0.0, -0.15, -1.75), new Vec3d(0.0, 0.0, 0.0));
        this.tail2 = new MonoplanePart(this, 1.4F, 1.4F, new Vec3d(0.0, -0.15, -2.75), new Vec3d(0.0, 0.0, 0.0));
        this.tail3 = new MonoplanePart(this, 3.0F, 3.0F, new Vec3d(0.0, -0.5, -4.5), new Vec3d(0.0, 0.0, 0.0));
        this.leftWing = new MonoplanePart(this, 4.0F, 0.5F, new Vec3d(2.75, -0.5, 0.0), new Vec3d(0.0, 0.0, 0.0));
        this.rightWing = new MonoplanePart(this, 4.0F, 0.5F, new Vec3d(-2.75, -0.5, 0.0), new Vec3d(0.0, 0.0, 0.0));
        this.leftWheel = new MonoplanePart(this, 1.0F, 1.0F, new Vec3d(0.75, -1.5, 1.0), new Vec3d(0.0, 0.0, 0.0));
        this.rightWheel = new MonoplanePart(this, 1.0F, 1.0F, new Vec3d(-0.75, -1.5, 1.0), new Vec3d(0.0, 0.0, 0.0));
        this.parts = new MonoplanePart[]{
            this.nose,
            this.fuselage1,
            this.fuselage2,
            this.fuselage3,
            this.fuselage4,
            this.cockpit,
            this.tail1,
            this.tail2,
            this.tail3,
            this.leftWing,
            this.rightWing,
            this.leftWheel,
            this.rightWheel
        };
        this.visibleDimensions = new EntityDimensions(10.0F, 10.0F, true);
        this.noClip = false;
        this.ignoreCameraFrustum = true;
        this.setStepHeight(1.0F);
    }

    @Override
    public Box getVisibilityBoundingBox() {
        return this.visibleDimensions.getBoxAt(this.getPos());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.put("Rotation", this.toNbtList(this.getPitch(), this.getYaw(), this.getRoll()));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        NbtList rotation = nbt.getList("Rotation", NbtElement.FLOAT_TYPE);
        this.setPitch(rotation.getFloat(0));
        this.setYaw(rotation.getFloat(1));
        this.setRoll(rotation.getFloat(2));
    }

    @Override
    public double getMountedHeightOffset() {
        return -0.2;
    }

    public double getMountedXOffset() {
        return -0.65;
    }

    @Override
    public LivingEntity getPrimaryPassenger() {
        return (LivingEntity) this.getFirstPassenger();
    }

    @Override
    public Direction getMovementDirection() {
        return this.getHorizontalFacing().rotateYClockwise();
    }

    @Override
    public void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Vec3d vec3d = new Vec3d(this.getMountedXOffset(), this.getMountedHeightOffset() + passenger.getHeightOffset(), 0.0)
            .rotateX(-this.getRoll() * (float) (Math.PI / 180.0))
            .rotateZ(this.getPitch() * (float) (Math.PI / 180.0))
            .rotateY(-this.getYaw() * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
        positionUpdater.accept(passenger, this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
        passenger.setYaw(passenger.getYaw() + this.yawVelocity);
        passenger.setPitch(passenger.getPitch() + this.pitchVelocity);
        this.clampEntityYaw(passenger);
    }

    @Override
    public void onPassengerLookAround(Entity passenger) {
        this.clampEntityYaw(passenger);
    }

    protected void clampEntityYaw(Entity entity) {
        entity.setBodyYaw(this.getYaw());
        float f = MathHelper.wrapDegrees(entity.getYaw() - this.getYaw());
        float g = MathHelper.clamp(f, -135.0F, 135.0F);
        entity.prevYaw += g - f;
        entity.setYaw(entity.getYaw() + g - f);
        entity.setHeadYaw(entity.getYaw());

//        float h = MathHelper.wrapDegrees(entity.getPitch() - this.getPitch());
//        float i = MathHelper.clamp(h, -75.0F, 75.0F);
//        entity.prevPitch += i - h;
//        entity.setPitch(entity.getPitch() + i - h);
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        if (!this.isOnGround()) {
            return new Vec3d(this.getX(), this.cockpit.getBoundingBox().minY - 0.5, this.getZ());
        }
        return new Vec3d(this.getX(), this.cockpit.getBoundingBox().maxY + 0.1, this.getZ());
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.clientYaw = yaw;
        this.clientPitch = pitch;
        this.lerpSteps = 10;
    }

    private void addGlidingEffects() {
        double gravity = this.getGravity();
        Vec3d velocity = this.getVelocity();
        if (velocity.y > -0.5) {
            this.fallDistance = 1.0F;
        }

        Vec3d rotVec = this.getRotationVector();
        float pitch = this.getPitch() * (float) (Math.PI / 180.0);
        double rot = Math.sqrt(rotVec.x * rotVec.x + rotVec.z * rotVec.z);
        double j = velocity.horizontalLength();
        double k = rotVec.length();
        double l = Math.cos(pitch);
        l = l * l * Math.min(1.0, k / 0.4);
        velocity = this.getVelocity().add(0.0, gravity * (-1.0 + l * 0.75), 0.0);
        if (velocity.y < 0.0 && rot > 0.0) {
            double m = velocity.y * -0.1 * l;
            velocity = velocity.add(rotVec.x * m / rot, m, rotVec.z * m / rot);
        }

        if (pitch < 0.0F && rot > 0.0) {
            double m = j * (double) (-MathHelper.sin(pitch)) * 0.04;
            velocity = velocity.add(-rotVec.x * m / rot, m * 3.2, -rotVec.z * m / rot);
        }

        if (rot > 0.0) {
            velocity = velocity.add((rotVec.x / rot * j - velocity.x) * 0.1, 0.0, (rotVec.z / rot * j - velocity.z) * 0.1);
        }

        this.setVelocity(velocity.multiply(0.995F, 0.995F, 0.995F));
        this.move(MovementType.SELF, this.getVelocity());
        if ((this.horizontalCollision || this.verticalCollision) && !this.getWorld().isClient) {
            double m = this.getVelocity().length();
            double n = j - m;
            float o = (float) (n * 10.0 - 3.0);
            if (o > 0.0F) {
                this.damage(this.getDamageSources().flyIntoWall(), o);
            }
        }

        if (this.isOnGround() && !this.getWorld().isClient) {
            this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, false);
        }
    }

    @Override
    public void baseTick() {
        this.prevRoll = this.getRoll();
        super.baseTick();
    }

    @Override
    public void tick() {
//		this.lastLocation = this.location;
//		this.location = this.checkLocation();
//		if (this.location != MonoplaneEntity.Location.UNDER_WATER && this.location != MonoplaneEntity.Location.UNDER_FLOWING_WATER) {
//			this.ticksUnderwater = 0.0F;
//		} else {
//			++this.ticksUnderwater;
//		}
//
//		if (!this.getWorld().isClient && this.ticksUnderwater >= 60.0F) {
//			this.removeAllPassengers();
//		}

        if (this.getWorld().isClient() && !getPassengerList().isEmpty()) {
            Entity pilot = getPassengerList().get(0);
            MinecraftClient client = MinecraftClient.getInstance();
            if (pilot instanceof ClientPlayerEntity) {
                this.setInputs(
                    client.options.leftKey.isPressed(),
                    client.options.rightKey.isPressed(),
                    client.options.forwardKey.isPressed(),
                    client.options.backKey.isPressed(),
                    client.options.jumpKey.isPressed(),
                    client.options.sprintKey.isPressed()
                );
            }
        }
        super.tick();
        if (!this.isRemoved()) {
            this.tickMovement();
        }
        this.lerpTick();
        if (this.getPrimaryPassenger() instanceof PlayerEntity player && this.getWorld().isClient) {
            // Flight Data
            player.sendMessage(
                Text.literal(this.isOnGround() ? "On ground" : "Flying")
                    .append(", Speed: " + new DecimalFormat("#").format(this.getVelocity().length() * 72) + " km/h, ")
                    .append("Altitude: " + new DecimalFormat("#").format(this.getPos().getY()) + " m, ")
                    .append("Pitch: " + new DecimalFormat("#").format(this.getPitch()) + "\u00B0 ")
                    .append("Yaw: " + new DecimalFormat("#").format(this.getYaw()) + "\u00B0 ")
                    .append("Roll: " + new DecimalFormat("#").format(this.getRoll()) + "\u00B0"),
                true
            );
        }
        if (this.isLogicalSideForUpdatingMovement()) {
            this.updateVelocity();
            if (this.getWorld().isClient) {
                this.updateController();
            }

            this.move(MovementType.SELF, this.getVelocity());
        }
        for (MonoplanePart part : this.parts) {
            part.checkBlockCollision();
        }
//		this.checkBlockCollision();
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        if (this.noClip) {
            this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
        }
        else {
            this.wasOnFire = this.isOnFire();
            if (movementType == MovementType.PISTON) {
                movement = this.adjustMovementForPiston(movement);
                if (movement.equals(Vec3d.ZERO)) {
                    return;
                }
            }

            this.getWorld().getProfiler().push("move");
            if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
                movement = movement.multiply(this.movementMultiplier);
                this.movementMultiplier = Vec3d.ZERO;
                this.setVelocity(Vec3d.ZERO);
            }

            Vec3d vec3d = this.adjustMovementForMultipartCollisions(movement);
            double d = vec3d.lengthSquared();
            if (d > 1.0E-7) {
                if (this.fallDistance != 0.0F && d >= 1.0) {
                    BlockHitResult blockHitResult = this.getWorld()
                        .raycast(
                            new RaycastContext(this.getPos(), this.getPos().add(vec3d), RaycastContext.ShapeType.FALLDAMAGE_RESETTING, RaycastContext.FluidHandling.WATER, this)
                        );
                    if (blockHitResult.getType() != HitResult.Type.MISS) {
                        this.resetFallDistance();
                    }
                }

                this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
            }

            this.getWorld().getProfiler().pop();
            this.getWorld().getProfiler().push("rest");
            boolean bl = !MathHelper.approximatelyEquals(movement.x, vec3d.x);
            boolean bl2 = !MathHelper.approximatelyEquals(movement.z, vec3d.z);
            this.horizontalCollision = bl || bl2;
            this.verticalCollision = movement.y != vec3d.y;
            this.verticalCollisionBelow = this.verticalCollision && movement.y < 0.0;
            if (this.horizontalCollision) {
                this.minorHorizontalCollision = this.hasCollidedSoftly(vec3d);
            }
            else {
                this.minorHorizontalCollision = false;
            }

            this.setOnGroundWithMovement(this.verticalCollisionBelow, vec3d);
            BlockPos blockPos = this.getLandingPosition();
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            this.fall(vec3d.y, this.isOnGround(), blockState, blockPos);
            if (this.isRemoved()) {
                this.getWorld().getProfiler().pop();
            }
            else {
                if (this.horizontalCollision) {
                    Vec3d vec3d2 = this.getVelocity();
                    this.setVelocity(bl ? 0.0 : vec3d2.x, vec3d2.y, bl2 ? 0.0 : vec3d2.z);
                }

                Block block = blockState.getBlock();
                if (movement.y != vec3d.y) {
                    block.onEntityLand(this.getWorld(), this);
                }

                if (this.isOnGround()) {
                    block.onSteppedOn(this.getWorld(), blockPos, blockState, this);
                }

                this.tryCheckBlockCollision();
                float h = this.getVelocityMultiplier();
                this.setVelocity(this.getVelocity().multiply(h, 1.0, h));
                if (this.getWorld()
                    .getStatesInBoxIfLoaded(this.getBoundingBox().contract(1.0E-6))
                    .noneMatch(state -> state.isIn(BlockTags.FIRE) || state.isOf(Blocks.LAVA))) {
                    if (this.wasOnFire && (this.inPowderSnow || this.isWet())) {
                        this.playExtinguishSound();
                    }
                }

                if (this.isOnFire() && (this.inPowderSnow || this.isWet())) {
                    this.setFireTicks(-this.getBurningDuration());
                }

                this.getWorld().getProfiler().pop();
            }
        }
    }

    private Vec3d adjustMovementForMultipartCollisions(Vec3d movement) {
        Vec3d adjusted = movement;
        for (MonoplanePart part : this.parts) {
            Box box = part.getBoundingBox();
            List<VoxelShape> list = this.getWorld().getEntityCollisions(this, box.stretch(movement));
            Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : adjustSingleAxisMovementForCollisions(this, movement, box, this.getWorld(), list);
            boolean bl = movement.x != vec3d.x;
            boolean bl2 = movement.y != vec3d.y;
            boolean bl3 = movement.z != vec3d.z;
            boolean bl4 = this.isOnGround() || bl2 && movement.y < 0.0;
            if (this.getStepHeight() > 0.0F && bl4 && (bl || bl3)) {
                Vec3d vec3d2 = adjustSingleAxisMovementForCollisions(this, new Vec3d(movement.x, this.getStepHeight(), movement.z), box, this.getWorld(), list);
                Vec3d vec3d3 = adjustSingleAxisMovementForCollisions(
                    this, new Vec3d(0.0, this.getStepHeight(), 0.0), box.stretch(movement.x, 0.0, movement.z), this.getWorld(), list
                );
                if (vec3d3.y < (double) this.getStepHeight()) {
                    Vec3d vec3d4 = adjustSingleAxisMovementForCollisions(this, new Vec3d(movement.x, 0.0, movement.z), box.offset(vec3d3), this.getWorld(), list).add(vec3d3);
                    if (vec3d4.horizontalLengthSquared() > vec3d2.horizontalLengthSquared()) {
                        vec3d2 = vec3d4;
                    }
                }

                if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
                    return vec3d2.add(adjustSingleAxisMovementForCollisions(this, new Vec3d(0.0, -vec3d2.y + movement.y, 0.0), box.offset(vec3d2), this.getWorld(), list));
                }
            }
            if (Math.abs(vec3d.x) < Math.abs(adjusted.x)) {
                adjusted = new Vec3d(vec3d.x, adjusted.y, adjusted.z);
            }
            if (Math.abs(vec3d.y) < Math.abs(adjusted.y)) {
                adjusted = new Vec3d(adjusted.x, vec3d.y, adjusted.z);
            }
            if (Math.abs(vec3d.z) < Math.abs(adjusted.z)) {
                adjusted = new Vec3d(adjusted.x, adjusted.y, vec3d.z);
            }
//            System.out.println("Prev: " + vec3d);
//            System.out.println("Adjusted: " + adjusted);
        }

        return adjusted;
    }

    private void lerpTick() {
        if (this.isLogicalSideForUpdatingMovement()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d = this.getX() + (this.x - this.getX()) / (double) this.lerpSteps;
            double e = this.getY() + (this.y - this.getY()) / (double) this.lerpSteps;
            double f = this.getZ() + (this.z - this.getZ()) / (double) this.lerpSteps;
            double g = MathHelper.wrapDegrees(this.clientYaw - (double) this.getYaw());
            this.setYaw(this.getYaw() + (float) g / (float) this.lerpSteps);
            this.setPitch(this.getPitch() + (float) (this.clientPitch - (double) this.getPitch()) / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d, e, f);
            this.setRotation(this.getYaw(), this.getPitch());
        }
    }

    protected float getGravity() {
        return this.hasNoGravity() ? 0.0F : GRAVITATIONAL_CONSTANT;
    }

    public void tickMovement() {
//		if (!this.getWorld().isClient) {
//			boolean collides = false;
//			for (MonoplanePart part : this.parts) {
//				if (part.wouldCollideWithBlocks(Vec3d.ZERO, getPitch(), getYaw(), 0.0F)) {
//					collides = true;
//					break;
//				}
//			}
//			if (collides) {
//				setPosition(getPos().add(0.0, 0.01D, 0.0));
//			}
//		}
        Vec3d vec3d = this.getVelocity();
        double h = vec3d.x;
        double i = vec3d.y;
        double j = vec3d.z;
        if (Math.abs(vec3d.x) < 0.003) {
            h = 0.0;
        }

        if (Math.abs(vec3d.y) < 0.003) {
            i = 0.0;
        }

        if (Math.abs(vec3d.z) < 0.003) {
            j = 0.0;
        }
        this.setVelocity(h, i, j);

        this.bodyYaw = this.getYaw();
        Vec3d[] vec3ds = new Vec3d[this.parts.length];

        for (int s = 0; s < this.parts.length; ++s) {
            vec3ds[s] = new Vec3d(this.parts[s].getX(), this.parts[s].getY(), this.parts[s].getZ());
        }

        if (!this.getWorld().isClient) {
            this.damageLivingEntities(this.getWorld().getOtherEntities(this, this.nose.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR), 2.0F);
            if (this.getVelocity().length() > 0.25F) {
                for (MonoplanePart part : this.parts) {
                    this.damageLivingEntities(this.getWorld().getOtherEntities(this, part.getBoundingBox().expand(0.5), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR), MathHelper.ceil(MathHelper.clamp(this.getVelocity().length() * 2.0, 0.0, 2.147483647E9)));
                }
            }
        }

        for (MonoplanePart part : this.parts) {
            this.rotatePart(part, (float) (-getPitch() * Math.PI / 180.0F), (float) (-getYaw() * Math.PI / 180.0F), (float) (-getRoll() * Math.PI / 180.0F));
        }

        for (int ac = 0; ac < this.parts.length; ++ac) {
            this.parts[ac].prevX = vec3ds[ac].x;
            this.parts[ac].prevY = vec3ds[ac].y;
            this.parts[ac].prevZ = vec3ds[ac].z;
            this.parts[ac].lastRenderX = vec3ds[ac].x;
            this.parts[ac].lastRenderY = vec3ds[ac].y;
            this.parts[ac].lastRenderZ = vec3ds[ac].z;
        }
    }

    public void rotatePart(MonoplanePart part, float x, float y, float z) {
        Vec3d difference = part.getAbsolutePosition().subtract(part.getAbsolutePivot());
        difference = difference.rotateZ(z).rotateX(x).rotateY(y);
        Vec3d transformedPos = part.getAbsolutePivot().subtract(part.getAbsolutePosition()).add(difference);
        part.move(transformedPos);
    }

    private void damageLivingEntities(List<Entity> entities, float amount) {
        for (Entity entity : entities) {
            if (!this.getPassengerList().contains(entity) && entity instanceof LivingEntity) {
                entity.damage(ConveyanceDamageTypes.of(this.getWorld(), ConveyanceDamageTypes.PROPELLER, this, null), amount);
            }
        }
    }

    private void updateController() {
        if (this.hasPassengers()) {
            double planeVelocity = this.getVelocity().horizontalLength();
            float velocity = 0.0F;

            if (this.pressingLeft) {
                if (this.isOnGround()) {
                    this.yawInput -= 0.1F;
                }
                this.rollInput -= 1.0F;
            }

            if (this.pressingRight) {
                if (this.isOnGround()) {
                    this.yawInput += 0.1F;
                }
                this.rollInput += 1.0F;
            }

            if (this.pressingBack) {
                this.yawInput += 0.3F * this.roll / 90;
                this.pitchInput -= 0.8F * (90 - Math.abs(this.roll)) / 90;
            }

            if (this.pressingForward) {
                this.pitchInput += 0.8F;
            }

            if (this.pressingEngineUp) {
                velocity += this.isOnGround() ? 0.005F : 0.015F;
            }

            if (this.pressingEngineDown) {
                if (this.isOnGround()) {
                    Vec3d vec3d = this.getVelocity();
                    if (this.getVelocity().horizontalLength() < 0.15) {
                        vec3d = vec3d.multiply(0.94, 1, 0.94);
                    }
                    else if (this.getVelocity().horizontalLength() < 0.8) {
                        vec3d = vec3d.multiply(0.98, 1, 0.98);
                    }
                    else {
                        vec3d = vec3d.multiply(0.99, 1, 0.99);
                    }
                    this.setVelocity(vec3d);
                }
            }

//			if (this.pressingBreak/* && this.isOnGround()*/) {
////				velocity -= 0.005F;
//				Vec3d vec3d = this.getVelocity();
//				this.setVelocity(vec3d.x * 0.98D, vec3d.y, vec3d.z * 0.98D);
//			}

//			for (MonoplanePart part : this.parts) {
//				if (part.wouldCollideWithBlocks(getVelocity().add((MathHelper.sin(-getYaw() * 0.017453292F) * velocity), 0, (MathHelper.cos(getYaw() * 0.017453292F) * velocity)), 0.0F, getYaw() + this.yawVelocity, 0.0F)) {
//					setVelocity(0.0, 0.0, 0.0);
//					this.yawVelocity = 0.0F;
//					return;
//				}
//			}

            this.pitchInput = MathHelper.clamp(this.pitchInput, -2F, 2F);
            this.yawInput = this.isOnGround() ? MathHelper.clamp(this.yawInput, -0.5F, 0.5F) : MathHelper.clamp(this.yawInput, -1F, 1F);
            this.rollInput = MathHelper.clamp(this.rollInput, -5F, 5F);

            this.pitchVelocity = this.pitchInput;
            this.rollVelocity = this.rollInput;
//            if (this.isOnGround()) {
            this.yawVelocity = this.yawInput;
//            }

            if (this.getPitch() < 90.0F) {
                this.pitchVelocity += 0.01F * (Math.abs(this.getRoll()) / 4);
            }
            this.yawVelocity += MathHelper.clamp(0.02F * (this.getRoll() / 3), -0.5F, 0.5F);

            if (this.isOnGround()) {
                this.pitchVelocity *= (float) Math.min(1, planeVelocity / 10);
                this.rollVelocity = 0;
                this.yawVelocity *= (float) Math.min(2, planeVelocity / 0.1);
            }
//            else {
//                this.yawInput = 0;
//            }
            this.setPitch(MathHelper.wrapDegrees(this.getPitch() + this.pitchVelocity));
            this.setYaw(MathHelper.wrapDegrees(this.getYaw() + this.yawVelocity));
            this.setRoll(MathHelper.wrapDegrees(this.getRoll() + this.rollVelocity));


            this.setVelocity(
                this.getVelocity().add(
                    MathHelper.sin(-this.getYaw() * (float) (Math.PI / 180.0)) * velocity, 0.0, MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * velocity
                )
            );
        }
    }

    public void updateVelocity() {
        double gravity = this.getGravity();
        double lift = this.getVelocity().horizontalLength();
        if (this.isOnGround()) {
            this.velocityDecay = 0.9999F;
            this.setRoll(0.0F);
        }
        else {
            this.velocityDecay = 0.999F;
            this.addGlidingEffects();
        }
        float pitchVelocityDecay = 0.7F;
        float yawVelocityDecay = this.isOnGround() ? 0.985F : 0.7F;
        float rollVelocityDecay = 0.6F;
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * this.velocityDecay, vec3d.y - gravity, vec3d.z * this.velocityDecay);

        if (/*-gravity + Math.min(lift, 0.040001F) < 0*/this.isOnGround()) {
            this.setPitch(Math.max(this.getPitch() - (2F), -11F));
        }

        if (this.getVelocity().horizontalLength() > this.getTakeoffSpeed()) {
            vec3d = this.getVelocity();
            this.setVelocity(vec3d.x, vec3d.y + Math.min(lift, 0.040001F), vec3d.z);
        }
        this.pitchVelocity *= pitchVelocityDecay;
        this.yawVelocity *= yawVelocityDecay;
        this.rollVelocity *= rollVelocityDecay;
        this.pitchInput *= pitchVelocityDecay;
        this.yawInput *= yawVelocityDecay;
        this.rollInput *= rollVelocityDecay;
    }

    public void setInputs(boolean pressingLeft, boolean pressingRight, boolean pressingForward, boolean pressingBack, boolean pressingEngineUp, boolean pressingEngineDown) {
        this.pressingLeft = pressingLeft;
        this.pressingRight = pressingRight;
        this.pressingForward = pressingForward;
        this.pressingBack = pressingBack;
        this.pressingEngineUp = pressingEngineUp;
        this.pressingEngineDown = pressingEngineDown;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_HURT, 1.0F, 0.5F);
        if (this.hasPassengers() && source.isType(DamageTypes.FLY_INTO_WALL)) {
            for (Entity entity : this.getPassengerList()) {
                entity.damage(source, amount);
            }
        }
        this.isInvulnerableTo(source);
        return false;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    private float getTakeoffSpeed() {
        return 2.0F;
    }

    public float getEngineSpeed() {
        return 1.0F;
    }

    public void setRoll(float roll) {
        if (!Float.isFinite(roll)) {
            Util.logAndPause("Invalid entity rotation: " + roll + ", discarding.");
        }
        else {
            this.roll = roll;
        }
    }

    public float getRoll(float tickDelta) {
        return tickDelta == 1.0F ? this.getRoll() : MathHelper.lerp(tickDelta, this.prevRoll, this.getRoll());
    }

    public float getRoll() {
        return this.roll;
    }

    public float getPitchInput() {
        return this.pitchInput;
    }

    public float getYawInput() {
        return this.yawInput;
    }

    public float getRollInput() {
        return this.rollInput;
    }

    public float getYawVelocity() {
        return this.yawVelocity;
    }

    public MonoplanePart[] getBodyParts() {
        return this.parts;
    }

    @Override
    public EntityPart<?>[] getEntityParts() {
        return this.getBodyParts();
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        MonoplanePart[] monoplaneParts = this.getBodyParts();

        for (int i = 0; i < monoplaneParts.length; ++i) {
            monoplaneParts[i].setId(i + packet.getId());
        }
    }
}
