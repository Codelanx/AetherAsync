package com.codelanx.aether.common;

import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.input.UserInputException;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.commons.logging.Logging;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

//many input calls here will NOT delegate to UserInput
//instead, these methods themselves should be passed to UserInput a la UserInput#runemateInput
public class Common {

    public static final class Banks {

        private static final AtomicInteger lastAmountInput = new AtomicInteger(); //specific withdrawls

        private Banks() {}

        public static boolean depositInventory() {
            Caches.forInventory().invalidateAll();
            return Bank.depositInventory();
        }

        public static boolean depositItem(ItemStack stack) {
            if (!Bank.open()) {
                Logging.warning("Banks#depositItem called without an open bank");
                return false;
            }
            //get last item in inventory
            SpriteItem item = Caches.forInventory().get(stack.getMaterial()).max(Comparator.comparing(SpriteItem::getIndex)).orElse(null);
            if (item == null) {
                Logging.warning("No item found in inventory for Banks#depositItem");
                return false;
            }
            if (true) {
                return Bank.deposit(item, stack.getQuantity());
            }
            return itemInteraction(item, stack, false, true);
        }

        //TODO: Randomization, though this depends on how much of the api accounts for these
        private static boolean itemInteraction(SpriteItem item, ItemStack stack, boolean withdraw, boolean all) {
            String prefix = withdraw ? "Withdraw-" : "Deposit-";
            boolean back = false;
            if (item.getQuantity() > stack.getQuantity()) {
                //specific amount
                int c = stack.getQuantity();
                if (c < 5) {
                    int clicks = 0;
                    int total = 0;
                    for (int i = 0; i < c << 1 && clicks < c; i++) { //double retries for all
                        if (item.click()) {
                            clicks++;
                        }
                    }
                    back = total >> 1 > clicks; //if the total/2 is greater than i, then we retried more than we succeeded and thus, failed
                    if (!back && clicks > 0) {
                        Caches.forBank().invalidateByType(stack.getMaterial());
                        Caches.forInventory().invalidateByType(stack.getMaterial());
                    }
                } else if (c == 5 || c == 10) {
                    back = item.interact(prefix + c);
                } else if (Banks.lastAmountInput.get() == c && item.interact(prefix + c)) {
                    back = true;
                } else {
                    //input it again if failed
                    Banks.lastAmountInput.set(c);
                    back = withdraw ? Bank.withdraw(item, c) : Bank.deposit(item, c);//item.interact(prefix + "X") && Banks.delayAndEnterBankChatInput("" + c);
                }
            } else {
                //as much as we can
                String act = prefix + "All";
                if (withdraw) {
                    if (!all && item.getQuantity() < stack.getQuantity()) {
                        throw new UserInputException("Not enough items in bank for " + stack.getMaterial() + "; Requested: " + stack.getQuantity() + ", found: " + item.getQuantity());
                    }
                    if (BotConfig.USES_BANK_PLACEHOLDER.as(boolean.class)) {
                        act = "Placeholder";
                    }
                }
                //withdraw all
                item.interact(act);
            }

            if (back) {
                //TODO: Cache updating
                Caches.forBank().invalidateByType(stack.getMaterial());
                Caches.forInventory().invalidateByType(stack.getMaterial());
            }
            return back;
        }

        private static boolean delayAndEnterBankChatInput(String input) {
            //hard delay using runemate api
            //Execution.delayUntil()
            //chat input is needed now
            //TODO: Chat input
            //Keyboard.type(input);
            return false;
        }

        public static boolean withdrawItem(ItemStack stack) {
            return Banks.withdrawItem(stack, stack.getQuantity() == Integer.MAX_VALUE);
        }

        public static boolean withdrawItem(ItemStack stack, boolean all) {
            if (!Bank.open()) {
                Logging.warning("Banks#withdrawItem called without an open bank");
                return false;
            }
            /*while (!Bank.withdraw(stack.getMaterial().getId(), stack.getQuantity())) {
                if (!Bank.contains(stack.getMaterial().getId())) {
                    return true;
                }
            }*/
            SpriteItem item = Caches.forBank().get(stack.getMaterial()).findAny().orElse(null);
            if (item == null) {
                Logging.warning("No item found in bank for Banks#withdrawItem");
                return false;
            } else {
                Logging.info("Found item: " + item);
                ItemDefinition def = item.getDefinition();
                Logging.info("stackable: " + def.stacks() + ", equipable: " + def.isEquipable());
            }
            if (true) {
                boolean back = Bank.withdraw(item, stack.getQuantity());
                Caches.forBank().invalidateByType(stack.getMaterial());
                Caches.forInventory().invalidateByType(stack.getMaterial());
                return back;
            }
            return itemInteraction(item, stack, true, all);
        }
    }
}
