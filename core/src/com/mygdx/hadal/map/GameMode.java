package com.mygdx.hadal.map;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.bots.BotPersonality.BotDifficulty;
import com.mygdx.hadal.bots.RallyPoint;
import com.mygdx.hadal.constants.UITagType;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.map.SettingLoadoutMode.LoadoutMode;
import com.mygdx.hadal.map.SettingTeamMode.TeamMode;
import com.mygdx.hadal.map.modifiers.*;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.text.GameText;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.TextUtil;
import com.mygdx.hadal.utils.TiledObjectUtil;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;

/**
 * A Game Mode entails a set of rules/settings that dictates a match
 * @author Nirabeau Neebone
 */
public enum GameMode {

    HUB("", "placeholder", GameText.HUB, GameText.HUB_DESC,
            new SettingTeamMode(TeamMode.COOP), new SettingLives(0), new SettingBots(1),
            new SettingHub()) {

        @Override
        public boolean isInvisibleInHub() { return true; }

        @Override
        public boolean isHub() { return true; }
    },

    CAMPAIGN("", "placeholder", GameText.CAMPAIGN, GameText.CAMPAIGN_DESC,
            new SettingTeamMode(TeamMode.COOP), new SettingLives(1), new SettingBots(1)) {

        @Override
        public boolean isInvisibleInHub() { return true; }
    },

    BOSS("", "placeholder", GameText.BOSS, GameText.BOSS_DESC,
            new SettingTeamMode(TeamMode.COOP), new SettingBots(1), new AllyRevive(),
            new DisplayUITag("ALLY_HEALTH"),
            new ToggleWeaponDrops()),

