package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UITag;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.map.SettingTeamMode.TeamMode;
import com.mygdx.hadal.map.modifiers.*;
import com.mygdx.hadal.save.InfoItem;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.utils.TiledObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH;

/**
 * A Game Mode entails a set of rules/settings that dictates a match
 * @author Nirabeau Neebone
 */
public enum GameMode {

    HUB("", new SettingTeamMode(TeamMode.COOP), new SettingLives(0), new SettingBots(0)) {

        @Override
        public boolean isInvisibleInHub() { return true; }

        @Override
        public boolean isHub() { return true; }
    },

    CAMPAIGN("", new SettingTeamMode(TeamMode.COOP), new SettingLives(1)) {

        @Override
        public boolean isInvisibleInHub() { return true; }
    },

    BOSS("", new SettingTeamMode(TeamMode.COOP), new SettingLives(1)),

    DEATHMATCH("dm",
        new SetCameraOnSpawn(),
        new SettingTeamMode(), new SettingTimer(ResultsState.magicWord), new SettingBots(), new SettingLives(),
        new SettingScoreCap(), new SettingBaseHp(), new SettingRespawnTime(), new SettingDroppableWeapons(),
        new DisplayUITag("SCOREBOARD"), new SpawnWeapons(), new ToggleKillsScore(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
            new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    SURVIVAL("arena",
        new SetCameraOnSpawn(),
        new SettingTeamMode(TeamMode.COOP), new SettingTimer("VICTORY"), new SettingLives(1),
        new DisplayUITag("SCORE"), new DisplayUITag("HISCORE"),
        new SpawnWeapons(), new SpawnEnemyWaves()),

    CTF("ctf",
        new SetCameraOnSpawn(),
        new SettingTeamMode(TeamMode.TEAM_AUTO), new SettingTimer(ResultsState.magicWord), new SettingTeamScoreCap(), new SettingLives(0),
        new SettingBaseHp(), new SettingRespawnTime(), new SettingDroppableWeapons(),
        new DisplayUITag("TEAMSCORE"), new SpawnWeapons(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
            new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    FOOTBALL("",
        new SetCameraOnSpawn(),
        new SettingTeamMode(TeamMode.TEAM_AUTO), new SettingTimer(ResultsState.magicWord), new SettingTeamScoreCap(), new SettingLives(0),
        new DisplayUITag("TEAMSCORE"), new ToggleNoDamage(),
        new SetLoadoutEquips(UnlockEquip.BATTERING_RAM, UnlockEquip.SCRAPRIPPER, UnlockEquip.DUELING_CORKGUN),
        new SetLoadoutArtifacts(UnlockArtifact.INFINITE_AMMO)),

    GUN_GAME("dm", DEATHMATCH,
        new SetCameraOnSpawn(),
        new SettingTeamMode(TeamMode.FFA), new SettingTimer(ResultsState.magicWord), new SettingBots(),
        new SettingLives(0), new SettingBaseHp(), new SettingRespawnTime(),
        new DisplayUITag("GUNGAME"),
        new SetLoadoutEquips(UnlockEquip.NOTHING, UnlockEquip.NOTHING, UnlockEquip.NOTHING),
        new SetLoadoutArtifacts(UnlockArtifact.INFINITE_AMMO), new SetLoadoutActive(UnlockActives.NOTHING),
        new ModeGunGame(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
            new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion())),

    EGGPLANTS("objective,dm", DEATHMATCH,
        new SetCameraOnSpawn(),
        new SettingTeamMode(), new SettingTimer(ResultsState.magicWord), new SettingLives(0),
        new SettingBaseHp(), new SettingRespawnTime(), new SettingDroppableWeapons(),
        new DisplayUITag("SCOREBOARD"), new SpawnWeapons(), new ToggleEggplantDrops(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
            new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    KINGMAKER("objective,dm", DEATHMATCH,
        new SetCameraOnSpawn(),
        new SettingTeamMode(), new SettingTimer(ResultsState.magicWord), new SettingLives(0),
        new SettingBaseHp(), new SettingRespawnTime(), new SettingDroppableWeapons(),
        new DisplayUITag("SCOREBOARD"), new SpawnWeapons(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
            new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    MATRYOSHKA("dm", DEATHMATCH,
        new SetCameraOnSpawn(),
        new SettingTeamMode(TeamMode.FFA), new SettingTimer(ResultsState.magicWord),
        new SettingBaseHp(), new SettingRespawnTime(), new SettingBots(), new SettingDroppableWeapons(),
        new DisplayUITag("LIVES"), new SpawnWeapons(),
        new ModeMatryoshka(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(),
            new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    SANDBOX("", new SettingTeamMode(TeamMode.COOP), new SettingLives(0)),

    ;

    //this is a list of event layers that this mode will parse when loaded. Used for maps with multiple valid modes
    private final String[] extraLayers;

    //settings that apply to this mode
    private final ModeSetting[] applicableSettings;

    private InfoItem info;

    //this contains the strings that will be displayed in the notification window at the start of the game
    //atm, this is just used to notify of mode modifiers
    private final List<String> initialNotifications = new ArrayList<>();

    //this is a game mode which has the same set of compliant maps.
    // Used for modes that have the same set of compliant maps (gun game etc with deathmatch)
    private GameMode checkCompliance = this;
    private TeamMode teamMode = TeamMode.FFA;

    GameMode(String extraLayers, GameMode checkCompliance, ModeSetting... applicableSettings) {
        this(extraLayers, applicableSettings);
        this.checkCompliance = checkCompliance;
    }

    GameMode(String extraLayers, ModeSetting... applicableSettings) {
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
        StringBuilder uiTriggerId = new StringBuilder(HText.LEVEL.text());
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

            //this creates a comma-separated list of event ids that will be activated upon spawining/starting the game
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

    /**
     * This is run when a player is created. This is used to change properties of the player prior to player init
     * @param newLoadout: the new loadout the player will spawn with. modify to change starting loadout
     * @param connID: connID of the player being created. We use connID b/c the player isn't created yet
     */
    public void processNewPlayerLoadout(PlayState state, Loadout newLoadout, int connID) {
        if (!state.isServer()) { return; }
        for (ModeSetting setting: applicableSettings) {
            setting.processNewPlayerLoadout(state, this, newLoadout, connID);
        }
    }

    /**
     * This is run after a player is created. Used to change player properties that can be set after player init
     * @param newLoadout: loadout of the new player. used for changing things like player alignment
     * @param p: new player being created
     * @param hitboxFilter: the hbox filter alignment. Used for setting team mode options
     */
    public void modifyNewPlayer(PlayState state, Loadout newLoadout, Player p, short hitboxFilter) {
        if (!state.isServer()) { return; }
        for (ModeSetting setting: applicableSettings) {
            setting.modifyNewPlayer(state, this, newLoadout, p, hitboxFilter);
        }
    }

    /**
     * This is run when a player dies. Change score values and do mode-specific death processing
     * @param perp: the schmuck (not necessarily player) that killed
     * @param vic: the player that died
     */
    public void processPlayerDeath(PlayState state, Schmuck perp, Player vic, DamageTypes... tags) {
        if (!state.isServer()) { return; }
        if (vic != null) {
            User user = HadalGame.server.getUsers().get(vic.getConnId());
            if (user != null) {
                user.getScores().setDeaths(user.getScores().getDeaths() + 1);
                user.setScoreUpdated(true);
            }
        }
        if (perp != null) {
            if (perp instanceof Player player) {
                User user = HadalGame.server.getUsers().get(player.getConnId());
                if (user != null) {
                    user.getScores().setKills(user.getScores().getKills() + 1);
                    user.setScoreUpdated(true);
                }
            }
        }
        for (ModeSetting setting : applicableSettings) {
            setting.processPlayerDeath(state, this, perp, vic, tags);
        }
    }

    /**
     * This is run when a player's score changes. Used for modes where an effect should activate upon score changing
     * @param p: the player whose score is changing
     * @param scoreIncrement: the amount to change the score by
     */
    public void processPlayerScoreChange(PlayState state, Player p, int scoreIncrement) {
        if (!state.isServer()) { return; }
        if (p != null) {
            User user = HadalGame.server.getUsers().get(p.getConnId());
            if (user != null) {
                user.getScores().setScore(user.getScores().getScore() + scoreIncrement);

                for (ModeSetting setting : applicableSettings) {
                    setting.processPlayerScoreChange(state, this, p, user.getScores().getScore());
                }

                //tell score window and ui extrato update next interval
                user.setScoreUpdated(true);
                state.getUiExtra().syncUIText(UITag.uiType.SCORE);
            }
        }
    }

    /**
     * This is run when a team's score changes. Used for modes where an effect should activate upon score changing
     * @param teamIndex: the index of the team we are changing the score of
     * @param scoreIncrement: The amount to change the score by
     */
    public void processTeamScoreChange(PlayState state, int teamIndex, int scoreIncrement) {
        if (teamIndex < AlignmentFilter.teamScores.length && teamIndex >= 0) {
            int newScore = AlignmentFilter.teamScores[teamIndex] + scoreIncrement;
            AlignmentFilter.teamScores[teamIndex] = newScore;

            for (ModeSetting setting : applicableSettings) {
                setting.processTeamScoreChange(state, this, teamIndex, newScore);
            }

            //tell ui extra to sync updated score
            state.getUiExtra().syncUIText(UITag.uiType.TEAMSCORE);
        }
    }

    /**
     * This is run when a player runs out of lives
     * Atm, this processes things like end game conditions depending on team affiliation
     * @param p: The player whose lives ran out
     */
    public void processPlayerLivesOut(PlayState state, Player p) {
        for (ModeSetting setting : applicableSettings) {
            setting.processPlayerLivesOut(state, this, p);
        }
        state.getKillFeed().addNotification(HText.ELIMINATED.text(WeaponUtils.getPlayerColorName(p, MAX_NAME_LENGTH)), true);
    }

    private static final HashMap<String, GameMode> ModesByName = new HashMap<>();
    static {
        for (GameMode m: GameMode.values()) {
            ModesByName.put(m.toString(), m);
        }
    }

    public InfoItem getInfo() { return info; }

    public void setInfo(InfoItem info) { this.info = info; }

    public static GameMode getByName(String s) {
        return ModesByName.getOrDefault(s, HUB);
    }

    public String[] getExtraLayers() { return extraLayers; }

    public GameMode getCheckCompliance() { return checkCompliance; }

    public ModeSetting[] getSettings() { return applicableSettings; }

    public boolean isInvisibleInHub() { return false; }

    public boolean isHub() { return false; }

    public List<String> getInitialNotifications() { return initialNotifications; }

    public TeamMode getTeamMode() { return teamMode; }

    public void setTeamMode(TeamMode teamMode) { this.teamMode = teamMode; }
}
