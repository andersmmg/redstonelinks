package com.andersmmg.redstonelinks.datagen;

import com.andersmmg.redstonelinks.blocks.ModBlocks;
import com.andersmmg.redstonelinks.blocks.custom.LinkedReceiverBlock;
import com.andersmmg.redstonelinks.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LINKED_BLOCK);
        registerLinkedReceiver(blockStateModelGenerator, ModBlocks.LINKED_RECEIVER);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.LINKER_ITEM, Models.GENERATED);
    }

    private void registerLinkedReceiver(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        Identifier identifier = TexturedModel.CUBE_ALL.upload(block, blockStateModelGenerator.modelCollector);
        Identifier identifier2 = blockStateModelGenerator.createSubModel(block, "_on", Models.CUBE_ALL, TextureMap::all);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(BlockStateModelGenerator.createBooleanModelMap(LinkedReceiverBlock.POWERED, identifier2, identifier)));
    }
}