package com.boilednimame.refinedproxyreborn.apiimpl.network.node;

import com.boilednimame.refinedproxyreborn.ObjectID;
import com.boilednimame.refinedproxyreborn.RP;
import com.boilednimame.refinedproxyreborn.inventory.item.RefinedProxyItemHandler;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

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

public class RefinedProxyNetworkNode extends NetworkNode implements IComparable, ObjectID {

    public static final ResourceLocation ID =  new ResourceLocation(RP.ID, ID_REFINEDPROXY);

    public RefinedProxyNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RP.SERVER_CONFIG.getrRefinedProxy().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    private final RefinedProxyItemHandler itemHandler = new RefinedProxyItemHandler();

    @Override
    public void onConnected(INetwork network) {
        this.network = network;
        itemHandler.connectNetwork(network);
        this.onConnectedStateChange(network, true, ConnectivityStateChangeCause.GRAPH_CHANGE);
        network.getItemStorageCache().addListener(itemHandler);
    }

    @Override
    public void onDisconnected(INetwork network) {
        network.getItemStorageCache().removeListener(itemHandler);
        this.onConnectedStateChange(network, false, ConnectivityStateChangeCause.GRAPH_CHANGE);
        this.itemHandler.disconnectNetwork();
        this.network = null;
    }

    @Override
    public void update() {
        super.update();
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
        return itemHandler;
    }
}
