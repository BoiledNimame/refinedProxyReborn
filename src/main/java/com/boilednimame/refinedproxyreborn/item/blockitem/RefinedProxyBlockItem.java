package com.boilednimame.refinedproxyreborn.item.blockitem;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
import net.minecraft.world.item.Item;

public class RefinedProxyBlockItem extends BaseBlockItem {
    public RefinedProxyBlockItem(BaseBlock block, Properties builder) {
        super(block, new Item.Properties().tab(RS.CREATIVE_MODE_TAB)); // RSタブに入れておけばええか;
    }
}
