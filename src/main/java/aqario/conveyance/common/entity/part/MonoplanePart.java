package aqario.conveyance.common.entity.part;

import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import aqario.conveyance.common.item.ConveyanceItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import org.quiltmc.qsl.entity.multipart.api.AbstractEntityPart;

public class MonoplanePart extends AbstractEntityPart<MonoplaneEntity> implements MonoplaneEntityPart {
	private final MonoplaneEntity owner;
	public ModuleType type;

	public MonoplanePart(MonoplaneEntity owner, float width, float height, Vec3d relativePosition, Vec3d relativePivot) {
		super(owner, width, height);
		setRelativePosition(relativePosition);
		setPivot(relativePivot);
		this.calculateDimensions();
		this.owner = owner;
	}

	public MonoplanePart(MonoplaneEntity owner, float width, float height, Vec3d relativePosition, Vec3d relativePivot, ModuleType type) {
		this(owner, width, height, relativePosition, relativePivot);
		this.type = type;
	}

	public boolean wouldCollideWithBlocks(Vec3d velocity, float pitch, float yaw, float roll) {
		Vec3f relativePos = new Vec3f(getAbsolutePosition().subtract(getAbsolutePivot()));
		relativePos.rotate(new Quaternion(-pitch, -yaw, -roll, true));
		Vec3d transformedPos = getAbsolutePivot().subtract(getAbsolutePosition()).add(relativePos.getX(), relativePos.getY(), relativePos.getZ());
		Box box = this.getDimensions(this.getPose()).getBoxAt(getAbsolutePosition().add(transformedPos));
		BlockPos blockPos = new BlockPos(box.minX + velocity.getX() + 0.001D, box.minY + velocity.getY() + 0.001D, box.minZ + velocity.getZ() + 0.001D);
		BlockPos blockPos2 = new BlockPos(box.maxX + velocity.getX() - 0.001D, box.maxY + velocity.getY() - 0.001D, box.maxZ + velocity.getZ() - 0.001D);
		if (this.world.isRegionLoaded(blockPos, blockPos2)) {
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			for (int i = blockPos.getX(); i <= blockPos2.getX(); i++) {
				for (int j = blockPos.getY(); j <= blockPos2.getY(); j++) {
					for (int k = blockPos.getZ(); k <= blockPos2.getZ(); k++) {
						mutable.set(i, j, k);
						if (!this.world.getBlockState(mutable).getCollisionShape(this.world, mutable).isEmpty()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void checkBlockCollision() {
		super.checkBlockCollision();
	}

	@Override
	public ItemStack getPickBlockStack() {
		return ConveyanceItems.MONOPLANE.getDefaultStack();
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if (!this.world.isClient) {
			if (player.getMainHandStack().isEmpty() && player.isSneaking()) {
				if (!(player.getAbilities()).creativeMode) {
					player.setStackInHand(hand, ConveyanceItems.MONOPLANE.getDefaultStack());
				}
				this.owner.remove(RemovalReason.DISCARDED);
				return ActionResult.SUCCESS;
			}
			if (this.type == ModuleType.COCKPIT) {
				return player.startRiding(this.getOwner()) ? ActionResult.CONSUME : ActionResult.PASS;
			}
		}
		return super.interact(player, hand);
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
	public boolean damage(DamageSource source, float amount) {
		return this.owner.damage(source, amount);
	}

	@Override
	public boolean isPartOf(Entity entity) {
		return this == entity || this.owner == entity;
	}

    public enum ModuleType {
		COCKPIT,
		PROPELLER
	}
}
