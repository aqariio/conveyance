package aqario.conveyance.common.entity.vehicle;

import aqario.conveyance.common.entity.damage.ConveyanceDamageTypes;
import aqario.conveyance.common.entity.part.MonoplaneMultipartEntity;
import aqario.conveyance.common.entity.part.MonoplanePart;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.entity.multipart.api.EntityPart;

import java.text.DecimalFormat;
import java.util.List;

public class MonoplaneEntity extends VehicleEntity implements MonoplaneMultipartEntity {
    private static final float GRAVITATIONAL_CONSTANT = -0.04F;
    private final MonoplanePart[] parts;
    public final MonoplanePart nose;
    private final MonoplanePart engine;
    private final MonoplanePart cockpit;
    private final MonoplanePart fuselage;
    private final MonoplanePart tail;
    private final MonoplanePart rightWing;
    private final MonoplanePart leftWing;
    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingForward;
    private boolean pressingBack;
    private boolean pressingEngineUp;
    private boolean pressingEngineDown;
    public float prevWingPosition;
    public float wingPosition;
    protected int bodyTrackingIncrements;
    protected double serverX;
    protected double serverY;
    protected double serverZ;
    protected double serverYaw;
    protected double serverPitch;
    protected double serverHeadYaw;
    public float prevRoll;
    private float roll;
    public float bodyYaw;
    public float prevBodyYaw;
    public float headYaw;
    public float prevHeadYaw;
    public final double[][] segmentCircularBuffer = new double[64][3];
    public int latestSegment = -1;
    public float yawAcceleration;
    private float velocityDecay;
    private float pitchVelocity;
    private float yawVelocity;
    private float rollVelocity;
    private float pitchInput;
    private float yawInput;
    private float rollInput;
    private float velocityMovement;
    private float yawMovement;
    private float ticksUnderwater;
    private double waterLevel;
    private float landFriction;
    private double x;
    private double y;
    private double z;
    private double clientYaw;
    private double clientPitch;
    private int lerpSteps;
    private MonoplaneEntity.Location location;
    private MonoplaneEntity.Location lastLocation;

    public MonoplaneEntity(EntityType<? extends MonoplaneEntity> type, World world) {
        super(type, world);
        this.nose = new MonoplanePart(this, 2.5F, 2.5F, new Vec3d(0.0D, 0.0D, 4.5D), new Vec3d(0.0D, 0.0D, 0.0D), MonoplanePart.ModuleType.PROPELLER);
        this.engine = new MonoplanePart(this, 2.5F, 2.5F, new Vec3d(0.0D, 0.0D, 2.5D), new Vec3d(0.0D, 0.0D, 0.0D), MonoplanePart.ModuleType.COCKPIT);
        this.cockpit = new MonoplanePart(this, 2.5F, 2.5F, new Vec3d(0.0D, 0.0D, 0.5D), new Vec3d(0.0D, 0.0D, 0.0D));
        this.fuselage = new MonoplanePart(this, 2.0F, 2.0F, new Vec3d(0.0D, 0.25D, -1.5D), new Vec3d(0.0D, 0.0D, 0.0D));
        this.tail = new MonoplanePart(this, 3.0F, 3.0F, new Vec3d(0.0D, 0.5D, -3.75D), new Vec3d(0.0D, 0.0D, 0.0D));
        this.rightWing = new MonoplanePart(this, 4.0F, 0.5F, new Vec3d(-3.0D, 0.5D, 1.5D), new Vec3d(0.0D, 0.0D, 0.0D));
        this.leftWing = new MonoplanePart(this, 4.0F, 0.5F, new Vec3d(3.0D, 0.5D, 1.5D), new Vec3d(0.0D, 0.0D, 0.0D));
        this.parts = new MonoplanePart[]{
            this.nose,
            this.engine,
            this.cockpit,
            this.fuselage,
            this.tail,
            this.rightWing,
            this.leftWing
        };
        this.noClip = false;
    }

