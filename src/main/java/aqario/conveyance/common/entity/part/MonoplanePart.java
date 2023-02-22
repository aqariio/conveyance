package aqario.conveyance.common.entity.part;

import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import aqario.conveyance.common.item.ConveyanceItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class MonoplanePart extends Entity implements MonoplaneEntityPart {
	public final MonoplaneEntity owner;
	public final String name;
	private final EntityDimensions partDimensions;
	private final Vec3d relativePosition = Vec3d.ZERO;

	public MonoplanePart(MonoplaneEntity owner, String name, float width, float height) {
		super(owner.getType(), owner.world);
		this.partDimensions = EntityDimensions.fixed(width, height);
		this.calculateDimensions();
		this.owner = owner;
		this.name = name;
	}

	@Override
	public ItemStack getPickBlockStack() {
		return ConveyanceItems.MONOPLANE.getDefaultStack();
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if (player.getStackInHand(hand).isEmpty() && player.isSneaking()) {
			if (!(player.getAbilities()).creativeMode) {
				player.setStackInHand(hand, ConveyanceItems.MONOPLANE.getDefaultStack());
			}
			this.remove(RemovalReason.DISCARDED);
			this.owner.remove(RemovalReason.DISCARDED);
			return ActionResult.SUCCESS;
		}
		return super.interact(player, hand);
	}

	public void move(double dx, double dy, double dz) {
		this.prevX = this.lastRenderX = this.getX();
		this.prevY = this.lastRenderY = this.getY();
		this.prevZ = this.lastRenderZ = this.getZ();
		var newPos = this.getAbsolutePosition().add(dx, dy, dz);
		this.setPosition(newPos);
	}

	public Vec3d getAbsolutePosition() {
		return this.owner.getPos().add(this.relativePosition);
	}

	@Override
	public MonoplaneEntity getOwner() {
		return this.owner;
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	public boolean collidesWith(Entity other) {
		return MonoplanePart.canCollide(this, other);
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
	public boolean collides() {
		return !this.isRemoved();
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean isPartOf(Entity entity) {
		return this == entity || this.owner == entity;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return this.partDimensions;
	}

	@Override
	public boolean shouldSave() {
		return false;
	}
}
