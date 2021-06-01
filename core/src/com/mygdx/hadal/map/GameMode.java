package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.utils.TiledObjectUtil;

public enum GameMode {

    HUB("HUB", "", new ToggleHub(), new ToggleUnlimitedLife()) {

        @Override
        public boolean isInvisibleInHub() { return true; }
    },

    DEATHMATCH("DM", "dm",
        new SetCameraOnSpawn(), new DisplayUITag("SCORE"), new SetLivesOnStart(), new SetTimerOnStart(ResultsState.magicWord),
        new SpawnWeapons(), new ToggleKillsScore(), new TogglePVP()),

    CAMPAIGN("","") {

        @Override
        public boolean isInvisibleInHub() { return true; }
    },

    SURVIVAL("ARENA", "arena",
        new SetCameraOnSpawn(), new DisplayUITag("SCORE"), new DisplayUITag("HISCORE"), new SetTimerOnStart("VICTORY"),
        new SpawnWeapons(), new SpawnEnemyWaves()),

    CTF("CTF", "ctf", new SetCameraOnSpawn(), new SetTimerOnStart(ResultsState.magicWord), new DisplayUITag("TEAMSCORE"),
        new SpawnWeapons(), new TogglePVP(), new ToggleTeamMode(1), new ToggleUnlimitedLife()),

    FOOTBALL("FOOTBALL","", new SetCameraOnSpawn(), new SetTimerOnStart(ResultsState.magicWord), new DisplayUITag("TEAMSCORE"),
        new ToggleNoDamage(), new TogglePVP(), new ToggleTeamMode(1), new ToggleUnlimitedLife(),
        new SetLoadoutEquips(UnlockEquip.BATTERING_RAM, UnlockEquip.SCRAPRIPPER, UnlockEquip.DUELING_CORKGUN),
        new SetLoadoutArtifacts(UnlockArtifact.INFINITE_AMMO)),

    GUN_GAME("GUN GAME", "", new SetCameraOnSpawn(), new SetTimerOnStart(ResultsState.magicWord), new DisplayUITag("GUNGAME"),
        new TogglePVP(), new ToggleTeamMode(0), new ToggleUnlimitedLife(),
        new SetLoadoutEquips(UnlockEquip.NOTHING, UnlockEquip.NOTHING, UnlockEquip.NOTHING),
        new SetLoadoutArtifacts(UnlockArtifact.GUN_GAME, UnlockArtifact.INFINITE_AMMO), new SetLoadoutActive(UnlockActives.NOTHING)),

    SANDBOX("", ""),

    ;

    //this is a list of event layers that this map will parse when loaded. Used for maps with multiple valid modes
    private final String[] extraLayers;

    private final ModeSetting[] applicableSettings;

    private final String text;

    GameMode(String text, String extraLayers, ModeSetting... applicableSettings) {
        this.text = text;
        this.extraLayers = extraLayers.split(",");
        this.applicableSettings = applicableSettings;
    }

    private static final String playerStartId = "playerstart";
    public void processSettings(PlayState state) {

        if (applicableSettings.length == 0) { return; }

        String timerId = TiledObjectUtil.getPrefabTriggerId();
        String multiId = TiledObjectUtil.getPrefabTriggerId();
        String uiId = TiledObjectUtil.getPrefabTriggerId();

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

        RectangleMapObject ui = new RectangleMapObject();
        ui.setName("UI");
        ui.getProperties().put("triggeredId", uiId);

        StringBuilder uiTriggerId = new StringBuilder("LEVEL");
        StringBuilder spawnTriggerId = new StringBuilder();
        StringBuilder startTriggerId = new StringBuilder(timerId + "," + uiId);

        for (ModeSetting setting: applicableSettings) {
            String newSpawn = setting.loadSettingSpawn(state);
            String newStart = setting.loadSettingStart(state);
            String newUi = setting.loadUIStart(state);
            if (!newSpawn.equals("")) {
                spawnTriggerId.append(',').append(newSpawn);
            }
            if (!newStart.equals("")) {
                startTriggerId.append(',').append(newStart);
            }
            if (!newUi.equals("")) {
                uiTriggerId.append(',').append(newUi);
            }
            setting.loadSettingMisc(state);
        }

        ui.getProperties().put("tags", uiTriggerId.toString());
        playerstart.getProperties().put("triggeringId", spawnTriggerId.toString());
        multi.getProperties().put("triggeringId", startTriggerId.toString());

        TiledObjectUtil.parseTiledEvent(state, playerstart);
        TiledObjectUtil.parseTiledEvent(state, timer);
        TiledObjectUtil.parseTiledEvent(state, multi);
        TiledObjectUtil.parseTiledEvent(state, ui);
    }

    public String getText() { return text;}

    public String[] getExtraLayers() { return extraLayers; }

    public ModeSetting[] getSettings() { return applicableSettings; }

    public boolean isInvisibleInHub() { return false; }
}
