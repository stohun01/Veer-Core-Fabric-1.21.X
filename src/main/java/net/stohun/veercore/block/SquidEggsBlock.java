package net.stohun.veercore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SquidEggsBlock extends PlantBlock {
    public static final MapCodec<SquidEggsBlock> CODEC = createCodec(SquidEggsBlock::new);

    public static final IntProperty AGE = Properties.AGE_3;

    private static final VoxelShape SHAPE =
            Block.createCuboidShape(2, 0, 2, 14, 12, 14);

    @Override
    public MapCodec<SquidEggsBlock> getCodec() {
        return CODEC;
    }

    public SquidEggsBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(AGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP)
                && !floor.isOf(Blocks.MAGMA_BLOCK);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluid = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return fluid.isIn(FluidTags.WATER) && fluid.getLevel() == 8
                ? getDefaultState()
                : null;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            WorldAccess world,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        BlockState result = super.getStateForNeighborUpdate(
                state, direction, neighborState, world, pos, neighborPos);

        if (!result.isAir()) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return result;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStill(false);
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) < 3;
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(AGE) < 3) {
            world.setBlockState(pos, state.with(AGE, state.get(AGE) + 1), Block.NOTIFY_LISTENERS);
        }
    }
}