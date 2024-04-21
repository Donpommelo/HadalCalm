package com.mygdx.hadal.map;

import com.badlogic.gdx.utils.ObjectMap;

public enum SettingSave {

    BASE_HP(2),
    BOT_NUMBER(),
    BOT_DIFFICUlTY(),
    LIVES(),
    WEAPON_DROPS(),
    LOADOUT_OUTFIT(),
    RESPAWN_TIME(4),
    SCORE_CAP(),
    TEAM_MODE(),
    TEAM_NUMBER(),
    TEAM_SCORE_CAP(),
    TIMER(3),

    MODIFIER_DOUBLE_SPEED(),
    MODIFIER_MEDIEVAL(),
    MODIFIER_BOUNCE(),
    MODIFIER_BIG(),
    MODIFIER_INVISIBLE(),
    MODIFIER_SLIPPERY(),
    MODIFIER_SLOW(),
    MODIFIER_SMALL(),
    MODIFIER_VISIBLE_HP(),
    MODIFIER_ZERO_GRAVITY(),

    ARCADE_BREAK_TIME(2),
    ARCADE_ROUND_NUMBER(),
    ARCADE_SCORE_CAP(1),
    ARCADE_CURRENCY_START(2),
    ARCADE_CURRENCY_ROUND(3),

    ;

    private final int startingValue;


    SettingSave() {
        this(0);
    }

    SettingSave(int startingValue) { this.startingValue = startingValue; }


    public int getStartingValue() { return startingValue; }

    private static final ObjectMap<String, SettingSave> SettingsByName = new ObjectMap<>();
    static {
        for (SettingSave s : SettingSave.values()) {
            SettingsByName.put(s.toString(), s);
        }
    }
    public static SettingSave getByName(String s) {
        return SettingsByName.get(s, BASE_HP);
    }
}
