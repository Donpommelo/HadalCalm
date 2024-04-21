package com.mygdx.hadal.map;

import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.text.GameText;

public enum ArcadeMode {

    DEATHMATCH(GameMode.DEATHMATCH),
    DEATHMATCH_TEAM(GameMode.DEATHMATCH, GameText.DEATHMATCH_TEAM2.text()),
    DEATHMATCH_ELIMINATION(GameMode.DEATHMATCH, GameText.DEATHMATCH_ELIMINATION.text()),

    EGGPLANTS(GameMode.EGGPLANTS),
    GUN_GAME(GameMode.GUN_GAME),
    KINGMAKER(GameMode.KINGMAKER),
    MATRYOSHKA(GameMode.MATRYOSHKA),
    RESURRECTION(GameMode.RESURRECTION),
    CTF(GameMode.CTF),
    TRICK_OR_TREAT(GameMode.TRICK_OR_TREAT),

    ;

    private final GameMode mode;
    private final String name;

    ArcadeMode(GameMode mode, String name) {
        this.mode = mode;
        this.name = name;
    }

    ArcadeMode(GameMode mode) {
        this(mode, mode.getName());
    }

    public GameMode getMode() { return mode; }

    public String getName() { return name; }

    public ObjectMap<String, Integer> getUniqueSettings() {
        ObjectMap<String, Integer> uniqueSettings = new ObjectMap<>();
        switch (this) {
            case DEATHMATCH_TEAM -> uniqueSettings.put(SettingSave.TEAM_MODE.name(), 1);
            case DEATHMATCH_ELIMINATION -> uniqueSettings.put(SettingSave.LIVES.name(), 5);
        }
        return uniqueSettings;
    }

    private static final ObjectMap<String, ArcadeMode> ModesByName = new ObjectMap<>();
    static {
        for (ArcadeMode m : ArcadeMode.values()) {
            ModesByName.put(m.toString(), m);
        }
    }
    public static ArcadeMode getByName(String s) {
        return ModesByName.get(s, DEATHMATCH);
    }
}
