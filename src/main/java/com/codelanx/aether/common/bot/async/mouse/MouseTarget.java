package com.codelanx.aether.common.bot.async.mouse;

import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;

public abstract class MouseTarget {

    public void hover() {
        SpriteItem target = null;
        Mouse.move(target);
    }

    public abstract ClickType getType();
}
