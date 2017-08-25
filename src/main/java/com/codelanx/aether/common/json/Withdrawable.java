package com.codelanx.aether.common.json;

import com.codelanx.aether.common.json.item.ItemStack;

import java.util.stream.Stream;

/**
 * Created by rogue on 8/22/2017.
 */
public interface Withdrawable {
    
    public Stream<ItemStack> fullInventoryWithdrawl(int spare);
    
    default public Stream<ItemStack> fullInventoryWithdrawl() {
        return this.fullInventoryWithdrawl(28);
    }
}
