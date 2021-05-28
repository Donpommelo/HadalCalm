package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

public enum GameMode {

    HUB(false, true, true, false, false, -1, ""),
    SANDBOX(true, false, true, false, false, -1, ""),
    DEATHMATCH(true, false, false, false, true, -1, "dm"),
    CAMPAIGN(false, false, false, false, true, -1, ""),
    SURVIVAL(false, false, false, false, true, -1, "arena"),

    CTF(true, false, false, false, false, 1, "ctf"),
    FOOTBALL(true, false, false, true, false, 1, ""),

    ;

    //this is a list of event layers that this map will parse when loaded. Used for maps with multiple valid modes
    private final String[] extraLayers;

    private final ModeSetting[] applicableSettings;

    //the settings of the map are a field of the unlock so the same map can have multiple modes
    private final boolean pvp, hub, unlimitedLives, killScore, noDamage;

    //is there a default team mode for this map? (-1 means it goes with the server settings)
    private final int teamType;

    GameMode(boolean pvp, boolean hub, boolean unlimitedLives, boolean noDamage, boolean killScore, int teamType,
                String extraLayers, ModeSetting... applicableSettings) {
        this.pvp = pvp;
        this.hub = hub;
        this.unlimitedLives = unlimitedLives;
        this.noDamage = noDamage;
        this.killScore = killScore;
        this.teamType = teamType;
        this.extraLayers = extraLayers.split(",");
        this.applicableSettings = applicableSettings;
    }

    private static final String playerStartId = "playerstart";
    public void processSettings(PlayState state) {

        if (applicableSettings.length == 0) { return; }

        String timerId = TiledObjectUtil.getPrefabTriggerId();
        String multiId = TiledObjectUtil.getPrefabTriggerId();

        RectangleMapObject playerstart = new RectangleMapObject();
        playerstart.setName("Multitrigger");
        playerstart.getProperties().put("triggeredId", playerStartId);

        RectangleMapObject timer = new RectangleMapObject();
        timer.setName("Timer");
        timer.getProperties().put("interval", 0.0f);
        timer.getProperties().put("triggeredId", timerId);
        timer.getProperties().put("triggeringId", multiId);

        RectangleMapObject multi = new RectangleMapObject();
        multi.setName("Multitrigger");
        multi.getProperties().put("triggeredId", multiId);

        StringBuilder spawnTriggerId = new StringBuilder();
        StringBuilder startTriggerId = new StringBuilder("timerId");

        for (ModeSetting setting: applicableSettings) {
            spawnTriggerId.append(setting.loadSettingSpawn(state));
            startTriggerId.append(setting.loadSettingStart(state));
            setting.loadSettingMisc(state);
        }

        playerstart.getProperties().put("triggeringId", spawnTriggerId.toString());
        multi.getProperties().put("triggeringId", startTriggerId.toString());

        TiledObjectUtil.parseTiledEvent(state, playerstart);
        TiledObjectUtil.parseTiledEvent(state, timer);
        TiledObjectUtil.parseTiledEvent(state, multi);
    }

    public String[] getExtraLayers() { return extraLayers; }

    public ModeSetting[] getSettings() { return applicableSettings; }

    public boolean isPvp() { return pvp; }

    public boolean isHub() { return hub; }

    public boolean isUnlimitedLives() { return unlimitedLives; }

    public boolean isKillScore() { return killScore; }

    public boolean isNoDamage() { return noDamage; }

    public int getTeamType() { return teamType; }
}
