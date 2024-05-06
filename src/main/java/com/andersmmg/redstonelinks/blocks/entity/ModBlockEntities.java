package com.andersmmg.redstonelinks.blocks.entity;

import com.andersmmg.redstonelinks.RedstoneLinks;
import com.andersmmg.redstonelinks.blocks.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntities {
    public static final BlockEntityType<LinkedBlockEntity> LINKED_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, RedstoneLinks.id("linked_block_entity"),
                    FabricBlockEntityTypeBuilder.create(LinkedBlockEntity::new,
                            ModBlocks.LINKED_BLOCK).build());

    public static void registerBlockEntities() {
        RedstoneLinks.LOGGER.info("Registering Block Entities for " + RedstoneLinks.MOD_ID);
    }
}
