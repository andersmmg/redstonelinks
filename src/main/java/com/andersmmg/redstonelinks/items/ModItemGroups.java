package com.andersmmg.redstonelinks.items;

import com.andersmmg.redstonelinks.RedstoneLinks;
import com.andersmmg.redstonelinks.blocks.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class ModItemGroups {
    public static final ItemGroup FALLOUT_GROUP = Registry.register(Registries.ITEM_GROUP,
            RedstoneLinks.id("redstonelinks"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.redstonelinks"))
                    .icon(() -> new ItemStack(ModBlocks.LINKED_BLOCK)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.LINKED_BLOCK);
                        entries.add(ModItems.LINKER_ITEM);
                    }).build());

    public static void registerItemGroups() {
        RedstoneLinks.LOGGER.info("Registering Mod Item Groups for " + RedstoneLinks.MOD_ID);
    }
}