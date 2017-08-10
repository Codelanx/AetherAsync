package com.codelanx.aether.construction;

import com.codelanx.aether.common.AetherBot;
import com.codelanx.aether.construction.mission.ConstructionMission;
import com.codelanx.aether.construction.mission.CraftTarget;

//are we really gonna leave this as a tree, or maybe go full on grid/grammar?
public class BasicBitchBot extends AetherBot {

    private static BasicBitchBot instance; //yuck

    public BasicBitchBot() {
        BasicBitchBot.instance = this;
    }

    @Override
    public void onStart(String... strings) {
        this.getBrain().register(new ConstructionMission(CraftTarget.OAK_LARDER));
        //this.brain.registerImmediate(new TestTask());
    }

    //let's try to avoid this
    public static BasicBitchBot get() {
        return BasicBitchBot.instance;
    }


}
