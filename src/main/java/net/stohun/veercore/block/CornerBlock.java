package net.stohun.veercore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
        BlockPos pos = ctx.getBlockPos();
        BlockState state = ctx.getWorld().getBlockState(pos);

        BooleanProperty corner = getClickedCorner(ctx);

        if (state.isOf(this)) {
            return state.with(corner, true);
        }

        return getDefaultState().with(corner, true);
    }

    public BooleanProperty getClickedCorner(ItemPlacementContext ctx) {
        Vec3d hit = ctx.getHitPos().subtract(
                ctx.getBlockPos().getX(),
                ctx.getBlockPos().getY(),
                ctx.getBlockPos().getZ()
        );

        boolean east = hit.x >= 0.5;
        boolean up = hit.y >= 0.5;
        boolean south = hit.z >= 0.5;

        if (up) {
            if (south) return east ? USE : USW;
            else return east ? UNE : UNW;
        } else {
            if (south) return east ? DSE : DSW;
            else return east ? DNE : DNW;
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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