    public double[] getSegmentProperties(int segmentNumber, float tickDelta) {
        if (!this.isAlive()) {
            tickDelta = 0.0F;
        }

        tickDelta = 1.0F - tickDelta;
        int i = this.latestSegment - segmentNumber & 63;
        int j = this.latestSegment - segmentNumber - 1 & 63;
        double[] ds = new double[3];
        double d = this.segmentCircularBuffer[i][0];
        double e = MathHelper.wrapDegrees(this.segmentCircularBuffer[j][0] - d);
        ds[0] = d + e * (double) tickDelta;
        d = this.segmentCircularBuffer[i][1];
        e = this.segmentCircularBuffer[j][1] - d;
        ds[1] = d + e * (double) tickDelta;
        ds[2] = MathHelper.lerp(tickDelta, this.segmentCircularBuffer[i][2], this.segmentCircularBuffer[j][2]);
        return ds;
    }

    @Override
    public double getMountedHeightOffset() {
        return 1.0;
    }

    public double getMountedXOffset() {
        return 0.6;
    }

    @Override
    public LivingEntity getPrimaryPassenger() {
        return (LivingEntity) this.getFirstPassenger();
    }

    @Override
    public Direction getMovementDirection() {
        return getHorizontalFacing().rotateYClockwise();
    }

    @Override
    public void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float g = (float) ((this.isRemoved() ? 0.01F : this.getMountedHeightOffset()) + passenger.getHeightOffset());

