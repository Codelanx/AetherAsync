package com.codelanx.aether.common;

import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.json.item.ItemStack;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;

import java.util.regex.Pattern;

//many input calls here will NOT delegate to UserInput
//instead, these methods themselves should be passed to UserInput a la UserInput#runemateInput
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
                    return false;
                }
                if (stack.getQuantity() == 1) {
                    return item.click();
                } else {
                    //TODO: Withdraw a partial amount
                    //atm this doesn't auto-account for filling the inventory
                    //I'd prefer not using a pattern but don't have much choice for a multiple-input option
                    if (item.interact("Withdraw-" + stack.getQuantity())) {

                    } else if (item.interact("Withdraw-X")) {
                        //chat input is needed now
                        //TODO: wait until
                        //Thread.sleep(UserInput.getMinimumClick());
                        //TODO: Chat input
                    }
                    item.interact(Pattern.compile("Withdraw-(X" + stack.getQuantity() + ")"));
                    //if (item.interact("Withdraw-X"))
                }
                Caches.forBank().replaceFirst(stack.getMaterial().toInquiry(), i -> i.derive(-stack.getQuantity()));
                Caches.forInventory().invalidateByType(stack.getMaterial());
            }
            return false;
        }

    }
}
