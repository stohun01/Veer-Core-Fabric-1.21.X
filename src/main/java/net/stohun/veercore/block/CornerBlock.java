package net.stohun.veercore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class CornerBlock extends Block {

    public static final BooleanProperty DNW = BooleanProperty.of("dnw");
    public static final BooleanProperty DNE = BooleanProperty.of("dne");
    public static final BooleanProperty DSW = BooleanProperty.of("dsw");
    public static final BooleanProperty DSE = BooleanProperty.of("dse");

    public static final BooleanProperty UNW = BooleanProperty.of("unw");
    public static final BooleanProperty UNE = BooleanProperty.of("une");
    public static final BooleanProperty USW = BooleanProperty.of("usw");
    public static final BooleanProperty USE = BooleanProperty.of("use");

    private static final VoxelShape DNW_SHAPE = Block.createCuboidShape(0, 0, 0, 8, 8, 8);
    private static final VoxelShape DNE_SHAPE = Block.createCuboidShape(8, 0, 0, 16, 8, 8);
    private static final VoxelShape DSW_SHAPE = Block.createCuboidShape(0, 0, 8, 8, 8, 16);
    private static final VoxelShape DSE_SHAPE = Block.createCuboidShape(8, 0, 8, 16, 8, 16);

    private static final VoxelShape UNW_SHAPE = Block.createCuboidShape(0, 8, 0, 8, 16, 8);
    private static final VoxelShape UNE_SHAPE = Block.createCuboidShape(8, 8, 0, 16, 16, 8);
    private static final VoxelShape USW_SHAPE = Block.createCuboidShape(0, 8, 8, 8, 16, 16);
    private static final VoxelShape USE_SHAPE = Block.createCuboidShape(8, 8, 8, 16, 16, 16);

    public CornerBlock(Settings settings) {
        super(settings);

        setDefaultState(getStateManager().getDefaultState()
                .with(DNW, false)
                .with(DNE, false)
                .with(DSW, false)
                .with(DSE, false)
                .with(UNW, false)
                .with(UNE, false)
                .with(USW, false)
                .with(USE, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DNW, DNE, DSW, DSE, UNW, UNE, USW, USE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos targetPos = ctx.getBlockPos();
        BlockState targetState = ctx.getWorld().getBlockState(targetPos);

        if (targetState.isOf(this)) {
            BooleanProperty corner = getTargetedCorner(ctx, targetPos, false);
            if (!targetState.get(corner)) {
                return targetState.with(corner, true);
            }
        }

        BlockPos clickedPos = targetPos.offset(ctx.getSide().getOpposite());
        BlockState clickedState = ctx.getWorld().getBlockState(clickedPos);

        if (clickedState.isOf(this)) {
            BooleanProperty corner = getTargetedCorner(ctx, clickedPos, true);
            if (!clickedState.get(corner)) {
                return clickedState.with(corner, true);
            }
        }

        if (targetState.isOf(this)) {
            BooleanProperty corner = getTargetedCorner(ctx, targetPos, false);
            if (!targetState.get(corner)) {
                return targetState.with(corner, true);
            }
        }

        BooleanProperty corner = getTargetedCorner(ctx, targetPos, false);
        return getDefaultState().with(corner, true);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
        if (ctx.getStack().isOf(this.asItem())) {
            BlockPos pos = ctx.getBlockPos();
            BooleanProperty corner = getTargetedCorner(ctx, pos, false);

            if (!state.get(corner)) {
                return true;
            }

            BlockPos neighborPos = pos.offset(ctx.getSide());
            BlockState neighborState = ctx.getWorld().getBlockState(neighborPos);
            if (neighborState.isOf(this)) {
                BooleanProperty neighborCorner = getTargetedCorner(ctx, neighborPos, false);
                return !neighborState.get(neighborCorner);
            }
        }
        return super.canReplace(state, ctx);
    }

    private BooleanProperty getTargetedCorner(ItemPlacementContext ctx, BlockPos pos, boolean isNeighborContext) {
        double x = ctx.getHitPos().x - pos.getX();
        double y = ctx.getHitPos().y - pos.getY();
        double z = ctx.getHitPos().z - pos.getZ();

        Direction side = ctx.getSide();

        boolean east = x >= 0.5;
        boolean south = z >= 0.5;
        boolean up;

        if (isNeighborContext) {
            if (side == Direction.UP) {
                up = false;
            } else if (side == Direction.DOWN) {
                up = true;
            } else {
                up = y >= 0.5;
            }

            if (side == Direction.EAST)  east = false;
            if (side == Direction.WEST)  east = true;
            if (side == Direction.SOUTH) south = false;
            if (side == Direction.NORTH) south = true;

        } else {
            up = y >= 0.5;

            double epsilon = 0.001;
            if (side == Direction.UP    && Math.abs(y - 1.0) < epsilon) up = false;
            if (side == Direction.DOWN  && Math.abs(y - 0.0) < epsilon) up = true;
            if (side == Direction.EAST  && Math.abs(x - 1.0) < epsilon) east = false;
            if (side == Direction.WEST  && Math.abs(x - 0.0) < epsilon) east = true;
            if (side == Direction.SOUTH && Math.abs(z - 1.0) < epsilon) south = false;
            if (side == Direction.NORTH && Math.abs(z - 0.0) < epsilon) south = true;
        }

        return getCornerStateProperty(east, south, up);
    }

    private BooleanProperty getCornerStateProperty(boolean east, boolean south, boolean up) {
        if (up) {
            if (south) return east ? USE : USW;
            else return east ? UNE : UNW;
        } else {
            if (south) return east ? DSE : DSW;
            else return east ? DNE : DNW;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    private VoxelShape getShape(BlockState state) {
        VoxelShape shape = VoxelShapes.empty();

        if (state.get(DNW)) shape = VoxelShapes.union(shape, DNW_SHAPE);
        if (state.get(DNE)) shape = VoxelShapes.union(shape, DNE_SHAPE);
        if (state.get(DSW)) shape = VoxelShapes.union(shape, DSW_SHAPE);
        if (state.get(DSE)) shape = VoxelShapes.union(shape, DSE_SHAPE);

        if (state.get(UNW)) shape = VoxelShapes.union(shape, UNW_SHAPE);
        if (state.get(UNE)) shape = VoxelShapes.union(shape, UNE_SHAPE);
        if (state.get(USW)) shape = VoxelShapes.union(shape, USW_SHAPE);
        if (state.get(USE)) shape = VoxelShapes.union(shape, USE_SHAPE);

        return shape;
    }
}