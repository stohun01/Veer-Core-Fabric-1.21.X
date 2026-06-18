package net.stohun.veercore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class CornerBlock extends Block {
    public static final BooleanProperty DNW = BooleanProperty.of("dnw");
    public static final BooleanProperty DNE = BooleanProperty.of("dne");
    public static final BooleanProperty DSW = BooleanProperty.of("dsw");
    public static final BooleanProperty DSE = BooleanProperty.of("dse");
    public static final BooleanProperty UNW = BooleanProperty.of("unw");
    public static final BooleanProperty UNE = BooleanProperty.of("une");
    public static final BooleanProperty USW = BooleanProperty.of("usw");
    public static final BooleanProperty USE = BooleanProperty.of("use");

    private static final VoxelShape SHAPE_DNW = Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
    private static final VoxelShape SHAPE_DNE = Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
    private static final VoxelShape SHAPE_DSW = Block.createCuboidShape(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
    private static final VoxelShape SHAPE_DSE = Block.createCuboidShape(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
    private static final VoxelShape SHAPE_UNW = Block.createCuboidShape(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
    private static final VoxelShape SHAPE_UNE = Block.createCuboidShape(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
    private static final VoxelShape SHAPE_USW = Block.createCuboidShape(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
    private static final VoxelShape SHAPE_USE = Block.createCuboidShape(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);

    public CornerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(DNW, false).with(DNE, false)
                .with(DSW, false).with(DSE, false)
                .with(UNW, false).with(UNE, false)
                .with(USW, false).with(USE, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DNW, DNE, DSW, DSE, UNW, UNE, USW, USE);
    }

    private static boolean isFull(BlockState state) {
        return state.get(DNW) && state.get(DNE) && state.get(DSW) && state.get(DSE)
                && state.get(UNW) && state.get(UNE) && state.get(USW) && state.get(USE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (isFull(state)) return VoxelShapes.fullCube();

        VoxelShape shape = VoxelShapes.empty();
        if (state.get(DNW)) shape = VoxelShapes.union(shape, SHAPE_DNW);
        if (state.get(DNE)) shape = VoxelShapes.union(shape, SHAPE_DNE);
        if (state.get(DSW)) shape = VoxelShapes.union(shape, SHAPE_DSW);
        if (state.get(DSE)) shape = VoxelShapes.union(shape, SHAPE_DSE);
        if (state.get(UNW)) shape = VoxelShapes.union(shape, SHAPE_UNW);
        if (state.get(UNE)) shape = VoxelShapes.union(shape, SHAPE_UNE);
        if (state.get(USW)) shape = VoxelShapes.union(shape, SHAPE_USW);
        if (state.get(USE)) shape = VoxelShapes.union(shape, SHAPE_USE);
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getOutlineShape(state, world, pos, context);
    }

    private static BooleanProperty cornerFor(double offX, double offY, double offZ) {
        boolean east = offX >= 0.5;
        boolean up = offY >= 0.5;
        boolean south = offZ >= 0.5;

        if (up) {
            if (south && east) return USE;
            if (south) return USW;
            if (east) return UNE;
            return UNW;
        } else {
            if (south && east) return DSE;
            if (south) return DSW;
            if (east) return DNE;
            return DNW;
        }
    }

    private static double localOffset(double hit, int origin, int step) {
        return hit - origin + step * 0.5;
    }

    private static double placementOffsetX(Direction face, double hitX, BlockPos pos) {
        return switch (face) {
            case EAST -> 0.0;
            case WEST -> 1.0;
            default -> hitX - pos.getX();
        };
    }

    private static double placementOffsetY(Direction face, double hitY, BlockPos pos) {
        return switch (face) {
            case UP -> 0.0;
            case DOWN -> 1.0;
            default -> hitY - pos.getY();
        };
    }

    private static double placementOffsetZ(Direction face, double hitZ, BlockPos pos) {
        return switch (face) {
            case SOUTH -> 0.0;
            case NORTH -> 1.0;
            default -> hitZ - pos.getZ();
        };
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        BlockState here = ctx.getWorld().getBlockState(pos);
        Vec3d hit = ctx.getHitPos();
        Direction face = ctx.getSide();

        double offX = localOffset(hit.x, pos.getX(), face.getOffsetX());
        double offY = localOffset(hit.y, pos.getY(), face.getOffsetY());
        double offZ = localOffset(hit.z, pos.getZ(), face.getOffsetZ());

        if (here.isOf(this)) {
            BooleanProperty corner = cornerFor(offX, offY, offZ);
            return here.with(corner, true);
        }

        BooleanProperty corner = cornerFor(
                placementOffsetX(face, hit.x, pos),
                placementOffsetY(face, hit.y, pos),
                placementOffsetZ(face, hit.z, pos));

        return this.getDefaultState()
                .with(DNW, false).with(DNE, false)
                .with(DSW, false).with(DSE, false)
                .with(UNW, false).with(UNE, false)
                .with(USW, false).with(USE, false)
                .with(corner, true);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
        ItemStack stack = ctx.getStack();
        if (!stack.isOf(this.asItem())) return false;
        if (isFull(state)) return false;

        if (!ctx.getBlockPos().equals(ctx.getBlockPos())) return true;

        Vec3d hit = ctx.getHitPos();
        BlockPos pos = ctx.getBlockPos();
        Direction face = ctx.getSide();

        double offX = localOffset(hit.x, pos.getX(), face.getOffsetX());
        double offY = localOffset(hit.y, pos.getY(), face.getOffsetY());
        double offZ = localOffset(hit.z, pos.getZ(), face.getOffsetZ());

        BooleanProperty corner = cornerFor(offX, offY, offZ);
        return !state.get(corner);
    }
}