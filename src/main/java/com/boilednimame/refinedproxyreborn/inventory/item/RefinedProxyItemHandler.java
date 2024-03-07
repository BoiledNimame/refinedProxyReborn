package com.boilednimame.refinedproxyreborn.inventory.item;

import com.boilednimame.refinedproxyreborn.RP;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RefinedProxyItemHandler implements IItemHandler, IStorageCacheListener<ItemStack> {
    private INetwork network;
    private ItemStack[] networkCacheItemData;

    private boolean onInvalidate;

    private static final Logger logger = RP.getLogger();

    public RefinedProxyItemHandler() {
        this.networkCacheItemData = new ItemStack[]{ItemStack.EMPTY};
    }

    public void connectNetwork(INetwork network) {
        this.network = network;
        if ( this.network != null ) {
            logger.info("successfully to connect to this network: " + this.network.getLevel() + ", " + this.network.getPosition());
            logger.info("try to add ItemStorageCache to RefinedProxy");
            this.invalidate();
        } else {
            logger.warn("RefinedProxy may failed to connect to network!");
        }
        this.onInvalidate = false;
        invalidate();
    }

    public void disconnectNetwork() {
        this.network = null;
        logger.info("RefinedProxy is Disconnect from Network.");
    }

    // 参照: exposer
    @Override
    public int getSlots() {
        return this.networkCacheItemData.length + 1;
    }

    // 参照: exposer
    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < this.networkCacheItemData.length) {
            return this.networkCacheItemData[slot];
        }
        return ItemStack.EMPTY;
    }

    // 参照: exposer
    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return Objects.requireNonNull(network).insertItem(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM);
    }

    // 参照: exposer
    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.networkCacheItemData.length != 0) {
            if (slot < this.networkCacheItemData.length) {
                return Objects.requireNonNull(this.network).extractItem(
                        this.networkCacheItemData[slot],
                        amount,
                        IComparer.COMPARE_NBT | IComparer.COMPARE_QUANTITY, // exposerとコードは違うけど値は同じ
                        simulate ? Action.SIMULATE : Action.PERFORM);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    // IStorageCacheListener<ItemStack>

    // 参照: exposer
    private void invalidate() {
        if (this.network != null) {
            if (!this.onInvalidate) {
                this.onInvalidate = true;
                this.networkCacheItemData = Arrays.stream(network
                                .getItemStorageCache()
                                .getList()
                                .getStacks()
                                .toArray(new StackListEntry[0]))
                        .map( m -> (ItemStack) m.getStack() )
                        .toList()
                        .toArray(new ItemStack[0]); // 正常に動作
                this.onInvalidate = false;
            }
        } else {
            logger.warn("running invalidate(), but network is Null!"); // 恐らく, 冗長
        }
    }

    // 参照: exposer
    @Override
    public void onAttached() {

    }
    @Override
    public void onInvalidated() {
        invalidate();
    }
    @Override
    public void onChanged(StackListResult<ItemStack> stackListResult) {
        invalidate();
    }
    @Override
    public void onChangedBulk(List<StackListResult<ItemStack>> list) {
        invalidate();
    }

}
