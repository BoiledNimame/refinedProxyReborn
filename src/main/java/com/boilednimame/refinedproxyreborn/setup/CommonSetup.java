package com.boilednimame.refinedproxyreborn.setup;

import com.boilednimame.refinedproxyreborn.ObjectID;
import com.boilednimame.refinedproxyreborn.RP;
import com.boilednimame.refinedproxyreborn.RPBlocks;
import com.boilednimame.refinedproxyreborn.apiimpl.network.node.RefinedProxyNetworkNode;
import com.boilednimame.refinedproxyreborn.blockEntity.RefinedProxyBlockEntity;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.BaseBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Objects;

public class CommonSetup implements ObjectID {
    private CommonSetup() {
    }

    public static void onCommonSetup(FMLCommonSetupEvent e) {
        API.instance().getNetworkNodeRegistry()
                .add(RefinedProxyNetworkNode.ID, (tag, world, pos)
                        -> readAndReturn(tag, new RefinedProxyNetworkNode(world, pos)
                ));
    }

    private static INetworkNode readAndReturn(CompoundTag tag, NetworkNode node) {
        node.read(tag);
        return node;
    }

    public static void onRegisterBlockEntities(RegistryEvent.Register<BlockEntityType<?>> e) {
        e.getRegistry().register(registerSynchronizationParameters(
                BlockEntityType.Builder.of(
                        RefinedProxyBlockEntity::new,
                        RPBlocks.REFINED_PROXY.get()).build(null).setRegistryName(RP.ID, ID_REFINEDPROXY)
        ));
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerSynchronizationParameters(BlockEntityType<T> t) {
        BaseBlockEntity blockEntity = (BaseBlockEntity) t.create(BlockPos.ZERO, null);

        Objects.requireNonNull(blockEntity).getDataManager().getParameters().forEach(BlockEntitySynchronizationManager::registerParameter);

        return t;
    }
}