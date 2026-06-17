package net.stohun.veercore.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.stohun.veercore.block.CornerBlock;

public class CornerBlockItem extends BlockItem {

    public CornerBlockItem(CornerBlock block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult place(ItemPlacementContext ctx) {
        World world = ctx.getWorld();

        BlockState state = world.getBlockState(ctx.getBlockPos());

        if (state.isOf(getBlock())) {

            CornerBlock cornerBlock = (CornerBlock) getBlock();

            BlockState updated = state.with(
                    cornerBlock.getClickedCorner(ctx),
                    true
            );

            world.setBlockState(ctx.getBlockPos(), updated, 3);

            return ActionResult.SUCCESS;
        }

        return super.place(ctx);
    }
}