        int size = getPassengerList().size() - 1;
        Vec3d vec3d = new Vec3d(this.getMountedXOffset(), 0.0, 0.0).rotateY(-this.getYaw() * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
        positionUpdater.accept(passenger, this.getX() + vec3d.x, this.getY() + (double) g, this.getZ() + vec3d.z);
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

    private float wrapYawChange(double yawDegrees) {
        return (float) MathHelper.wrapDegrees(yawDegrees);
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
        double gravity = 0.08;
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
//				setInputs(
//					getMovementMultiplier(
//						client.options.rightKey.isPressed(),
//						client.options.leftKey.isPressed()
//					),
//					getMovementMultiplier(
//						client.options.forwardKey.isPressed(),
//						client.options.backKey.isPressed()
//					),
//					getMovementMultiplier(
//						client.options.jumpKey.isPressed(),
//						client.options.sprintKey.isPressed()
//					)
//				);
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
            player.sendMessage(Text.literal(this.isOnGround() ? "On ground" : "Flying").append(Text.literal(", Speed: ")).append(String.valueOf(new DecimalFormat("#").format(this.getVelocity().length() * 20 * 3.6))).append(" km/h"), true);
        }
        if (this.isLogicalSideForUpdatingMovement()) {
            this.updateVelocity();
            if (this.getWorld().isClient) {
                this.updateController();
            }
            this.move(MovementType.SELF, getVelocity());
        }
        for (MonoplanePart part : this.parts) {
            part.checkBlockCollision();
        }
//		this.checkBlockCollision();
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
//				setPosition(getPos().add(0.0D, 0.01D, 0.0D));
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

//		this.prevWingPosition = this.wingPosition;
//		Vec3d vec3d = this.getVelocity();
//		float g = 0.2F / ((float)vec3d.horizontalLength() * 10.0F + 1.0F);
//		g *= (float)Math.pow(2.0, vec3d.y);
//		this.wingPosition += g;
//
//		this.setYaw(MathHelper.wrapDegrees(this.getYaw()));
//		if (this.latestSegment < 0) {
//			for(int i = 0; i < this.segmentCircularBuffer.length; ++i) {
//				this.segmentCircularBuffer[i][0] = this.getYaw();
//				this.segmentCircularBuffer[i][1] = this.getY();
//			}
//		}
//
//		if (++this.latestSegment == this.segmentCircularBuffer.length) {
//			this.latestSegment = 0;
//		}
//
//		this.segmentCircularBuffer[this.latestSegment][0] = this.getYaw();
//		this.segmentCircularBuffer[this.latestSegment][1] = this.getY();
//		if (this.getWorld().isClient) {
//			if (this.bodyTrackingIncrements > 0) {
//				double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
//				double e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
//				double j = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
//				double k = MathHelper.wrapDegrees(this.serverYaw - (double)this.getYaw());
//				this.setYaw(this.getYaw() + (float)k / (float)this.bodyTrackingIncrements);
//				this.setPitch(this.getPitch() + (float)(this.serverPitch - (double)this.getPitch()) / (float)this.bodyTrackingIncrements);
//				--this.bodyTrackingIncrements;
//				this.setPosition(d, e, j);
//				this.setRotation(this.getYaw(), this.getPitch());
//			}
//		} else {
        this.bodyYaw = this.getYaw();
        Vec3d[] vec3ds = new Vec3d[this.parts.length];

        for (int s = 0; s < this.parts.length; ++s) {
            vec3ds[s] = new Vec3d(this.parts[s].getX(), this.parts[s].getY(), this.parts[s].getZ());
        }

//		float t = (float)(this.getSegmentProperties(5, 1.0F)[1] - this.getSegmentProperties(10, 1.0F)[1]) * 10.0F * (float) (Math.PI / 180.0);
//		float u = MathHelper.cos(t);
//		float v = MathHelper.sin(t);
        float w = this.getYaw() * (float) (Math.PI / 180.0);
        float x = MathHelper.sin(w);
        float y = MathHelper.cos(w);


        float pitchDegrees = this.getPitch() * (float) (Math.PI / 180.0);
        float pitchSin = MathHelper.sin(pitchDegrees);
        float pitchCos = MathHelper.cos(pitchDegrees);

        float w_ = (this.getYaw() - 90) * (float) (Math.PI / 180.0);
        float x_ = MathHelper.sin(w_);
        float y_ = MathHelper.cos(w_);

//		this.movePart(this.nose, x * -4.5F, 0.0, -y * -4.5F);
//		this.movePart(this.engine, x * -2.5F, 0.0, -y * -2.5F);
//		this.movePart(this.cockpit, x * -0.5F, 0.0, -y * -0.5F);
//		this.movePart(this.fuselage, x * 1.5F, 0.25, -y * 1.5F);
//		this.movePart(this.tail, x * 3.75F, 0.5, -y * 3.75F);
//		this.movePart(this.rightWing,  x_ * -(4 - 1), 0.5, y_ * (4 + 1));
//		this.movePart(this.leftWing, y * -3.5F, 0.5, x * -3.5F);

        if (!this.getWorld().isClient) {
            this.damageLivingEntities(this.getWorld().getOtherEntities(this, this.nose.getBoundingBox().expand(0.5), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR), 1.0F);
            if (this.getVelocity().length() > 0.25F) {
                for (MonoplanePart part : this.parts) {
                    this.damageLivingEntities(this.getWorld().getOtherEntities(this, part.getBoundingBox().expand(0.5), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR), MathHelper.ceil(MathHelper.clamp(this.getVelocity().length() * 2.0, 0.0, 2.147483647E9)));
                }
            }
        }

//		float z = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0) - this.yawAcceleration * 0.01F);
//		float aa = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0) - this.yawAcceleration * 0.01F);
//		float ab = this.getHeadVerticalMovement();
//		this.movePart(this.nose, (z * 6.5F * u), (ab + v * 6.5F), -aa * 6.5F * u);
//		this.movePart(this.cockpit, z * 5.5F * u, ab + v * 5.5F, -aa * 5.5F * u);
//		double[] ds = this.getSegmentProperties(5, 1.0F);
//		}

        for (MonoplanePart part : this.parts) {
            this.rotatePart(part, (float) (-getPitch() * Math.PI / 180.0F), (float) (-getYaw() * Math.PI / 180.0F));
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

    public void rotatePart(MonoplanePart part, float x, float y) {
        Vec3d difference = part.getAbsolutePosition().subtract(part.getAbsolutePivot());
        difference = difference.rotateX(x).rotateY(y);
        Vec3d transformedPos = part.getAbsolutePivot().subtract(part.getAbsolutePosition()).add(difference);
        part.move(transformedPos);
    }

    private void damageLivingEntities(List<Entity> entities, float amount) {
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                entity.damage(ConveyanceDamageTypes.of(this.getWorld(), ConveyanceDamageTypes.PROPELLER, this, null), amount);
            }
        }
    }

//	private void launchLivingEntities(List<Entity> entities) {
//		double d = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0;
//		double e = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0;
//
//		for(Entity entity : entities) {
//			if (entity instanceof LivingEntity) {
//				double f = entity.getX() - d;
//				double g = entity.getZ() - e;
//				double h = Math.max(f * f + g * g, 0.1);
//				entity.addVelocity(f / h * 4.0, 0.2F, g / h * 4.0);
//			}
//		}
//	}

