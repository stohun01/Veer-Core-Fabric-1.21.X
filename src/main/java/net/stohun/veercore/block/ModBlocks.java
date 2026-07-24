package net.stohun.veercore.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.stohun.veercore.VeerCore;

public class ModBlocks {

    public static final Block DOUGH_BLOCK = registerBlock("dough_block",
            new Block(
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.OFF_WHITE)
                            .strength(0.8f)
                            .sounds(BlockSoundGroup.WOOL)
            )
    );

    public static final Block WROUGHT_IRON_BLOCK = registerBlock("wrought_iron_block",
            new Block(
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.GRAY)
                            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                            .requiresTool()
                            .strength(5.0F, 6.0F)
                            .sounds(BlockSoundGroup.METAL)
            )
    );

    public static final CornerBlock APPLE_CORNER = (CornerBlock) registerBlock("apple_corner",
            new CornerBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).strength(2.0F, 6.0F))
    );

    public static final CornerBlock WROUGHT_IRON_CORNER = (CornerBlock) registerBlock("wrought_iron_corner",
            new CornerBlock(AbstractBlock.Settings.copy(ModBlocks.WROUGHT_IRON_BLOCK)));

    public static final ColumnBlock MAPLE_PLANKS_COLUMN = (ColumnBlock) registerBlock("maple_planks_column",
            new ColumnBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));

    private static Block registerBlock(String name, Block block) {

        Registry.register(Registries.BLOCK,Identifier.of(VeerCore.MOD_ID, name),block);

        Registry.register(Registries.ITEM, Identifier.of(VeerCore.MOD_ID, name), new BlockItem(block, new Item.Settings()));

        return block;
    }

    public static void registerModBlocks() {
        VeerCore.LOGGER.info("Registering Mod Blocks for " + VeerCore.MOD_ID);

        FlammableBlockRegistry.getDefaultInstance().add(MAPLE_PLANKS_COLUMN, 5, 20);

        FuelRegistry.INSTANCE.add(MAPLE_PLANKS_COLUMN, 150);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(DOUGH_BLOCK);
            entries.add(APPLE_CORNER);
            entries.add(WROUGHT_IRON_BLOCK);
            entries.add(WROUGHT_IRON_CORNER);
            entries.add(MAPLE_PLANKS_COLUMN);
        });
    }
}