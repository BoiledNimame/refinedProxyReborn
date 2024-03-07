package com.boilednimame.refinedproxyreborn;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/*
 * ref:
 *    https://github.com/refinedmods/refinedstorage/blob/v1.10.6/src/main/java/com/refinedmods/refinedstorage/RSItems.java
 */

public class RPItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RP.ID);

    static {
        registerBlockItemFor();
    }

    private RPItems() {
    }

    private static void registerBlockItemFor() {
        ITEMS.register(RPBlocks.REFINED_PROXY.getId().getPath(),
                () -> new BaseBlockItem((RPBlocks.REFINED_PROXY).get(), new Item.Properties().tab(RS.CREATIVE_MODE_TAB)));
    }

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
