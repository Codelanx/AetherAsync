package com.codelanx.aether.common.input.type;

import com.runemate.game.api.hybrid.entities.details.Interactable;

//TODO: We're gonna need combat tracking to ensure proper state
public class CombatTarget extends MouseTarget {

    //TODO: Left/right click determination of target (probably combat level and settings to take into account)
    public CombatTarget(Interactable target) {
        super(target);
    }

    //TODO: Finish input validation at moment of combat start. Rather, we need a way to ensure combat was attempted,
    //without resorting to stalling the input validation forever (so it would return false on failure)
    //I'd like to do this without having a repeating task internally
}
