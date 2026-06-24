package net.stohun.veercore.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
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
            new Block(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL))
    );

    public static final CornerBlock APPLE_CORNER = (CornerBlock) registerBlock("apple_corner",
            new CornerBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).strength(2.0F, 6.0F))
    );

    public static final CornerBlock OAK_CORNER = (CornerBlock) registerBlock("oak_corner",
            new CornerBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable())
    );

    public static final CornerBlock BRICK_CORNER = (CornerBlock) registerBlock("brick_corner",
            new CornerBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
    );

    public static final CornerBlock SANDSTONE_CORNER = (CornerBlock) registerBlock("sandstone_corner",
            new CornerBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
    );

    public static final CornerBlock STONE_CORNER = (CornerBlock) registerBlock("stone_corner",
            new CornerBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
    );

    private static Block registerBlock(String name, Block block) {

        Registry.register(Registries.BLOCK,Identifier.of(VeerCore.MOD_ID, name),block);

        Registry.register(Registries.ITEM, Identifier.of(VeerCore.MOD_ID, name), new BlockItem(block, new Item.Settings()));

        return block;
    }

    public static void registerModBlocks() {
        VeerCore.LOGGER.info("Registering Mod Blocks for " + VeerCore.MOD_ID);

        FlammableBlockRegistry.getDefaultInstance().add(OAK_CORNER, 5, 20);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(DOUGH_BLOCK);
            entries.add(APPLE_CORNER);
            entries.add(OAK_CORNER);
            entries.add(BRICK_CORNER);
            entries.add(SANDSTONE_CORNER);
            entries.add(STONE_CORNER);
        });
    }
}