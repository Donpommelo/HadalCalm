package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.map.modifiers.*;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.utils.TiledObjectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A Game Mode entails a set of rules/settings that dictates a match
 * @author Nirabeau Neebone
 */
public enum GameMode {

    HUB("HUB", "", new ToggleHub(), new ToggleUnlimitedLife()) {

        @Override
        public boolean isInvisibleInHub() { return true; }
    },

    CAMPAIGN("","") {

        @Override
        public boolean isInvisibleInHub() { return true; }
    },

    BOSS("BOSS",""),

    DEATHMATCH("DM", "dm",
        new SetCameraOnSpawn(), new SettingScoreCap(), new SettingTimer(ResultsState.magicWord), new DisplayUITag("SCOREBOARD"),
        new SettingLives(), new SettingTeamMode(), new SettingBaseHp(), new SettingDroppableWeapons(),
        new SpawnWeapons(), new ToggleKillsScore(), new TogglePVP(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(),
            new PlayerMini(), new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion())),

    SURVIVAL("ARENA", "arena",
        new SetCameraOnSpawn(), new SettingTimer("VICTORY"),
        new DisplayUITag("SCORE"), new DisplayUITag("HISCORE"),
        new SpawnWeapons(), new SpawnEnemyWaves()),

    CTF("CTF", "ctf",
        new SetCameraOnSpawn(), new SettingTeamScoreCap(), new SettingTimer(ResultsState.magicWord),
        new DisplayUITag("TEAMSCORE"), new SettingDroppableWeapons(), new SettingBaseHp(), new SpawnWeapons(),
        new TogglePVP(), new ToggleTeamMode(1), new ToggleUnlimitedLife(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(),
            new PlayerMini(), new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion())),

    FOOTBALL("FOOTBALL","",
        new SetCameraOnSpawn(), new SettingTeamScoreCap(), new SettingTimer(ResultsState.magicWord),
        new DisplayUITag("TEAMSCORE"),
        new ToggleNoDamage(), new TogglePVP(), new ToggleTeamMode(1), new ToggleUnlimitedLife(),
        new SetLoadoutEquips(UnlockEquip.BATTERING_RAM, UnlockEquip.SCRAPRIPPER, UnlockEquip.DUELING_CORKGUN),
        new SetLoadoutArtifacts(UnlockArtifact.INFINITE_AMMO)),

    GUN_GAME("GUN GAME", "", DEATHMATCH,
        new SetCameraOnSpawn(), new SettingTimer(ResultsState.magicWord),
        new DisplayUITag("GUNGAME"), new SettingBaseHp(),
        new TogglePVP(), new ToggleTeamMode(0), new ToggleUnlimitedLife(),
        new SetLoadoutEquips(UnlockEquip.NOTHING, UnlockEquip.NOTHING, UnlockEquip.NOTHING),
        new SetLoadoutArtifacts(UnlockArtifact.GUN_GAME, UnlockArtifact.INFINITE_AMMO), new SetLoadoutActive(UnlockActives.NOTHING),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(),
            new PlayerMini(), new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion())),

    SANDBOX("", ""),

    ;

    //this is a list of event layers that this mode will parse when loaded. Used for maps with multiple valid modes
    private final String[] extraLayers;

    //settings that apply to this mode
    private final ModeSetting[] applicableSettings;

    //this text is displayed in the ui when this mode is selected
    private final String text;

    private final List<String> initialNotifications = new ArrayList<>();

    //this is a game mode which has the same set of compliant maps.
    // Used for modes that have the same set of compliant maps (gun game etc with deathmatch)
    private GameMode checkCompliance = this;

    GameMode(String text, String extraLayers, GameMode checkCompliance, ModeSetting... applicableSettings) {
        this(text, extraLayers, applicableSettings);
        this.checkCompliance = checkCompliance;
    }

    GameMode(String text, String extraLayers, ModeSetting... applicableSettings) {
        this.text = text;
        this.extraLayers = extraLayers.split(",");
        this.applicableSettings = applicableSettings;
    }

    private static final String playerStartId = "playerstart";
    public void processSettings(PlayState state) {

        initialNotifications.clear();

        //for maps with no applicable settings, we don't need to add the extra events to the map (campaign maps)
        if (applicableSettings.length == 0) { return; }

        String timerId = TiledObjectUtil.getPrefabTriggerId();
        String multiId = TiledObjectUtil.getPrefabTriggerId();
        String uiId = TiledObjectUtil.getPrefabTriggerId();

        //this creates a trigger that will be activated when a player spawns
        RectangleMapObject playerstart = new RectangleMapObject();
        playerstart.setName("Multitrigger");
        playerstart.getProperties().put("triggeredId", playerStartId);

        //this creates a timer that will activate once at the start of the match
        RectangleMapObject timer = new RectangleMapObject();
        timer.setName("Timer");
        timer.getProperties().put("interval", 0.0f);
        timer.getProperties().put("triggeredId", timerId);
        timer.getProperties().put("triggeringId", multiId);

        RectangleMapObject multi = new RectangleMapObject();
        multi.setName("Multitrigger");
        multi.getProperties().put("triggeredId", multiId);

        //this modifies the ui at the start of the match
        RectangleMapObject ui = new RectangleMapObject();
        ui.setName("UI");
        ui.getProperties().put("triggeredId", uiId);

        //using these string builders, we modify the aforementioned events based on the mode's settings
        StringBuilder uiTriggerId = new StringBuilder("LEVEL");
        StringBuilder spawnTriggerId = new StringBuilder();
        StringBuilder startTriggerId = new StringBuilder(timerId + "," + uiId);

        for (ModeSetting setting: applicableSettings) {

            //only the server saves their chosen settings.
            if (state.isServer()) {
                setting.saveSetting(state, this);

                //atm, the client does not need to run this b/c it creates events only the server needs to process.
                String newStart = setting.loadSettingStart(state, this);
                if (!newStart.equals("")) {
                    startTriggerId.append(',').append(newStart);
                }
            }

            String newSpawn = setting.loadSettingSpawn(state, this);
            String newUi = setting.loadUIStart(state, this);
            if (!newSpawn.equals("")) {
                spawnTriggerId.append(',').append(newSpawn);
            }

            if (!newUi.equals("")) {
                uiTriggerId.append(',').append(newUi);
            }
            setting.loadSettingMisc(state, this);
        }

        state.getGsm().getSetting().saveSetting();

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

    public GameMode getCheckCompliance() { return checkCompliance; }

    public ModeSetting[] getSettings() { return applicableSettings; }

    public boolean isInvisibleInHub() { return false; }

    public List<String> getInitialNotifications() { return initialNotifications; }
}
