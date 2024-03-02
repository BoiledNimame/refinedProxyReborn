package com.boilednimame.refinedproxyreborn.blockEntity;

import com.boilednimame.refinedproxyreborn.RPBlockEntities;
import com.boilednimame.refinedproxyreborn.apiimpl.network.node.RefinedProxyNetworkNode;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/*
 * ref:
 *    https://github.com/refinedmods/refinedstorage/blob/v1.10.6/src/main/java/com/refinedmods/refinedstorage/blockentity/InterfaceBlockEntity.java
 */

public class RefinedProxyBlockEntity extends NetworkNodeBlockEntity<RefinedProxyNetworkNode>{
    public static final BlockEntitySynchronizationParameter<Integer, RefinedProxyBlockEntity> COMPARE = IComparable.createParameter();

    public RefinedProxyBlockEntity(BlockPos pos, BlockState state) {
        super(RPBlockEntities.REFINEDPROXY, pos, state, RefinedProxyNetworkNode.class);

        dataManager.addWatchedParameter(COMPARE);
    }

    @Override
    @Nonnull
    public RefinedProxyNetworkNode createNode(Level level, BlockPos blockPos) {
        return new RefinedProxyNetworkNode(level, blockPos);
    }
}
