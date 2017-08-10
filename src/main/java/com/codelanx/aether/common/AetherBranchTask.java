package com.codelanx.aether.common;

import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class AetherBranchTask extends BranchTask {
    @Override
    public TreeTask successTask() {
        return null;
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public TreeTask failureTask() {
        return null;
    }
}
