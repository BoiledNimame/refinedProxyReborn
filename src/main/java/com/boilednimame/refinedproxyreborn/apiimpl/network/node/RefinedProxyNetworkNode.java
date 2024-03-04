package com.boilednimame.refinedproxyreborn.apiimpl.network.node;

import com.boilednimame.refinedproxyreborn.ObjectID;
import com.boilednimame.refinedproxyreborn.RP;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
 *  ・モロexposure(これの元ネタ)のコードを参照してみる(verが古いので, そこを解決すれば使えるだろうか?) -> 使えた. -> RefinedProxyItemHandlerへ移した
 */

public class RefinedProxyNetworkNode extends NetworkNode implements IItemHandler, IComparable, ObjectID, IStorageCacheListener<ItemStack> {

    public static final ResourceLocation ID =  new ResourceLocation(RP.ID, ID_REFINEDPROXY);

    private static final Logger logger = RP.getLogger();

    public RefinedProxyNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
        invalidate();
    }

    @Override
    public int getEnergyUsage() {
        return RP.SERVER_CONFIG.getrRefinedProxy().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    // Network関連

    private INetwork network;

    @Override
    public void onConnected(INetwork network) {
        this.onConnectedStateChange(network, true, ConnectivityStateChangeCause.GRAPH_CHANGE);
        logger.info("RefinedProxy try to connect this network: " + network);
        this.network = network;

        if ( this.network != null ) {
            logger.info("successfully to connect to this network: " + this.network.getLevel() + ", " + this.network.getPosition());
            logger.info("try to add ItemStorageCache to RefinedProxy");
            network.getItemStorageCache().addListener(this);
            this.invalidate();
        } else {
            logger.warn("RefinedProxy may failed to connect to network!");
        }
    }

    @Override
    public void onDisconnected(INetwork network) {
        super.onDisconnected(network);

        network.getItemStorageCache().removeListener(this);
    }

    // ここから新規(IItemHandler)

    private ItemStack[] networkCacheItemData; // このブロックの内部インベントリ?


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

    public IItemHandler getItems() {
        return this;
    }
}