    private void updateController() {
        if (this.hasPassengers()) {
            double planeVelocity = getVelocity().horizontalLength();
            float velocity = 0.0F;

            if (this.pressingLeft) {
                this.yawInput -= 0.1F;
                this.rollInput -= 1F;
            }

            if (this.pressingRight) {
                this.yawInput += 0.1F;
                this.rollInput += 1F;
            }

            if (this.pressingBack) {
                this.pitchInput -= 0.5F;
            }

            if (this.pressingForward) {
                this.pitchInput += 0.5F;
            }

            if (this.pressingEngineUp) {
                velocity += 0.005F;
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
//					setVelocity(0.0D, 0.0D, 0.0D);
//					this.yawVelocity = 0.0F;
//					return;
//				}
//			}

            this.pitchInput = MathHelper.clamp(this.pitchInput, -2F, 2F);
            this.yawInput = MathHelper.clamp(this.yawInput, -0.5F, 0.5F);
            this.rollInput = MathHelper.clamp(this.rollInput, -4F, 4F);

            this.pitchVelocity = this.pitchInput;
            this.rollVelocity = this.rollInput;
            if (this.isOnGround()) {
                this.yawVelocity = this.yawInput;
            }

            if (this.isOnGround()) {
                this.pitchVelocity = 0;
                this.rollVelocity = 0;
                this.yawVelocity *= (float) Math.min(2, planeVelocity / 0.1);
            }
            else {
                this.yawInput = 0;
            }
            this.setPitch(this.getPitch() + this.pitchVelocity);
            this.setYaw(this.getYaw() + this.yawVelocity);
            this.setRoll(this.getRoll() + this.rollVelocity);


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
            this.setPitch((getPitch() + 11.0F) * 0.9F - 11.0F);
            this.setRoll(0.0F);
        }
        else {
            this.velocityDecay = 0.999F;
            this.addGlidingEffects();
        }
        float pitchVelocityDecay = 0.9F;
        float yawVelocityDecay = 0.985F;
        float rollVelocityDecay = 0.85F;
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * this.velocityDecay, vec3d.y + gravity/* + Math.min(lift, 0.040001F)*/, vec3d.z * this.velocityDecay);
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
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_HURT, 1.0F, 1.0F);
        if (this.hasPassengers() && source.isType(DamageTypes.FLY_INTO_WALL)) {
            for (Entity entity : this.getPassengerList()) {
                entity.damage(source, amount);
            }
        }
        return super.damage(source, amount);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    private MonoplaneEntity.Location checkLocation() {
        MonoplaneEntity.Location location = this.getUnderWaterLocation();
        if (location != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return location;
        }
        else if (this.checkIfInWater()) {
            return MonoplaneEntity.Location.IN_WATER;
        }
        else {
            float f = this.getGroundFriction();
            if (f > 0.0F) {
                this.landFriction = f;
                return MonoplaneEntity.Location.ON_LAND;
            }
            else {
                return MonoplaneEntity.Location.IN_AIR;
            }
        }
    }