    DEATHMATCH("dm", "deathmatch", GameText.DEATHMATCH, GameText.DEATHMATCH_DESC,
        new SetCameraOnSpawn(),
        new SettingTeamMode(), new SettingTimer(ResultsState.MAGIC_WORD), new SettingBots(), new SettingLives(),
        new SettingScoreCap(), new SettingBaseHp(), new SettingRespawnTime(), new SettingLoadoutOutfit(), new SettingLoadoutMode(),
        new DisplayUITag("SCOREBOARD"), new SpawnWeapons(), new ToggleKillsScore(), new ToggleWeaponDrops(), new ToggleHealthDrops(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
            new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    ARCADE("", "placeholder", GameText.ARCADE, GameText.ARCADE_DESC,
            new SettingTeamMode(TeamMode.COOP), new SettingLives(0), new SettingArcade(),
            new SettingBots(),
            new DisplayUITag("ARCADE_ROUND"), new DisplayUITag("WINBOARD"), new DisplayUITag("CURRENCY")) {

        @Override
        public boolean isArcadeBreakroom() { return true; }
    },

    SURVIVAL("arena", "survival", GameText.SURVIVAL, GameText.SURVIVAL_DESC,
        new SetCameraOnSpawn(),
        new SettingTeamMode(TeamMode.COOP), new SettingTimer("VICTORY"), new SettingBots(1), new AllyRevive(),
        new DisplayUITag("SCOREBOARD"), new DisplayUITag("HISCORE"), new DisplayUITag("ALLY_HEALTH"),
        new SpawnWeapons(), new SpawnEnemyWaves(), new ToggleWeaponDrops()),

    CTF("team,ctf", "ctf", GameText.CTF, GameText.CTF_DESC,
        new SetCameraOnSpawn(),
        new SettingTeamMode(TeamMode.TEAM_AUTO), new SettingTimer(ResultsState.MAGIC_WORD), new SettingBots(),
        new SettingTeamScoreCap(), new SettingLives(0), new SettingBaseHp(), new SettingRespawnTime(5), new SettingLoadoutOutfit(),
        new SettingLoadoutMode(),
        new DisplayUITag("TEAMSCORE"), new SpawnWeapons(), new ToggleWeaponDrops(), new ToggleHealthDrops(),
        new ModeCapturetheFlag(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
        new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    TRICK_OR_TREAT("team,tot,objective", "trickortreat_v2", CTF, GameText.TRICK_OR_TREAT, GameText.TRICK_OR_TREAT_DESC,
            new SetCameraOnSpawn(),
            new SettingTeamMode(TeamMode.TEAM_AUTO), new SettingTimer(ResultsState.MAGIC_WORD), new SettingBots(),
            new SettingTeamScoreCap(), new SettingLives(0), new SettingBaseHp(), new SettingRespawnTime(5), new SettingLoadoutOutfit(),
            new SettingLoadoutMode(),
            new DisplayUITag("TEAMSCORE"), new SpawnWeapons(), new ToggleWeaponDrops(), new ToggleHealthDrops(),
            new ModeTrickorTreat(),
            new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
                    new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    EGGPLANTS("objective,dm", "eggplant_hunt", DEATHMATCH, GameText.MODE_EGGPLANT, GameText.MODE_EGGPLANT_DESC,
            new SetCameraOnSpawn(),
            new SettingTeamMode(), new SettingTimer(ResultsState.MAGIC_WORD), new SettingLives(0),
            new SettingBaseHp(), new SettingRespawnTime(), new SettingBots(), new SettingLoadoutOutfit(), new SettingLoadoutMode(),
            new DisplayUITag("SCOREBOARD"), new SpawnWeapons(),  new ToggleWeaponDrops(), new ToggleHealthDrops(),
            new ModeEggplantHunt(),
            new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
                    new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    GUN_GAME("dm", "gun_game", DEATHMATCH, GameText.GUN_GAME, GameText.GUN_GAME_DESC,
        new SetCameraOnSpawn(),
        new SettingTeamMode(TeamMode.FFA), new SettingTimer(ResultsState.MAGIC_WORD, 8), new SettingBots(),
        new SettingLives(0), new SettingBaseHp(), new SettingRespawnTime(),
        new DisplayUITag("GUNGAME"), new ToggleHealthDrops(),
        new SetLoadoutEquips(UnlockEquip.NOTHING, UnlockEquip.NOTHING, UnlockEquip.NOTHING),
        new SetLoadoutArtifacts(UnlockArtifact.INFINITE_AMMO), new SetLoadoutActive(UnlockActives.NOTHING),
        new ModeGunGame(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
            new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion())),

    KINGMAKER("objective,dm", "koh", DEATHMATCH, GameText.KINGMAKER, GameText.KINGMAKER_DESC,
        new SetCameraOnSpawn(),
        new SettingTeamMode(), new SettingTimer(ResultsState.MAGIC_WORD), new SettingLives(0),
        new SettingBaseHp(), new SettingRespawnTime(), new SettingBots(), new SettingLoadoutOutfit(), new SettingLoadoutMode(),
        new DisplayUITag("SCOREBOARD"), new SpawnWeapons(), new ToggleWeaponDrops(), new ToggleHealthDrops(),
        new ModeKingmaker(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
            new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    MATRYOSHKA("dm", "matryoshka", DEATHMATCH, GameText.MATRYOSHKA, GameText.MATRYOSHKA_DESC,
        new SetCameraOnSpawn(),
        new SettingTeamMode(TeamMode.FFA), new SettingTimer(ResultsState.MAGIC_WORD, 8),
        new SettingBaseHp(), new SettingBots(), new SettingLoadoutOutfit(), new SettingLoadoutMode(),
        new DisplayUITag("LIVES"), new SpawnWeapons(), new ToggleWeaponDrops(), new ToggleHealthDrops(),
        new ModeMatryoshka(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(),
            new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    RESURRECTION("dm", "resurrection", DEATHMATCH, GameText.RESURRECTION, GameText.RESURRECTION_DESC,
        new SetCameraOnSpawn(),
        new SettingTeamMode(TeamMode.TEAM_AUTO), new SettingTimer(ResultsState.MAGIC_WORD, 8), new AllyRevive(),
        new SettingBaseHp(), new SettingBots(), new SettingLoadoutOutfit(), new SettingLoadoutMode(),
        new DisplayUITag("PLAYERS_ALIVE"), new DisplayUITag("ALLY_HEALTH"), new SpawnWeapons(), new ToggleWeaponDrops(), new ToggleHealthDrops(),
        new SetModifiers(new VisibleHp(), new PlayerBounce(), new PlayerSlide(), new PlayerMini(), new PlayerGiant(),
            new PlayerInvisible(), new ZeroGravity(), new DoubleSpeed(), new SlowMotion(), new MedievalMode())),

    FOOTBALL("futbol", "football", GameText.FOOTBALL, GameText.FOOTBALL_DESC,
            new SetCameraOnSpawn(),
            new SettingTeamMode(TeamMode.TEAM_AUTO), new SettingTimer(ResultsState.MAGIC_WORD), new SettingBots(1), new SettingTeamScoreCap(), new SettingLives(0),
            new DisplayUITag("TEAMSCORE"), new ToggleNoDamage(),
            new SetLoadoutEquips(UnlockEquip.BATTERING_RAM, UnlockEquip.SCRAPRIPPER, UnlockEquip.DUELING_CORKGUN),
            new SetLoadoutArtifacts(UnlockArtifact.INFINITE_AMMO)),

    SANDBOX("", "placeholder", GameText.SANDBOX, GameText.SANDBOX_DESC, new SettingTeamMode(TeamMode.COOP),
            new SettingLives(0)),

    ;

    //this is a list of event layers that this mode will parse when loaded. Used for maps with multiple valid modes
    private final String[] extraLayers;

    //settings that apply to this mode
    private final ModeSetting[] applicableSettings;

    private final GameText name, desc;

    //this contains the strings that will be displayed in the notification window at the start of the game
    //atm, this is just used to notify of mode modifiers
    private final Array<String> initialNotifications = new Array<>();

    //this is a game mode which has the same set of compliant maps.
    // Used for modes that have the same set of compliant maps (gun game etc with deathmatch)
    private GameMode checkCompliance = this;
    private TeamMode teamMode = TeamMode.FFA;
    private LoadoutMode loadoutMode = LoadoutMode.CLASSIC;
    private BotDifficulty botDifficulty = BotDifficulty.EASY;

    //will players that join mid game join as players or spectators (spectator for lives-based modes)
    private boolean joinMidGame = true;

    private float botScoreAggroModifier;

    //number of teams playing on auto team assign mode
    private int teamNum = 2;

    //starting score of each team (usually 0, but can be higher for certain modes)
    private int teamStartScore = 0;

    //The string id of the mode's icon in the mode texture atlas
    private final String spriteId;

    GameMode(String extraLayers, String spriteId, GameMode checkCompliance, GameText name, GameText desc, ModeSetting... applicableSettings) {
        this(extraLayers, spriteId, name, desc, applicableSettings);
        this.checkCompliance = checkCompliance;
    }

    GameMode(String extraLayers, String spriteId, GameText name, GameText desc, ModeSetting... applicableSettings) {
        this.extraLayers = extraLayers.split(",");
        this.spriteId = spriteId;
        this.name = name;
        this.desc = desc;
        this.applicableSettings = applicableSettings;
    }

    //id for events run when a player spawns in default pvp maps
    private static final String playerStartId = "playerstart";

    /**
     * Run when initializing a playstate. This processes mode-specific settings
     * @param state: playstate being initialized
     */
    public void processSettings(PlayState state) {

        //initial notifications announces things like modifiers at thte start of the match
        initialNotifications.clear();

        //for maps with no applicable settings, we don't need to add the extra events to the map (campaign maps)
        if (applicableSettings.length == 0) { return; }

        String timerId = TiledObjectUtil.getPrefabTriggerIdUnsynced();
        String multiId = TiledObjectUtil.getPrefabTriggerIdUnsynced();

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

        //using these string builders, we modify the aforementioned events based on the mode's settings
        StringBuilder uiTriggerId = new StringBuilder(UIText.LEVEL.text());
        StringBuilder spawnTriggerId = new StringBuilder();
        StringBuilder startTriggerId = new StringBuilder(timerId);

        for (ModeSetting setting : applicableSettings) {

            //only the server saves their chosen settings.
            if (state.isServer()) {

                //only save settings when entering mode from hub selection
                if (this.equals(HUB)) {
                    setting.saveSetting(state, this);
                }

                //atm, the client does not need to run this b/c it creates events only the server needs to process.
                String newStart = setting.loadSettingStart(state, this);
                if (!"".equals(newStart)) {
                    startTriggerId.append(',').append(newStart);
                }
            }

            //this creates a comma-separated list of event ids that will be activated upon spawning/starting the game
            String newSpawn = setting.loadSettingSpawn(state);
            if (!"".equals(newSpawn)) {
                spawnTriggerId.append(',').append(newSpawn);
            }

            String newUi = setting.loadUIStart(state, this);
            if (!"".equals(newUi)) {
                uiTriggerId.append(',').append(newUi);
            }
            setting.loadSettingMisc(state, this);
        }

        JSONManager.setting.saveSetting();

        //ui text set directly instead of through an event, since ui is initiated immediately
        //null ui manager = headless server
        if (state.getUIManager() != null) {
            state.getUIManager().getUiExtra().changeTypes(uiTriggerId.toString(), true);
        }

        playerstart.getProperties().put("triggeringId", spawnTriggerId.toString());
        multi.getProperties().put("triggeringId", startTriggerId.toString());

        TiledObjectUtil.parseAddTiledEvent(state, playerstart);
        if (state.isServer()) {
            TiledObjectUtil.parseAddTiledEvent(state, timer);
            TiledObjectUtil.parseAddTiledEvent(state, multi);
        }
    }

    /**
     * This is run before a player is created.
     * This is separate from processNewPlayerLoadout because it occurs before the player spawns.
     * This is needed for affects that modify the player's loadout, since their team decides where they spawn in some modes
     */
    public void processNewPlayerAlignment(PlayState state, Loadout newLoadout, int connID) {
        if (!state.isServer()) { return; }
        for (ModeSetting setting : applicableSettings) {
            setting.processNewPlayerAlignment(state, this, newLoadout, connID);
        }
    }

    /**
     * This is run when a player is created. This is used to change properties of the player prior to player init
     * @param newLoadout: the new loadout the player will spawn with. modify to change starting loadout
     * @param connID: connID of the player being created. We use connID b/c the player isn't created yet
     */
    public void processNewPlayerLoadout(PlayState state, Loadout newLoadout, int connID) {
        if (!state.isServer()) { return; }
        for (ModeSetting setting : applicableSettings) {
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
        for (ModeSetting setting : applicableSettings) {
            setting.modifyNewPlayer(state, this, newLoadout, p, hitboxFilter);
        }
        state.getUIManager().getUiExtra().syncUIText(UITagType.PLAYERS_ALIVE);
    }

    public void postCreatePlayer(PlayState state, Player p) {
        for (ModeSetting setting : applicableSettings) {
            setting.postCreatePlayer(state, this, p);
        }
    }

    /**
     * This is run when a player dies. Change score values and do mode-specific death processing
     * @param perp: the schmuck (not necessarily player) that killed
     * @param vic: the player that died
     */
    public void processPlayerDeath(PlayState state, Schmuck perp, Player vic, DamageSource source, DamageTag... tags) {
        if (!state.isServer()) { return; }
        if (vic != null) {
            User user = vic.getUser();
            if (user != null) {
                user.getScoreManager().setDeaths(user.getScoreManager().getDeaths() + 1);
                user.setScoreUpdated(true);
            }
            for (PlayerBodyData playerData : vic.getPlayerData().getRecentDamagedBy().keys()) {
                User assisted = playerData.getPlayer().getUser();
                if (assisted != null) {
                    assisted.getScoreManager().setAssists(assisted.getScoreManager().getAssists() + 1);
                    assisted.setScoreUpdated(true);
                }
            }
        }
        if (perp != null) {
            if (perp instanceof Player player) {
                User user = player.getUser();
                if (user != null) {
                    user.getScoreManager().setKills(user.getScoreManager().getKills() + 1);
                    user.setScoreUpdated(true);
                }
            }
        }
        for (ModeSetting setting : applicableSettings) {
            setting.processPlayerDeath(state, this, perp, vic, source, tags);
        }
        state.getUIManager().getUiExtra().syncUIText(UITagType.PLAYERS_ALIVE);
    }

    /**
     * This is run when a player's score changes. Used for modes where an effect should activate upon score changing
     * @param p: the player whose score is changing
     * @param scoreIncrement: the amount to change the score by
     */
    public void processPlayerScoreChange(PlayState state, Player p, int scoreIncrement) {
        if (!state.isServer()) { return; }
        if (p != null) {
            User user = p.getUser();

            if (user != null) {
                user.getScoreManager().setScore(user.getScoreManager().getScore() + scoreIncrement);

                for (ModeSetting setting : applicableSettings) {
                    setting.processPlayerScoreChange(state, user.getScoreManager().getScore());
                }

                //tell score window and ui extra to update next interval
                user.setScoreUpdated(true);
                state.getUIManager().getUiExtra().syncUIText(UITagType.SCORE);
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
                setting.processTeamScoreChange(state, newScore);
            }

            //tell ui extra to sync updated score
            state.getUIManager().getUiExtra().syncUIText(UITagType.TEAMSCORE);
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
        state.getUIManager().getKillFeed().addNotification(UIText.ELIMINATED.text(TextUtil.getPlayerColorName(p, MAX_NAME_LENGTH)), true);
    }

    /**
     * This is run regularly by bots to find an optimal path through the map accounting for mode-specific events
     * @param p: Bot player doing the pathfinding
     * @param playerLocation: loaction of the bot player
     * @return list of points the bot is looking for with multipliers
     */
    public Array<RallyPoint.RallyPointMultiplier> processAIPath(PlayState state, PlayerBot p, Vector2 playerLocation) {
        Array<RallyPoint.RallyPointMultiplier> path = new Array<>();
        for (ModeSetting setting : applicableSettings) {
            setting.processAIPath(state, p, playerLocation, path);
        }
        return path;
    }

    /**
     * This is run when the game ends. Atm, this just cleans up bot pathfinding threads
     */
    public void processGameEnd(PlayState state) {
        for (ModeSetting setting : applicableSettings) {
            setting.processGameEnd(state);
        }
    }

    /**
     * This returns the sprite representing this mode in the ui
     */
    public TextureRegion getFrame() {
        return ((TextureAtlas) HadalGame.assetManager.get(AssetList.MODE_ICONS.toString())).findRegion(spriteId);
    }

    private static final ObjectMap<String, GameMode> ModesByName = new ObjectMap<>();
    static {
        for (GameMode m : GameMode.values()) {
            ModesByName.put(m.toString(), m);
        }
    }
    public static GameMode getByName(String s) {
        return ModesByName.get(s, HUB);
    }

    public String getName() { return name.text(); }

    public String getDesc() { return desc.text(); }

    public boolean isTeamDesignated() {
        return TeamMode.TEAM_AUTO.equals(teamMode) || TeamMode.HUMANS_VS_BOTS.equals(teamMode);
    }

    public String[] getExtraLayers() { return extraLayers; }

    public GameMode getCheckCompliance() { return checkCompliance; }

    public ModeSetting[] getSettings() { return applicableSettings; }

    public boolean isInvisibleInHub() { return false; }

    public boolean isHub() { return false; }

    public boolean isArcadeBreakroom() { return false; }

    public boolean isFriendlyFire() { return !isHub() && !isArcadeBreakroom(); }

    public boolean isJoinMidGame() { return joinMidGame; }

    public void setJoinMidGame(boolean joinMidGame) { this.joinMidGame = joinMidGame; }

    public float getBotScoreAggroModifier() { return botScoreAggroModifier; }

    public void setBotScoreAggroModifier(float botScoreAggroModifier) { this.botScoreAggroModifier = botScoreAggroModifier; }

    public Array<String> getInitialNotifications() { return initialNotifications; }

    public TeamMode getTeamMode() { return teamMode; }

    public void setTeamMode(TeamMode teamMode) { this.teamMode = teamMode; }

    public int getTeamNum() { return teamNum; }

    public void setTeamNum(int teamNum) { this.teamNum = teamNum; }

    public int getTeamStartScore() { return teamStartScore; }

    public void setTeamStartScore(int teamStartScore) { this.teamStartScore = teamStartScore; }

    public LoadoutMode getLoadoutMode() { return loadoutMode; }

    public void setLoadoutMode(LoadoutMode loadoutMode) { this.loadoutMode = loadoutMode; }

    public BotDifficulty getBotDifficulty() { return botDifficulty; }

    public void setBotDifficulty(BotDifficulty botDifficulty) { this.botDifficulty = botDifficulty; }
}
