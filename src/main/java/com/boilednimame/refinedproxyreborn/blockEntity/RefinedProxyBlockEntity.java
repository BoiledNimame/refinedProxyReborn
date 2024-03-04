package com.boilednimame.refinedproxyreborn.blockEntity;

import com.boilednimame.refinedproxyreborn.RPBlockEntities;
import com.boilednimame.refinedproxyreborn.apiimpl.network.node.RefinedProxyNetworkNode;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/*
 * ref:
 *    https://github.com/refinedmods/refinedstorage/blob/v1.10.6/src/main/java/com/refinedmods/refinedstorage/blockentity/InterfaceBlockEntity.java
 */

public class RefinedProxyBlockEntity extends NetworkNodeBlockEntity<RefinedProxyNetworkNode>{
    public static final BlockEntitySynchronizationParameter<Integer, RefinedProxyBlockEntity> COMPARE = IComparable.createParameter();

    private final LazyOptional<IItemHandler> itemsCapability = LazyOptional.of(() -> getNode().getItems());

    public RefinedProxyBlockEntity(BlockPos pos, BlockState state) {
        super(RPBlockEntities.REFINEDPROXY, pos, state, RefinedProxyNetworkNode.class);

        dataManager.addWatchedParameter(COMPARE);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemsCapability.cast();
        }

        return super.getCapability(cap, direction);
    }

    @Override
    @Nonnull
    public RefinedProxyNetworkNode createNode(Level level, BlockPos blockPos) {
        return new RefinedProxyNetworkNode(level, blockPos);
    }
}
