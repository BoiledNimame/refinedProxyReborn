package com.boilednimame.refinedproxyreborn;

import com.boilednimame.refinedproxyreborn.config.ServerConfig;
import com.boilednimame.refinedproxyreborn.datageneration.DataGenerator;
import com.boilednimame.refinedproxyreborn.setup.CommonSetup;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RP.ID)
public class RP
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String ID = "refinedproxyreborn";

    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    public RP()
    {
        MinecraftForge.EVENT_BUS.register(this);

        RPBlocks.register();
        RPItems.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(BlockEntityType.class, CommonSetup::onRegisterBlockEntities);

        FMLJavaModLoadingContext.get().getModEventBus().register(new DataGenerator());

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
