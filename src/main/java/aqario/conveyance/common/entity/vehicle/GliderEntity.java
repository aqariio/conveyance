package aqario.conveyance.common.entity.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class GliderEntity extends Entity {
    private static final TrackedData<Boolean> LEFT_WING_PULLED = DataTracker.registerData(GliderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> RIGHT_WING_PULLED = DataTracker.registerData(GliderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public GliderEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(LEFT_WING_PULLED, false);
        this.dataTracker.startTracking(RIGHT_WING_PULLED, false);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }
}
