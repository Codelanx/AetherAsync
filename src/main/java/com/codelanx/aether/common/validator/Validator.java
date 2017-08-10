package com.codelanx.aether.common.validator;

import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

/**
 * A validator is a simple branch task which evaluations
 * next missions. This can mean either a mission assignment of
 * nothing (wait), or the next logical progression. In most cases,
 * when input is exhausted (e.g. out of food, no food in bank),
 * waiting will be the default response.
 */
public class Validator extends BranchTask {

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public TreeTask successTask() {
        return null;
    }

    @Override
    public TreeTask failureTask() {
        return null;
    }
}