    public float getGroundFriction() {
        Box box = this.getBoundingBox();
        Box box2 = new Box(box.minX, box.minY - 0.001, box.minZ, box.maxX, box.minY, box.maxZ);
        int i = MathHelper.floor(box2.minX) - 1;
        int j = MathHelper.ceil(box2.maxX) + 1;
        int k = MathHelper.floor(box2.minY) - 1;
        int l = MathHelper.ceil(box2.maxY) + 1;
        int m = MathHelper.floor(box2.minZ) - 1;
        int n = MathHelper.ceil(box2.maxZ) + 1;
        VoxelShape voxelShape = VoxelShapes.cuboid(box2);
        float f = 0.0F;
        int o = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int p = i; p < j; ++p) {
            for (int q = m; q < n; ++q) {
                int r = (p != i && p != j - 1 ? 0 : 1) + (q != m && q != n - 1 ? 0 : 1);
                if (r != 2) {
                    for (int s = k; s < l; ++s) {
                        if (r <= 0 || s != k && s != l - 1) {
                            mutable.set(p, s, q);
                            BlockState blockState = this.getWorld().getBlockState(mutable);
                            if (VoxelShapes.matchesAnywhere(
                                blockState.getCollisionShape(this.getWorld(), mutable).offset(p, s, q), voxelShape, BooleanBiFunction.AND
                            )) {
                                f += blockState.getBlock().getSlipperiness();
                                ++o;
                            }
                        }
                    }
                }
            }
        }

        return f / (float) o;
    }

    private boolean checkIfInWater() {
        Box box = this.getBoundingBox();
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.minY + 0.001);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        boolean bl = false;
        this.waterLevel = -Double.MAX_VALUE;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int o = i; o < j; ++o) {
            for (int p = k; p < l; ++p) {
                for (int q = m; q < n; ++q) {
                    mutable.set(o, p, q);
                    FluidState fluidState = this.getWorld().getFluidState(mutable);
                    if (fluidState.isIn(FluidTags.WATER)) {
                        float f = (float) p + fluidState.getHeight(this.getWorld(), mutable);
                        this.waterLevel = Math.max(f, this.waterLevel);
                        bl |= box.minY < (double) f;
                    }
                }
            }
        }

        return bl;
    }

    @Nullable
    private MonoplaneEntity.Location getUnderWaterLocation() {
        Box box = this.getBoundingBox();
        double d = box.maxY + 0.001;
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.maxY);
        int l = MathHelper.ceil(d);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        boolean bl = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int o = i; o < j; ++o) {
            for (int p = k; p < l; ++p) {
                for (int q = m; q < n; ++q) {
                    mutable.set(o, p, q);
                    FluidState fluidState = this.getWorld().getFluidState(mutable);
                    if (fluidState.isIn(FluidTags.WATER) && d < (double) ((float) mutable.getY() + fluidState.getHeight(this.getWorld(), mutable))) {
                        if (!fluidState.isSource()) {
                            return MonoplaneEntity.Location.UNDER_FLOWING_WATER;
                        }

                        bl = true;
                    }
                }
            }
        }

        return bl ? MonoplaneEntity.Location.UNDER_WATER : null;
    }

    private float getTakeoffSpeed() {
        return 2.0F;
    }

    private void movePart(MonoplanePart monoplanePart, double dx, double dy, double dz) {
        monoplanePart.setPosition(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
//		monoplanePart.move(MovementType.SELF, new Vec3d(dx, dy, dz));
    }

    public float getRoll(float tickDelta) {
        return tickDelta == 1.0F ? this.getRoll() : MathHelper.lerp(tickDelta, this.prevRoll, this.getRoll());
    }

    public void setRoll(float roll) {
        if (!Float.isFinite(roll)) {
            Util.logAndPause("Invalid entity rotation: " + roll + ", discarding.");
        }
        else {
            this.roll = roll;
        }
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

    public enum Location {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR
    }
}
