package com.andersmmg.redstonelinks.blocks;

import com.andersmmg.redstonelinks.RedstoneLinks;
import com.andersmmg.redstonelinks.blocks.custom.LinkedBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlocks {
    public static final Block LINKED_BLOCK = registerBlock("linked_block", new LinkedBlock(FabricBlockSettings.copyOf(Blocks.GLASS)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, RedstoneLinks.id(name), block);
    }

    private static Block registerBlockOnly(String name, Block block) {
        return Registry.register(Registries.BLOCK, RedstoneLinks.id(name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, RedstoneLinks.id(name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        RedstoneLinks.LOGGER.info("Registering ModBlocks for " + RedstoneLinks.MOD_ID);
    }
}
