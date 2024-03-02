package com.boilednimame.refinedproxyreborn.container;

import com.boilednimame.refinedproxyreborn.RPContainerMenus;
import com.boilednimame.refinedproxyreborn.blockEntity.RefinedProxyBlockEntity;
import com.refinedmods.refinedstorage.container.BaseContainerMenu;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class RefinedProxyContainerMenu extends BaseContainerMenu {
    public RefinedProxyContainerMenu(RefinedProxyBlockEntity blockEntity, @NotNull Player player, int windowId) {
        super(RPContainerMenus.REFINEDPROXY, blockEntity, player, windowId);
        // 現在の接続状況とか表示できるといいかも
        addPlayerInventory(8, 141);
    }
}
