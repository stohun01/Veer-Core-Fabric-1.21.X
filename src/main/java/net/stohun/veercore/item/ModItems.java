package net.stohun.veercore.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.stohun.veercore.VeerCore;

public class ModItems {
    public static final Item ACID = registerItem("acid", new Item(new Item.Settings()));
    public static final Item RAW_EMERALD = registerItem("raw_emerald", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(VeerCore.MOD_ID, name), item);
    }

    public static void registerModItems() {
        VeerCore.LOGGER.info("Registering Mod Items for " + VeerCore.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(ACID);
            entries.add(RAW_EMERALD);
        });
    }
}
