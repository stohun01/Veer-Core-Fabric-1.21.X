package net.stohun.veercore.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;
import net.stohun.veercore.block.ModBlocks;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class VeerCoreModelProvider extends FabricModelProvider {
    public VeerCoreModelProvider(FabricDataOutput output) {
        super(output);
    }

    private record MaterialConfig(String prefix, Block sourceBlock, Block customBlock) {}

    private static final MaterialConfig[] MATERIALS = {
            new MaterialConfig("wrought_iron", ModBlocks.WROUGHT_IRON_BLOCK, ModBlocks.WROUGHT_IRON_CORNER)
    };

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        TextureKey downKey = TextureKey.of("down");
        TextureKey upKey = TextureKey.of("up");
        TextureKey sideKey = TextureKey.of("side");

        String[] templateNames = {
                "corner_external_down",
                "corner_external_up",
                "corner_internal_up",
                "corner_internal_down",
                "corner_internal_side",
                "corner_internal_side_other"
        };

        String templateString;
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("assets/veercore/blockstates/template/corner.json")) {
            Objects.requireNonNull(stream, "Couldn't find template json");
            templateString = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read blockstate template file", e);
        }

        for (MaterialConfig material : MATERIALS) {
            Identifier baseTexture = TextureMap.getId(material.sourceBlock());

            for (String templateName : templateNames) {
                TextureMap textures = new TextureMap().put(sideKey, baseTexture);
                TextureKey verticalKey;

                if (templateName.contains("_up")) {
                    textures.put(upKey, baseTexture);
                    verticalKey = upKey;
                } else {
                    textures.put(downKey, baseTexture);
                    verticalKey = downKey;
                }

                Model customModel = new Model(
                        Optional.of(Identifier.of("veercore", "block/template/" + templateName)),
                        Optional.empty(),
                        verticalKey, sideKey
                );

                String outputModelName = material.prefix() + "_" + templateName;

                customModel.upload(
                        Identifier.of("veercore", "block/" + outputModelName),
                        textures,
                        blockStateModelGenerator.modelCollector
                );
            }

            String processedJson = templateString.replace("%material%", material.prefix());
            JsonElement jsonElement = JsonParser.parseString(processedJson);

            blockStateModelGenerator.blockStateCollector.accept(new net.minecraft.data.client.BlockStateSupplier() {
                @Override
                public net.minecraft.block.Block getBlock() {
                    return material.customBlock();
                }

                @Override
                public com.google.gson.JsonElement get() {
                    return jsonElement;
                }
            });
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        TextureKey upKey = TextureKey.of("up");
        TextureKey downKey = TextureKey.of("down");
        TextureKey sideKey = TextureKey.of("side");

        Model itemTemplate = new Model(
                Optional.of(Identifier.of("veercore", "block/template/corner_item")),
                Optional.empty(),
                upKey, downKey, sideKey
        );

        for (MaterialConfig material : MATERIALS) {
            Identifier baseTexture = TextureMap.getId(material.sourceBlock());

            TextureMap itemTextures = new TextureMap()
                    .put(upKey, baseTexture)
                    .put(downKey, baseTexture)
                    .put(sideKey, baseTexture);

            String outputItemName = material.prefix() + "_corner";

            itemTemplate.upload(
                    Identifier.of("veercore", "item/" + outputItemName),
                    itemTextures,
                    itemModelGenerator.writer
            );
        }
    }
}