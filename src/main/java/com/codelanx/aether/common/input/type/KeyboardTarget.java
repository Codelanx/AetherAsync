package com.codelanx.aether.common.input.type;

import com.codelanx.aether.common.Randomization;
import com.codelanx.aether.common.input.InputTarget;
import com.runemate.game.api.hybrid.input.Keyboard;

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
            double wpm = Randomization.WPM.getValue().doubleValue();
            double mpc = 1000D / ((wpm * 5) * 60); //WPM -> CPM -> CPS -> milliseconds per character
            return Keyboard.type(this.input, this.enter, (int) mpc);
        });
    }

    @Override
    public String toString() {
        return "KeyboardTarget{" +
                "enter=" + enter +
                ", input='" + input + '\'' +
                '}';
    }
}
