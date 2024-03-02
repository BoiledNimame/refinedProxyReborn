package com.boilednimame.refinedproxyreborn.apiimpl.network.node;

import com.boilednimame.refinedproxyreborn.ObjectID;
import com.boilednimame.refinedproxyreborn.RP;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/*
 * reference:
 *    https://github.com/refinedmods/refinedstorage/blob/v1.10.6/src/main/java/com/refinedmods/refinedstorage/apiimpl/network/node/InterfaceNetworkNode.java
 *    https://git.tilera.org/tilera/Exposer/src/branch/master/src/main/java/ley/anvil/exposer/cap/ItemHandlerExposer.java
 *
 * 疑問点:
 *  ・内部インベントリはどうすればいいのか
 *  ・外部からのアイテムリクエストはどう処理するのか? またその受け口となるイベントは? -> 解決済 (=> 解決策)
 *   ただし, これは「このブロックの内部インベントリを定義してそれをネットワーク上のアイテムと同期させる」
 *   ことで解決可能かもしれないが, 負荷が心配
 *
 * 挙動から参照先を絞る:
 * 「ネットワーク上のアイテムを捜索する」 -> crafter辺り ?
 * 「ネットワーク上のフルブロック機器」 -> Interface ?
 * 「ネットワーク上と外部を繋ぐ」 -> externalStorage ?
 *
 *  解決策:
 *  ・モロexposure(これの元ネタ)のコードを参照してみる(verが古いので, そこを解決すれば使えるだろうか?) -> 使えた.
 */

public class RefinedProxyNetworkNode extends NetworkNode implements IItemHandler, IComparable, ObjectID, IStorageCacheListener<ItemStack> {

    public static final ResourceLocation ID = new ResourceLocation(RS.ID, ID_REFINEDPROXY);

    protected RefinedProxyNetworkNode(Level level, BlockPos pos, INetwork network) {
        super(level, pos);
        this.network = network;
        invalidate();
    }

    @Override
    public int getEnergyUsage() {
        return RP.SERVER_CONFIG.getrRefinedProxy().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    // ここから新規(IItemHandler)

    private final INetwork network;
    private ItemStack[] networkCacheItemData; // このブロックの内部インベントリ?


    // 参照: exposer
    @Override
    public int getSlots() {
        // getStackInSlot が getSlots して引数を決定しているので +1 して null防止してる?
        return networkCacheItemData.length + 1;
    }

    // 参照: exposer
    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        // 無を参照しないようにしている?
        if (slot < networkCacheItemData.length) {
            return networkCacheItemData[slot];
        }
        return ItemStack.EMPTY;
    }

    // 参照: exposer
    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        // 元コードではnull回避でnull -> emptyとする処理を挟んでいたがどうやら必要なくなった?(IDEが言っているだけなので信用してはならない)
        // そもそもStackUtilsにnullToEmptyが無くなっていたようなので, Minecraft本体の部分が進化したのかも
        return network.insertItem(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM);
    }

    // 参照: exposer
    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < networkCacheItemData.length) {
            return network.extractItem(
                    networkCacheItemData[slot],
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
    @SuppressWarnings("all") // インスペクションとかわからん;;;; ごめん;;
    private void invalidate() {
        this.networkCacheItemData = network
                .getItemStorageCache()
                .getList()
                .getStacks()
                .toArray(new ItemStack[0]); // バグの原因たり得る? IDEは型違うと言ってるけど
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

    // IComparable: どこ見てもこの実装なのでこれで良いかと思われる

    private int compare = IComparer.COMPARE_NBT;

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

}
