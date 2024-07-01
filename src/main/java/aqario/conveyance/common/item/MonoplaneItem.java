package aqario.conveyance.common.item;

import aqario.conveyance.common.entity.ConveyanceEntityType;
import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class MonoplaneItem extends Item {
    public MonoplaneItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        BlockPos pos = ctx.getBlockPos();
        float headYaw = player.getHeadYaw();
        if (ctx.getWorld().getBlockState(pos.offset(ctx.getSide())).isAir() && ctx.getWorld().getBlockState(pos.offset(Direction.UP, 2)).isAir() ||
            ctx.getWorld().getBlockState(pos.offset(ctx.getSide())).getBlock().equals(Blocks.WATER) && ctx.getWorld().getBlockState(pos.offset(ctx.getSide()).offset(Direction.UP)).getBlock().equals(Blocks.WATER) && ctx.getWorld().getBlockState(pos.offset(ctx.getSide()).offset(Direction.UP, 2)).getBlock().equals(Blocks.WATER)) {
            MonoplaneEntity monoplaneEntity = new MonoplaneEntity(ConveyanceEntityType.MONOPLANE, ctx.getWorld());
            monoplaneEntity.refreshPositionAndAngles(pos.offset(ctx.getSide()).offset(Direction.Axis.Y, 2),
                (headYaw >= 0 && headYaw <= 45) || (headYaw >= 135 && headYaw <= 180) ? 90 :
                    (headYaw >= 45 && headYaw <= 90) || (headYaw >= -90 && headYaw <= -45) ? 0 :
                        (headYaw >= 90 && headYaw <= 135) || (headYaw >= -135 && headYaw <= -90) ? 180 : -90,
                0.0F);
            ctx.getWorld().spawnEntity(monoplaneEntity);
            if (!player.getAbilities().creativeMode) {
                ctx.getStack().decrement(1);
            }
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(ctx);
    }
}
