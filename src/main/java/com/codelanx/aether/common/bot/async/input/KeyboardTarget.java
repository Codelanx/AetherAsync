package com.codelanx.aether.common.bot.async.input;

import com.codelanx.aether.common.Randomization;
import com.runemate.game.api.hybrid.input.Keyboard;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by rogue on 8/13/2017.
 */
public class KeyboardTarget extends InputTarget {

    private final String input;
    private final boolean enter;
    
    public KeyboardTarget(String input, boolean enter) {
        this.input = input;
        this.enter = enter;
    }
    
    @Override
    public void attempt() {
        this.doAttempt(() -> {
            //TODO: keyboard entering, as well as manual sleep (yay our own thread)
            double wpm = Randomization.WPM.getRandom(ThreadLocalRandom::nextDouble).doubleValue();
            double mpc = (1000D / (wpm * 5) * 60); //WPM -> CPM -> CPS -> milliseconds per character
            return Keyboard.type(this.input, this.enter, (int) mpc);
        });
    }
}
