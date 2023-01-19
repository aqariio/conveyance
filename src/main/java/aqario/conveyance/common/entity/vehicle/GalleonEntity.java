package aqario.conveyance.common.entity.vehicle;

import aqario.conveyance.common.world.dimension.ConveyanceWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.quiltmc.qsl.worldgen.dimension.api.QuiltDimensions;

public class GalleonEntity extends Entity {
    public GalleonEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public boolean collidesWith(Entity other) {
        return GalleonEntity.canCollide(this, other);
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
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        sendPlayerToShip(player);

//        if (this.ticksUnderwater < 60.0f) {
//            if (!this.world.isClient) {
//                return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
//            }
//            return ActionResult.SUCCESS;
//        }
        return ActionResult.PASS;
    }

    private void sendPlayerToShip(PlayerEntity player) {
        if (!world.isClient && !(world.getRegistryKey() == ConveyanceWorld.GALLEON)) {
            RegistryKey<World> registryKey = ConveyanceWorld.GALLEON;
            ServerWorld serverWorld = ((ServerWorld)world).getServer().getWorld(registryKey);
            QuiltDimensions.teleport(player, serverWorld, new TeleportTarget(new Vec3d(0.5, 1, 0.5), player.getVelocity(), 0, 0));
        } else {
            player.sendMessage(Text.translatable("entity.conveyance.galleon.fail"), true);
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
