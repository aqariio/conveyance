package aqario.conveyance.common.entity.vehicle;

import aqario.conveyance.common.entity.ConveyanceEntityType;
import aqario.conveyance.common.entity.part.GalleonPart;
import aqario.conveyance.common.world.dimension.ConveyanceDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.quiltmc.qsl.worldgen.dimension.api.QuiltDimensions;

public class GalleonEntity extends VehicleEntity {
    private final GalleonPart[] parts;
    public final GalleonPart head;
    private final GalleonPart neck;
    private final GalleonPart body;
    private final GalleonPart tail1;
    private final GalleonPart tail2;
    private final GalleonPart tail3;
    private final GalleonPart rightWing;
    private final GalleonPart leftWing;

    public GalleonEntity(EntityType<? extends GalleonEntity> entityType, World world) {
        super(ConveyanceEntityType.GALLEON, world);
        this.head = new GalleonPart(this, "head", 1.0F, 1.0F);
        this.neck = new GalleonPart(this, "neck", 3.0F, 3.0F);
        this.body = new GalleonPart(this, "body", 5.0F, 3.0F);
        this.tail1 = new GalleonPart(this, "tail", 2.0F, 2.0F);
        this.tail2 = new GalleonPart(this, "tail", 2.0F, 2.0F);
        this.tail3 = new GalleonPart(this, "tail", 2.0F, 2.0F);
        this.rightWing = new GalleonPart(this, "wing", 4.0F, 2.0F);
        this.leftWing = new GalleonPart(this, "wing", 4.0F, 2.0F);
        this.parts = new GalleonPart[]{this.head, this.neck, this.body, this.tail1, this.tail2, this.tail3, this.rightWing, this.leftWing};
//		this.setHealth(this.getMaxHealth());
        this.noClip = true;
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
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        sendPlayerToCabin(player);
        return ActionResult.PASS;
    }

    private void sendPlayerToCabin(PlayerEntity player) {
        if (!world.isClient && !(world.getRegistryKey() == ConveyanceDimensions.GALLEON)) {
            MinecraftServer minecraftServer = ((ServerWorld) world).getServer();
            RegistryKey<World> registryKey = ConveyanceDimensions.GALLEON;
            ServerWorld serverWorld = minecraftServer.getWorld(registryKey);
            if (serverWorld != null) {
                if (player.hasVehicle()) {
                    QuiltDimensions.teleport(player.getVehicle(), serverWorld, new TeleportTarget(new Vec3d(0.5, 1, 0.5), player.getVelocity(), 0, 0));
                }
                QuiltDimensions.teleport(player, serverWorld, new TeleportTarget(new Vec3d(0.5, 1, 0.5), player.getVelocity(), 0, 0));
            }
        }
        else {
            player.sendMessage(Text.translatable("entity.conveyance.galleon.fail"), true);
        }
    }
}
