package com.mygdx.hadal.map;

import com.badlogic.gdx.utils.ObjectMap;

public enum ArcadeMode {

    DEATHMATCH(GameMode.DEATHMATCH),
    EGGPLANTS(GameMode.EGGPLANTS),
    GUN_GAME(GameMode.GUN_GAME),
    KINGMAKER(GameMode.KINGMAKER),
    MATRYOSHKA(GameMode.MATRYOSHKA),
    RESURRECTION(GameMode.RESURRECTION),

    ;

    private final GameMode mode;

    ArcadeMode(GameMode mode) {
        this.mode = mode;
    }

    public GameMode getMode() { return mode; }

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
