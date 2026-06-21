package net.stohun.veercore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class CornerBlock extends Block implements Waterloggable {
    public static final BooleanProperty DNW = BooleanProperty.of("dnw");
    public static final BooleanProperty DNE = BooleanProperty.of("dne");
    public static final BooleanProperty DSW = BooleanProperty.of("dsw");
    public static final BooleanProperty DSE = BooleanProperty.of("dse");
    public static final BooleanProperty UNW = BooleanProperty.of("unw");
    public static final BooleanProperty UNE = BooleanProperty.of("une");
    public static final BooleanProperty USW = BooleanProperty.of("usw");
    public static final BooleanProperty USE = BooleanProperty.of("use");

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final BooleanProperty[] PROPERTIES = {
            DNW, DNE, DSW, DSE,
            UNW, UNE, USW, USE
    };

    private static final VoxelShape[] SHAPES = {
            Block.createCuboidShape(0, 0, 0, 8, 8, 8),     // DNW
            Block.createCuboidShape(8, 0, 0, 16, 8, 8),    // DNE
            Block.createCuboidShape(0, 0, 8, 8, 8, 16),    // DSW
            Block.createCuboidShape(8, 0, 8, 16, 8, 16),   // DSE
            Block.createCuboidShape(0, 8, 0, 8, 16, 8),    // UNW
            Block.createCuboidShape(8, 8, 0, 16, 16, 8),   // UNE
            Block.createCuboidShape(0, 8, 8, 8, 16, 16),   // USW
            Block.createCuboidShape(8, 8, 8, 16, 16, 16)   // USE
    };

    private static final VoxelShape[] STATE_INDEX_TO_SHAPE = new VoxelShape[256];

    public CornerBlock(Settings settings) {
        super(settings);

        setDefaultState(getStateManager().getDefaultState()
                .with(DNW, false).with(DNE, false).with(DSW, false).with(DSE, false)
                .with(UNW, false).with(UNE, false).with(USW, false).with(USE, false)
                .with(WATERLOGGED, false));

        runShapePrecalculation();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DNW, DNE, DSW, DSE, UNW, UNE, USW, USE, WATERLOGGED);
    }

    private void runShapePrecalculation() {
        for (BlockState state : getStateManager().getStates()) {
            if (state.get(WATERLOGGED)) continue;

            int index = getShapeIndex(state);

            if (index == 255) {
                STATE_INDEX_TO_SHAPE[index] = VoxelShapes.fullCube();
                continue;
            }

            VoxelShape combinedShape = VoxelShapes.empty();
            for (int i = 0; i < PROPERTIES.length; i++) {
                if (state.get(PROPERTIES[i])) {
                    combinedShape = VoxelShapes.union(combinedShape, SHAPES[i]);
                }
            }

            STATE_INDEX_TO_SHAPE[index] = combinedShape.simplify();
        }
    }

    private static int getShapeIndex(BlockState state) {
        int index = 0;
        for (int i = 0; i < PROPERTIES.length; i++) {
            if (state.get(PROPERTIES[i])) {
                index |= (1 << i);
            }
        }
        return index;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return STATE_INDEX_TO_SHAPE[getShapeIndex(state)];
    }

    private static boolean isFull(BlockState state) {
        return getShapeIndex(state) == 255;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private static BooleanProperty getTargetedCorner(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        Direction face = ctx.getSide();
        Vec3d hit = ctx.getHitPos();

        double offX = hit.x - pos.getX();
        double offY = hit.y - pos.getY();
        double offZ = hit.z - pos.getZ();

        if (face != Direction.SOUTH && face != Direction.EAST && face != Direction.UP) {
            offX += face.getOffsetX() * 0.5;
            offY += face.getOffsetY() * 0.5;
            offZ += face.getOffsetZ() * 0.5;
        }

        boolean east = offX >= 0.5;
        boolean up = offY >= 0.5;
        boolean south = offZ >= 0.5;

        if (up) {
            if (south) return east ? USE : USW;
            return east ? UNE : UNW;
        } else {
            if (south) return east ? DSE : DSW;
            return east ? DNE : DNW;
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        BlockState here = ctx.getWorld().getBlockState(pos);
        BooleanProperty corner = getTargetedCorner(ctx);

        boolean fluidIsWater = ctx.getWorld().getFluidState(pos).getFluid() == Fluids.WATER;

        if (here.isOf(this)) {
            BlockState updatedState = here.with(corner, true);
            if (isFull(updatedState)) {
                return updatedState.with(WATERLOGGED, false);
            }
            return updatedState;
        }

        BlockState defaultPlacement = getDefaultState().with(corner, true);
        return defaultPlacement.with(WATERLOGGED, fluidIsWater && !isFull(defaultPlacement));
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
        ItemStack stack = ctx.getStack();

        if (!stack.isOf(this.asItem()) || isFull(state)) {
            return false;
        }

        return !state.get(getTargetedCorner(ctx));
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        if (rotation == BlockRotation.NONE) {
            return state;
        }

        boolean dnw = state.get(DNW);
        boolean dne = state.get(DNE);
        boolean dsw = state.get(DSW);
        boolean dse = state.get(DSE);
        boolean unw = state.get(UNW);
        boolean une = state.get(UNE);
        boolean usw = state.get(USW);
        boolean use = state.get(USE);

        return switch (rotation) {
            case CLOCKWISE_90 -> state
                    .with(DNW, dsw).with(DNE, dnw).with(DSE, dne).with(DSW, dse)
                    .with(UNW, usw).with(UNE, unw).with(USE, une).with(USW, use);
            case CLOCKWISE_180 -> state
                    .with(DNW, dse).with(DNE, dsw).with(DSE, dnw).with(DSW, dne)
                    .with(UNW, use).with(UNE, usw).with(USE, unw).with(USW, une);
            case COUNTERCLOCKWISE_90 -> state
                    .with(DNW, dne).with(DNE, dse).with(DSE, dsw).with(DSW, dnw)
                    .with(UNW, une).with(UNE, use).with(USE, usw).with(USW, unw);
            default -> state;
        };
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        boolean dnw = state.get(DNW);
        boolean dne = state.get(DNE);
        boolean dsw = state.get(DSW);
        boolean dse = state.get(DSE);
        boolean unw = state.get(UNW);
        boolean une = state.get(UNE);
        boolean usw = state.get(USW);
        boolean use = state.get(USE);

        return switch (mirror) {
            case LEFT_RIGHT -> state
                    .with(DNW, dsw).with(DNE, dse).with(DSW, dnw).with(DSE, dne)
                    .with(UNW, usw).with(UNE, use).with(USW, unw).with(USE, une);
            case FRONT_BACK -> state
                    .with(DNW, dne).with(DNE, dnw).with(DSW, dse).with(DSE, dsw)
                    .with(UNW, une).with(UNE, unw).with(USW, use).with(USE, usw);
            default -> state;
        };
    }
}