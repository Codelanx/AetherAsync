package com.codelanx.aether.common.action;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.Spell;
import com.runemate.game.api.hybrid.local.SpellBook;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.osrs.local.hud.interfaces.Magic;
import com.runemate.game.api.rs3.local.hud.Powers;

public interface SimpleSpell extends Spell {

    @Override
    default public boolean activate() {
        return HMagic.inputter.apply(this.getRawSpell());
    }

    @Override
    default public SpellBook getSpellBook() {
        return this.getRawSpell().getSpellBook();
    }

    @Override
    default public boolean isSelected() {
        return this.getRawSpell().isSelected();
    }

    @Override
    default public InterfaceComponent getComponent() {
        return this.getRawSpell().getComponent();
    }

    default public boolean isRS3() {
        return this.getRs3Spell() != null;
    }

    default public boolean isOSRS() {
        return this.getOsrsSpell() != null;
    }

    public Powers.Magic getRs3Spell();

    public Magic getOsrsSpell();

    default public Spell getRawSpell() {
        boolean rs3 = Environment.isRS3();
        Spell back = rs3 ? this.getRs3Spell() : this.getOsrsSpell();
        if (back == null) {
            throw new IllegalStateException("Using non-existant spell in " + (rs3 ? "RS3" : "OSRS") + ": " + this.name());
        }
        return back;
    }

    public String name();
}
