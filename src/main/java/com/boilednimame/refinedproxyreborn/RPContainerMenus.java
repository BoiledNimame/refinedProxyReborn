package com.boilednimame.refinedproxyreborn;

import com.boilednimame.refinedproxyreborn.container.RefinedProxyContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RP.ID)
public class RPContainerMenus implements ObjectID {
    @ObjectHolder(ID_REFINEDPROXY)
    public static final MenuType<RefinedProxyContainerMenu> REFINEDPROXY = null;

    private RPContainerMenus() {
    }
}
