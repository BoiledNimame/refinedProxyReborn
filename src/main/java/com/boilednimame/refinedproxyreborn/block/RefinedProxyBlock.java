package com.boilednimame.refinedproxyreborn.block;

import com.boilednimame.refinedproxyreborn.blockEntity.RefinedProxyBlockEntity;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.refinedmods.refinedstorage.util.BlockUtils;

import org.jetbrains.annotations.NotNull;

/*
 * reference:
 *   https://github.com/refinedmods/refinedstorage/blob/v1.10.6/src/main/java/com/refinedmods/refinedstorage/block/InterfaceBlock.java
 *   https://github.com/refinedmods/refinedstorage/blob/v1.10.6/src/main/java/com/refinedmods/refinedstorage/block/ExternalStorageBlock.java
 */

public class RefinedProxyBlock extends NetworkNodeBlock {

    public RefinedProxyBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    // 必須?
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RefinedProxyBlockEntity(pos, state);
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
