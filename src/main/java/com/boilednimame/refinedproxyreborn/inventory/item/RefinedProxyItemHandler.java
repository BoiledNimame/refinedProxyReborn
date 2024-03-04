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
    private final INetwork network;
    private ItemStack[] networkCacheItemData; // このブロックの内部インベントリ?

    private static final Logger logger = RP.getLogger();

    public RefinedProxyItemHandler(INetwork network) {
        this.network = network;
        invalidate();
    }

    // 参照: exposer
    @Override
    public int getSlots() {
        // getStackInSlot が getSlots して引数を決定しているので +1 して null防止してる?
        return this.networkCacheItemData.length + 1;
    }

    // 参照: exposer
    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        // 無を参照しないようにしている?
        if (slot < this.networkCacheItemData.length) {
            return this.networkCacheItemData[slot];
        }
        logger.warn("Try to getStackInSlot, but... : " + Arrays.toString(this.networkCacheItemData));
        return ItemStack.EMPTY;
    }

    // 参照: exposer
    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        // 元コードではnull回避でnull -> emptyとする処理を挟んでいたがどうやら必要なくなった?(IDEが言っているだけなので信用してはならない)
        // そもそもStackUtilsにnullToEmptyが無くなっていたようなので, Minecraft本体の部分が進化したのかも
        return Objects.requireNonNull(network).insertItem(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM);
    }

    // 参照: exposer
    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < this.networkCacheItemData.length) {
            return Objects.requireNonNull(network).extractItem(
                    this.networkCacheItemData[slot],
                    amount,
                    IComparer.COMPARE_NBT | IComparer.COMPARE_QUANTITY, // 疑問点: 元コードと異なる挙動が予想される
                    simulate ? Action.SIMULATE : Action.PERFORM); // 三項演算子 bool ? bool->true : bool->false
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64; // 1stack
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true; // trueでいいっぽいが
    }

    // IStorageCacheListener<ItemStack>

    // 参照: exposer
    private void invalidate() {
        if (this.network != null) {
            this.networkCacheItemData = Arrays.stream(network
                            .getItemStorageCache()
                            .getList()
                            .getStacks()
                            .toArray(new StackListEntry[0]))
                    .map( m -> (ItemStack) m.getStack() )
                    .toList()
                    .toArray(new ItemStack[0]);
        } else {
            logger.warn("running invalidate(), but network is Null!");
        }
    }

    // 参照: exposer
    // 本当にこれで良いのか???と思ったけど, 他の実装もこんな調子だったのでパス
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
