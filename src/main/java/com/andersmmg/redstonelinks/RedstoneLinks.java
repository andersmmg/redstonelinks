package com.andersmmg.redstonelinks;

import com.andersmmg.redstonelinks.blocks.ModBlocks;
import com.andersmmg.redstonelinks.blocks.entity.ModBlockEntities;
import com.andersmmg.redstonelinks.items.ModItemGroups;
import com.andersmmg.redstonelinks.items.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedstoneLinks implements ModInitializer {
    public static final String MOD_ID = "redstonelinks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        ModItemGroups.registerItemGroups();
        ModBlockEntities.registerBlockEntities();
    }
}
