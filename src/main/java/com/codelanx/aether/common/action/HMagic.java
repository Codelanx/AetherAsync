package com.codelanx.aether.common.action;

import com.runemate.game.api.hybrid.local.Spell;
import com.runemate.game.api.osrs.local.hud.interfaces.Magic;
import com.runemate.game.api.osrs.local.hud.interfaces.Prayer;
import com.runemate.game.api.rs3.local.hud.Powers;

import java.util.function.Function;

public enum HMagic implements SimpleSpell {

    ENCHANT_LVL_1(Powers.Magic.LVL_1_ENCHANT, Magic.LVL_1_ENCHANT),
    ENCHANT_LVL_2(Powers.Magic.LVL_2_ENCHANT, Magic.LVL_2_ENCHANT),
    ENCHANT_LVL_3(Powers.Magic.LVL_3_ENCHANT, Magic.LVL_3_ENCHANT),
    ENCHANT_LVL_4(Powers.Magic.LVL_4_ENCHANT, Magic.LVL_4_ENCHANT),
    ENCHANT_LVL_5(Powers.Magic.LVL_5_ENCHANT, Magic.LVL_5_ENCHANT),
    ENCHANT_LVL_6(Powers.Magic.LVL_6_ENCHANT, Magic.LVL_6_ENCHANT),
    ENCHANT_CROSSBOW_BOLT(Powers.Magic.ENCHANT_CROSSBOW_BOLT, Magic.ENCHANT_CROSSBOW_BOLT),
    WATER_STRIKE(Powers.Magic.WATER_STRIKE, Magic.WATER_STRIKE),
    WATER_BOLT(Powers.Magic.WATER_BOLT, Magic.WATER_BOLT),
    WATER_BLAST(Powers.Magic.WATER_BLAST, Magic.WATER_BLAST),
    WATER_WAVE(Powers.Magic.WATER_WAVE, Magic.WATER_WAVE),
    EARTH_STRIKE(Powers.Magic.EARTH_STRIKE, Magic.EARTH_STRIKE),
    EARTH_BOLT(Powers.Magic.EARTH_BOLT, Magic.EARTH_BOLT),
    EARTH_BLAST(Powers.Magic.EARTH_BLAST, Magic.EARTH_BLAST),
    EARTH_WAVE(Powers.Magic.EARTH_WAVE, Magic.EARTH_WAVE),
    FIRE_STRIKE(Powers.Magic.FIRE_STRIKE, Magic.FIRE_STRIKE),
    FIRE_BOLT(Powers.Magic.FIRE_BOLT, Magic.FIRE_BOLT),
    FIRE_BLAST(Powers.Magic.FIRE_BLAST, Magic.FIRE_BLAST),
    FIRE_WAVE(Powers.Magic.FIRE_WAVE, Magic.FIRE_WAVE),
    CHARGE_AIR_ORB(Powers.Magic.CHARGE_AIR_ORB, Magic.CHARGE_AIR_ORB),
    CHARGE_WATER_ORB(Powers.Magic.CHARGE_WATER_ORB, Magic.CHARGE_WATER_ORB),
    CHARGE_EARTH_ORB(Powers.Magic.CHARGE_EARTH_ORB, Magic.CHARGE_EARTH_ORB),
    CHARGE_FIRE_ORB(Powers.Magic.CHARGE_FIRE_ORB, Magic.CHARGE_FIRE_ORB),

