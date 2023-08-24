package aqario.conveyance.common.entity.vehicle;

import aqario.conveyance.common.entity.part.MonoplaneMultipartEntity;
import aqario.conveyance.common.entity.part.MonoplanePart;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.entity.multipart.api.EntityPart;

import java.util.List;

public class MonoplaneEntity extends VehicleEntity implements MonoplaneMultipartEntity {
	private final MonoplanePart[] parts;
	public final MonoplanePart nose;
	private final MonoplanePart engine;
	private final MonoplanePart cockpit;
	private final MonoplanePart fuselage;
	private final MonoplanePart tail;
	private final MonoplanePart rightWing;
	private final MonoplanePart leftWing;
	public float prevWingPosition;
	public float wingPosition;
	protected int bodyTrackingIncrements;
	protected double serverX;
	protected double serverY;
	protected double serverZ;
	protected double serverYaw;
	protected double serverPitch;
	protected double serverHeadYaw;
	public float bodyYaw;
	public float prevBodyYaw;
	public float headYaw;
	public float prevHeadYaw;
	public final double[][] segmentCircularBuffer = new double[64][3];
	public int latestSegment = -1;
	public float yawAcceleration;
	private float velocityDecay;
	private float yawVelocity;
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
		this.nose = new MonoplanePart(this, "nose", 2.5F, 2.5F, MonoplanePart.ModuleType.PROPELLER);
		this.engine = new MonoplanePart(this, "engine", 2.5F, 2.5F, MonoplanePart.ModuleType.COCKPIT);
		this.cockpit = new MonoplanePart(this, "cockpit", 2.5F, 2.5F);
		this.fuselage = new MonoplanePart(this, "fuselage", 2.0F, 2.0F);
		this.tail = new MonoplanePart(this, "tail", 3.0F, 3.0F);
		this.rightWing = new MonoplanePart(this, "wing", 4.0F, 0.5F);
		this.leftWing = new MonoplanePart(this, "wing", 4.0F, 0.5F);
		this.parts = new MonoplanePart[]{this.nose, this.engine, this.cockpit, this.fuselage, this.tail, this.rightWing, this.leftWing};
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
		ds[0] = d + e * (double)tickDelta;
		d = this.segmentCircularBuffer[i][1];
		e = this.segmentCircularBuffer[j][1] - d;
		ds[1] = d + e * (double)tickDelta;
		ds[2] = MathHelper.lerp(tickDelta, this.segmentCircularBuffer[i][2], this.segmentCircularBuffer[j][2]);
		return ds;
	}

	@Override
	public double getMountedHeightOffset() {
		return 0.625;
	}

	public float getMountedXOffset() {
		return 0.75F;
	}

	@Override
	public void updatePassengerPosition(Entity passenger) {
		if (!this.hasPassenger(passenger)) {
			return;
		}
		float g = (float)((this.isRemoved() ? 0.01F : this.getMountedHeightOffset()) + passenger.getHeightOffset());

		int size = getPassengerList().size() - 1;
		Vec3d vec3d = new Vec3d(this.getMountedXOffset(), 0.0, 0.0).rotateY(-this.getYaw() * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
		passenger.setPosition(this.getX() + vec3d.x, this.getY() + (double)g, this.getZ() + vec3d.z);
		passenger.setYaw(passenger.getYaw() + this.yawVelocity);
		passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);
		this.copyEntityData(passenger);
		if (passenger instanceof AnimalEntity && size > 1) {
			int angle = passenger.getId() % 2 == 0 ? 90 : 270;
			passenger.setBodyYaw(((AnimalEntity)passenger).bodyYaw + (float)angle);
			passenger.setHeadYaw(passenger.getHeadYaw() + (float)angle);
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

	private float getHeadVerticalMovement() {
		double[] ds = this.getSegmentProperties(5, 1.0F);
		double[] es = this.getSegmentProperties(0, 1.0F);
		return (float)(ds[1] - es[1]);
	}

	private float wrapYawChange(double yawDegrees) {
		return (float)MathHelper.wrapDegrees(yawDegrees);
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

	@Override
	public void tick() {
//		nose.setPosition(0, 0, 4);
//		cockpit.setPosition(0, 0, 0);
//		tail.setPosition(0, 0.5, -4);
//		leftWing.setPosition(3.5, 0.3, 1);
//		rightWing.setPosition(-3.5, 0.3, 1);
		this.lastLocation = this.location;
		this.location = this.checkLocation();
		if (this.location != MonoplaneEntity.Location.UNDER_WATER && this.location != MonoplaneEntity.Location.UNDER_FLOWING_WATER) {
			this.ticksUnderwater = 0.0F;
		} else {
			++this.ticksUnderwater;
		}

		if (!this.world.isClient && this.ticksUnderwater >= 60.0F) {
			this.removeAllPassengers();
		}
		if (!this.isRemoved()) {
			this.tickMovement();
		}
		super.tick();
		this.lerpTick();
	}

	private void lerpTick() {
		if (this.isLogicalSideForUpdatingMovement()) {
			this.lerpSteps = 0;
			this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
		}

		if (this.lerpSteps > 0) {
			double d = this.getX() + (this.x - this.getX()) / (double)this.lerpSteps;
			double e = this.getY() + (this.y - this.getY()) / (double)this.lerpSteps;
			double f = this.getZ() + (this.z - this.getZ()) / (double)this.lerpSteps;
			double g = MathHelper.wrapDegrees(this.clientYaw - (double)this.getYaw());
			this.setYaw(this.getYaw() + (float)g / (float)this.lerpSteps);
			this.setPitch(this.getPitch() + (float)(this.clientPitch - (double)this.getPitch()) / (float)this.lerpSteps);
			--this.lerpSteps;
			this.setPosition(d, e, f);
			this.setRotation(this.getYaw(), this.getPitch());
		}
	}

	public void tickMovement() {
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


//		for (MonoplanePart part : this.parts) {
//			part.tick();
//		}
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
//		if (this.world.isClient) {
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

		for(int s = 0; s < this.parts.length; ++s) {
			vec3ds[s] = new Vec3d(this.parts[s].getX(), this.parts[s].getY(), this.parts[s].getZ());
		}

//		float t = (float)(this.getSegmentProperties(5, 1.0F)[1] - this.getSegmentProperties(10, 1.0F)[1]) * 10.0F * (float) (Math.PI / 180.0);
//		float u = MathHelper.cos(t);
//		float v = MathHelper.sin(t);
		float w = this.getYaw() * (float) (Math.PI / 180.0);
		float x = MathHelper.sin(w);
		float y = MathHelper.cos(w);

		float w_ = (this.getYaw() - 90) * (float) (Math.PI / 180.0);
		float x_ = MathHelper.sin(w_);
		float y_ = MathHelper.cos(w_);

		this.movePart(this.nose, x * -4.5F, 0.0, -y * -4.5F);
		this.movePart(this.engine, x * -2.5F, 0.0, -y * -2.5F);
		this.movePart(this.cockpit, x * -0.5F, 0.0, -y * -0.5F);
		this.movePart(this.fuselage, x * 1.5F, 0.25, -y * 1.5F);
		this.movePart(this.tail, x * 3.75F, 0.5, -y * 3.75F);
		this.movePart(this.rightWing,  x_ * -(4 - 1), 0.5, y_ * (4 + 1));
		this.movePart(this.leftWing, y * -3.5F, 0.5, x * -3.5F);

		if (!this.world.isClient) {
			this.damageLivingEntities(this.world.getOtherEntities(this, this.nose.getBoundingBox().expand(0.5), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
		}

//		float z = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0) - this.yawAcceleration * 0.01F);
//		float aa = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0) - this.yawAcceleration * 0.01F);
//		float ab = this.getHeadVerticalMovement();
//		this.movePart(this.nose, (z * 6.5F * u), (ab + v * 6.5F), -aa * 6.5F * u);
//		this.movePart(this.cockpit, z * 5.5F * u, ab + v * 5.5F, -aa * 5.5F * u);
//		double[] ds = this.getSegmentProperties(5, 1.0F);
//		}

		for(int ac = 0; ac < this.parts.length; ++ac) {
			this.parts[ac].prevX = vec3ds[ac].x;
			this.parts[ac].prevY = vec3ds[ac].y;
			this.parts[ac].prevZ = vec3ds[ac].z;
			this.parts[ac].lastRenderX = vec3ds[ac].x;
			this.parts[ac].lastRenderY = vec3ds[ac].y;
			this.parts[ac].lastRenderZ = vec3ds[ac].z;
		}
//		}
	}

	private void damageLivingEntities(List<Entity> entities) {
		for(Entity entity : entities) {
			if (entity instanceof LivingEntity) {
				entity.damage(DamageSource.thorns(this), 5.0F);
			}
		}
	}

	private MonoplaneEntity.Location checkLocation() {
		MonoplaneEntity.Location location = this.getUnderWaterLocation();
		if (location != null) {
			this.waterLevel = this.getBoundingBox().maxY;
			return location;
		} else if (this.checkIfInWater()) {
			return MonoplaneEntity.Location.IN_WATER;
		} else {
			float f = this.getGroundFriction();
			if (f > 0.0F) {
				this.landFriction = f;
				return MonoplaneEntity.Location.ON_LAND;
			} else {
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

		for(int p = i; p < j; ++p) {
			for(int q = m; q < n; ++q) {
				int r = (p != i && p != j - 1 ? 0 : 1) + (q != m && q != n - 1 ? 0 : 1);
				if (r != 2) {
					for(int s = k; s < l; ++s) {
						if (r <= 0 || s != k && s != l - 1) {
							mutable.set(p, s, q);
							BlockState blockState = this.world.getBlockState(mutable);
							if (VoxelShapes.matchesAnywhere(
									blockState.getCollisionShape(this.world, mutable).offset(p, s, q), voxelShape, BooleanBiFunction.AND
							)) {
								f += blockState.getBlock().getSlipperiness();
								++o;
							}
						}
					}
				}
			}
		}

		return f / (float)o;
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

		for(int o = i; o < j; ++o) {
			for(int p = k; p < l; ++p) {
				for(int q = m; q < n; ++q) {
					mutable.set(o, p, q);
					FluidState fluidState = this.world.getFluidState(mutable);
					if (fluidState.isIn(FluidTags.WATER)) {
						float f = (float)p + fluidState.getHeight(this.world, mutable);
						this.waterLevel = Math.max(f, this.waterLevel);
						bl |= box.minY < (double)f;
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

		for(int o = i; o < j; ++o) {
			for(int p = k; p < l; ++p) {
				for(int q = m; q < n; ++q) {
					mutable.set(o, p, q);
					FluidState fluidState = this.world.getFluidState(mutable);
					if (fluidState.isIn(FluidTags.WATER) && d < (double)((float)mutable.getY() + fluidState.getHeight(this.world, mutable))) {
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

	@Override
	public void move(MovementType movementType, Vec3d movement) {
		for (MonoplanePart part : this.parts) {
			this.movePart(part, movement.x, movement.y, movement.z);
		}
		super.move(movementType, movement);
	}

	private void movePart(MonoplanePart monoplanePart, double dx, double dy, double dz) {
		monoplanePart.setPosition(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
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

		for(int i = 0; i < monoplaneParts.length; ++i) {
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
