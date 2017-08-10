package com.codelanx.aether.common;

import com.codelanx.aether.common.item.ItemStack;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;

public class Common {

    public static final class Banks {

        private Banks() {}

        public static void depositInventory() {
            AetherBot.get().getInventory().invalidateAll();
            com.runemate.game.api.hybrid.local.hud.interfaces.Bank.depositInventory();
        }

        //true on failure, to allow breaking a stream pipeline
        public static boolean withdrawItem(ItemStack stack) {
            while (!Bank.withdraw(stack.getMaterial().getId(), stack.getQuantity())) {
                if (!Bank.contains(stack.getMaterial().getId())) {
                    return true;
                }
            }
            AetherBot.get().getInventory().update(stack.getMaterial(), stack.getQuantity());
            return false;
        }

    }
}
