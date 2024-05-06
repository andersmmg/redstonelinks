package com.andersmmg.redstonelinks.items;

import com.andersmmg.redstonelinks.RedstoneLinks;
import com.andersmmg.redstonelinks.items.custom.LinkerItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {
    public static final Item LINKER_ITEM = registerItem("linker", new LinkerItem(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, RedstoneLinks.id(name), item);
    }

    public static void registerModItems() {
        RedstoneLinks.LOGGER.info("Registering Mod Items for " + RedstoneLinks.MOD_ID);
    }
}
