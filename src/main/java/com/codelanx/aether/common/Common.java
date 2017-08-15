package com.codelanx.aether.common;

import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.json.item.ItemStack;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;

public class Common {

    public static final class Banks {

        private Banks() {}

        public static boolean depositInventory() {
            Caches.forInventory().invalidateAll();
            return com.runemate.game.api.hybrid.local.hud.interfaces.Bank.depositInventory();
        }

        //true on failure, to allow breaking a stream pipeline
        public static boolean withdrawItem(ItemStack stack) {
            while (!Bank.withdraw(stack.getMaterial().getId(), stack.getQuantity())) {
                if (!Bank.contains(stack.getMaterial().getId())) {
                    return true;
                }
            }
            SpriteItem item = Caches.forBank().get(stack.getMaterial().toInquiry()).findAny().orElse(null);
            if (item != null) {
                //here's where we can note offsets
                if (item.getQuantity() < stack.getQuantity()) {
                    return true;
                }
                Caches.forBank().replaceFirst(stack.getMaterial().toInquiry(), i -> i.derive(-stack.getQuantity()));
            }
            return false;
        }

    }
}
