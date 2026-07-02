package net.stohun.veercore.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.BooleanProperty;
import net.stohun.veercore.block.ModBlocks;
import net.stohun.veercore.block.CornerBlock;

import java.util.concurrent.CompletableFuture;

public class VeerCoreLootTableProvider extends FabricBlockLootTableProvider {
    public VeerCoreLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        BooleanProperty[] cornerProperties = {
                CornerBlock.DNW, CornerBlock.DNE, CornerBlock.DSE, CornerBlock.DSW,
                CornerBlock.UNW, CornerBlock.UNE, CornerBlock.USE, CornerBlock.USW
        };

        addCornerDrop(ModBlocks.APPLE_CORNER, cornerProperties);
        addCornerDrop(ModBlocks.WROUGHT_IRON_CORNER, cornerProperties);
    }

    private void addCornerDrop(Block block, BooleanProperty[] cornerProperties) {
        LootPool.Builder poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .bonusRolls(ConstantLootNumberProvider.create(0.0F));

        var itemEntryBuilder = ItemEntry.builder(block);

        itemEntryBuilder.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F), false));

        for (BooleanProperty property : cornerProperties) {
            itemEntryBuilder.apply(
                    SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F), true)
                            .conditionally(BlockStatePropertyLootCondition.builder(block)
                                    .properties(StatePredicate.Builder.create().exactMatch(property, true)))
            );
        }

        itemEntryBuilder.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(-1.0F), true));

        itemEntryBuilder.apply(ExplosionDecayLootFunction.builder());

        poolBuilder.with(itemEntryBuilder);

        this.addDrop(block, LootTable.builder().pool(poolBuilder));
    }
}