    //law/teleport spells
    TELEPORT_FALADOR(Powers.Magic.FALADOR_TELEPORT, Magic.FALADOR_TELEPORT),
    TELEPORT_LUMBRIDGE(Powers.Magic.LUMBRIDGE_TELEPORT, Magic.LUMBRIDGE_TELEPORT),
    TELEPORT_CAMELOT(Powers.Magic.CAMELOT_TELEPORT, Magic.CAMELOT_TELEPORT),
    TELEPORT_WATCHTOWER(Powers.Magic.WATCHTOWER_TELEPORT, Magic.WATCHTOWER_TELEPORT),
    TELEPORT_TROLLHEIM(Powers.Magic.TROLLHEIM_TELEPORT, Magic.TROLLHEIM_TELEPORT),
    TELEPORT_ARDOUGNE(Powers.Magic.ARDOUGNE_TELEPORT, Magic.ARDOUGNE_TELEPORT),
    TELEPORT_VARROCK(Powers.Magic.VARROCK_TELEPORT, Magic.VARROCK_TELEPORT),
    TELEKINETIC_GRAB(Powers.Magic.TELEKINETIC_GRAB, Magic.TELEKINETIC_GRAB),

    //nature spells
    LOW_LEVEL_ALCHEMY(Powers.Magic.LOW_LEVEL_ALCHEMY, Magic.LOW_LEVEL_ALCHEMY),
    HIGH_LEVEL_ALCHEMY(Powers.Magic.HIGH_LEVEL_ALCHEMY, Magic.HIGH_LEVEL_ALCHEMY),
    SUPERHEAT_ITEM(Powers.Magic.SUPERHEAT_ITEM, Magic.SUPERHEAT_ITEM),
    BONES_TO_PEACHES(Powers.Magic.BONES_TO_PEACHES, Magic.BONES_TO_PEACHES),
    BONES_TO_BANANAS(Powers.Magic.BONES_TO_BANANAS, Magic.BONES_TO_BANANAS),

    //combat spells
    WEAKEN(Powers.Magic.WEAKEN, Magic.WEAKEN),
    VULNERABILITY(Powers.Magic.VULNERABILITY, Magic.VULNERABILITY),
    ENFEEBLE(Powers.Magic.ENFEEBLE, Magic.ENFEEBLE),
    SNARE(Powers.Magic.SNARE, Magic.SNARE),
    BIND(Powers.Magic.BIND, Magic.BIND),
    CURSE(Powers.Magic.CURSE, Magic.CURSE),
    ENTANGLE(Powers.Magic.ENTANGLE, Magic.ENTANGLE),
    CONFUSE(Powers.Magic.CONFUSE, Magic.CONFUSE),

    //mismatched in api
    AIR_STRIKE(Powers.Magic.AIR_STRIKE, Magic.WIND_STRIKE),
    AIR_BOLT(Powers.Magic.AIR_BOLT, Magic.WIND_BOLT),
    AIR_BLAST(Powers.Magic.AIR_BLAST, Magic.WIND_BLAST),
    AIR_WAVE(Powers.Magic.AIR_WAVE, Magic.WIND_WAVE),
    TELEPORT_HOUSE(Powers.Magic.HOUSE_TELEPORT, Magic.TELEPORT_TO_HOUSE),
    TELEPORT_BLOCK(Powers.Magic.TELEPORT_BLOCK, Magic.TELE_BLOCK),
    TELEPORT_HOME(Powers.Magic.HOME_TELEPORT, Magic.LUMBRIDGE_HOME_TELEPORT),
    TELEPORT_APE_ATOLL(Powers.Magic.APE_ATOLL_TELEPORT, Magic.TELEPORT_TO_APE_ATOLL),
    TELE_OTHER_CAMELOT(Powers.Magic.TELE_OTHER_CAMELOT, Magic.TELEOTHER_CAMELOT),
    TELE_OTHER_FALADOR(Powers.Magic.TELE_OTHER_FALADOR, Magic.TELEOTHER_FALADOR),
    TELE_OTHER_LUMBRIDGE(Powers.Magic.TELE_OTHER_LUMBRIDGE, Magic.TELEOTHER_LUMBRIDGE),

