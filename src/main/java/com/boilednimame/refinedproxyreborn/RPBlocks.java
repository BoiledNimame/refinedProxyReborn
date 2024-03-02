package com.boilednimame.refinedproxyreborn;

import com.boilednimame.refinedproxyreborn.block.RefinedProxyBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/*
 * ref:
 *    https://github.com/refinedmods/refinedstorage/blob/v1.10.6/src/main/java/com/refinedmods/refinedstorage/RSBlocks.java
 */

public class RPBlocks implements ObjectID {
    public static final RegistryObject<RefinedProxyBlock> REFINED_PROXY;
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RP.ID);

    static {
        REFINED_PROXY = BLOCKS.register(ID_REFINEDPROXY, RefinedProxyBlock::new);
    }
    private RPBlocks() {
    }

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
