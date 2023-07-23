package aqario.conveyance.common.entity.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

public class VehicleEntity extends Entity {
	public VehicleEntity(EntityType<? extends VehicleEntity> type, World world) {
		super(type, world);
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return null;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
