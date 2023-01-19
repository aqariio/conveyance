package aqario.conveyance.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.piston.PistonBehavior;

public class ImmovablePillarBlock extends PillarBlock {
    public ImmovablePillarBlock(Settings settings) {
        super(settings);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }
}
