package com.boilednimame.refinedproxyreborn.block;

import com.boilednimame.refinedproxyreborn.blockEntity.RefinedProxyBlockEntity;
import com.boilednimame.refinedproxyreborn.container.RefinedProxyContainerMenu;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;

import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.refinedmods.refinedstorage.util.BlockUtils;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

/*
 * reference:
 *   https://github.com/refinedmods/refinedstorage/blob/v1.10.6/src/main/java/com/refinedmods/refinedstorage/block/InterfaceBlock.java
 * -> I think this Block is like Interface ... so I referred to .
 *   https://github.com/refinedmods/refinedstorage/blob/v1.10.6/src/main/java/com/refinedmods/refinedstorage/block/ExternalStorageBlock.java
 * -> and it would be good if we could do the opposite of external storage.
 */

public class RefinedProxyBlock extends NetworkNodeBlock {

    /*
     * 必要そうなメソッド候補::
     *  newBlockEntity (済)
     *  hasConnectedState (済)
     *  use (不完全)
     */

    public RefinedProxyBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    // 必須?
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RefinedProxyBlockEntity(pos, state);
    }

    // 必須: 恐らくはケーブルが接続可能かどうか
    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