    //osrs only
    MAGIC_DART(null, Magic.MAGIC_DART),
    CLAWS_OF_GUTHIX(null, Magic.CLAWS_OF_GUTHIX),
    IBAN_BLAST(null, Magic.IBAN_BLAST),
    SARADOMIN_STRIKE(null, Magic.SARADOMIN_STRIKE),
    CRUMBLE_UNDEAD(null, Magic.CRUMBLE_UNDEAD),
    FLAMES_OF_ZAMORAK(null, Magic.FLAMES_OF_ZAMORAK),
    STUN(null, Magic.STUN),
    CHARGE(null, Magic.CHARGE),

    //rs3 only
    AIR_SURGE(Powers.Magic.AIR_SURGE, null),
    WATER_SURGE(Powers.Magic.WATER_SURGE, null),
    EARTH_SURGE(Powers.Magic.EARTH_SURGE, null),
    FIRE_SURGE(Powers.Magic.FIRE_SURGE, null),
    TELEPORT_MOBILISING_ARMIES(Powers.Magic.MOBILISING_ARMIES_TELEPORT, null),
    TELEPORT_GOD_WARS_DUNGEON(Powers.Magic.GOD_WARS_DUNGEON_TELEPORT, null),
    SLAYER_DART(Powers.Magic.SLAYER_DART, null),
    POLYPORE_STRIKE(Powers.Magic.POLYPORE_STRIKE, null),
    DIVINE_STORM(Powers.Magic.DIVINE_STORM, null),
    STAGGER(Powers.Magic.STAGGER, null),
    STORM_OF_ARMADYL(Powers.Magic.STORM_OF_ARMADYL, null),
    ;

    static Function<Spell, Boolean> inputter = Spell::activate;

    private final Powers.Magic rs3Spell;
    private final Magic osrsSpell;

    private HMagic(Powers.Magic rs3Spell, Magic osrsSpell) {
        this.rs3Spell = rs3Spell;
        this.osrsSpell = osrsSpell;
    }

    public Powers.Magic getRs3Spell() {
        return this.rs3Spell;
    }

    public Magic getOsrsSpell() {
        return this.osrsSpell;
    }

    public static void setInputSupplier(Function<Spell, Boolean> inputter) {
        HMagic.inputter = inputter;
    }

    //keep the api feelin' going
    public static enum Lunar implements SimpleSpell {
        ;

        private final Powers.Magic rs3Spell;
        private final Magic osrsSpell;

        private Lunar(Powers.Magic rs3Spell, Magic osrsSpell) {
            this.rs3Spell = rs3Spell;
            this.osrsSpell = osrsSpell;
        }

        @Override
        public Powers.Magic getRs3Spell() {
            return this.rs3Spell;
        }

        @Override
        public Magic getOsrsSpell() {
            return this.osrsSpell;
        }
    }

    public static enum Ancient implements SimpleSpell {
        ;

        private final Powers.Magic rs3Spell;
        private final Magic osrsSpell;

        private Ancient(Powers.Magic rs3Spell, Magic osrsSpell) {
            this.rs3Spell = rs3Spell;
            this.osrsSpell = osrsSpell;
        }

        @Override
        public Powers.Magic getRs3Spell() {
            return this.rs3Spell;
        }

        @Override
        public Magic getOsrsSpell() {
            return this.osrsSpell;
        }
    }

    public static enum Arceuus implements SimpleSpell {
        ;
        private final Magic osrsSpell;

        private Arceuus(Magic osrsSpell) {
            this.osrsSpell = osrsSpell;
        }

        @Override
        public Powers.Magic getRs3Spell() {
            return null;
        }

        @Override
        public Magic getOsrsSpell() {
            return this.osrsSpell;
        }
    }

    public static enum Dungeoneering implements SimpleSpell {
        ;
        private final Powers.Magic rs3Spell;

        private Dungeoneering(Powers.Magic rs3Spell) {
            this.rs3Spell = rs3Spell;
        }

        @Override
        public Powers.Magic getRs3Spell() {
            return this.rs3Spell;
        }

        @Override
        public Magic getOsrsSpell() {
            return null;
        }
    }
}
