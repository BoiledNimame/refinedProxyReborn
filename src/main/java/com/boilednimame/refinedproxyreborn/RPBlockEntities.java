package com.boilednimame.refinedproxyreborn;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RP.ID)
public class RPBlockEntities implements ObjectID {
    @ObjectHolder(ID_REFINEDPROXY)
    @SuppressWarnings("rawtypes")
    public static final BlockEntityType<NetworkNodeBlockEntity> REFINEDPROXY = null;

    private RPBlockEntities() {
    }
}
