package com.mygdx.hadal.states;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.event.utility.PositionDummy;
import com.mygdx.hadal.handlers.WorldContactListener;
import com.mygdx.hadal.managers.*;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.SettingArcade;
import com.mygdx.hadal.map.SettingTeamMode.TeamMode;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.entities.*;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.LobbyInfoDynamic;
import com.mygdx.hadal.server.packets.PacketEffect;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.users.User.UserDto;
import com.mygdx.hadal.users.UserManager;
import com.mygdx.hadal.utils.CameraUtil;
import com.mygdx.hadal.utils.TiledObjectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mygdx.hadal.constants.Constants.PHYSICS_TIME;
import static com.mygdx.hadal.users.Transition.DEFAULT_FADE_OUT_SPEED;
import static com.mygdx.hadal.users.Transition.SHORT_FADE_DELAY;

/**
 * The PlayState is the main state of the game and holds the Box2d world, all characters + gameplay.
 * @author Norroway Nigganov
 */
public class PlayState extends GameState {

	//This is the player's controller that receives inputs
	protected InputProcessor controller;

	//This is the loadout that the player starts off with when they enter the playstate
	private final Array<UnlockArtifact> mapModifiers = new Array<>();
	private final Array<UnlockManager.UnlockTag> mapEquipTags = new Array<>();

	//These process and store the map parsed from the Tiled file.
	protected TiledMap map;
	protected World world;

	private CameraManager cameraManager;

	private UIManager uiManager;
	private TimerManager timerManager;

	//These represent the set of entities to be added to/removed from the world. This is necessary to ensure we do this between world steps.
	private final OrderedSet<HadalEntity> removeList = new OrderedSet<>();
	private final OrderedSet<HadalEntity> createList = new OrderedSet<>();

	//These sets are used by the Client for removing/adding entities.
	protected final OrderedSet<UUID> removeListClient = new OrderedSet<>();
	protected final OrderedSet<PlayStateClient.CreatePacket> createListClient = new OrderedSet<>();

	//This is a set of all non-hitbox entities in the world
	private final OrderedSet<HadalEntity> entities = new OrderedSet<>();
	//This is a set of all hitboxes. This is separate to draw hitboxes underneath other bodies
	private final OrderedSet<HadalEntity> hitboxes = new OrderedSet<>();
	//This is a set of all particle effects. This is separate to draw effects above other bodies
	private final OrderedSet<HadalEntity> effects = new OrderedSet<>();

	private final Array<OrderedSet<HadalEntity>> entityLists = new Array<>();

	//This is a list of packetEffects, given when we receive packets with effects that we want to run in update() rather than whenever
	private final List<PacketEffect> packetEffects = new ArrayList<>();
	private final List<PacketEffect> addPacketEffects = java.util.Collections.synchronizedList(new ArrayList<>());
	
	//sourced effects from the world are attributed to this dummy.
	private WorldDummy worldDummy;
	private AnchorPoint anchor;
	
	//these the current level (map info) and mode (objective/modifier info)
	protected final UnlockLevel level;
	protected final GameMode mode;

	//This is the id of the start event that we will be spawning on (for 1-p maps that can be entered from >1 point)
	private final String startID;

	//are we currently a spectator or not?
	protected boolean spectatorMode;

	//If a player respawns, they will respawn at the coordinates of a safe point from this list.
	private final Array<StartPoint> savePoints = new Array<>();
	
	//This is an arrayList of ids to dummy events. These are used for enemy ai processing
	private final ObjectMap<String, PositionDummy> dummyPoints = new ObjectMap<>();

	//modifier that affects game engine speed used for special mode modifiers
	private float timeModifier = 0.0f;

	//default respawn time that can be changed in mode settings
	private float respawnTime = 1.5f;

	//if we are transitioning to another state, this is that state
	protected TransitionState nextState;
	
	//If we are transitioning to another level, this is that level.
	private UnlockLevel nextLevel;
	private GameMode nextMode;
	private String nextStartID;
	
	//If we are transitioning to a results screen, this is the displayed text;
	protected String resultsText = "";
	
	//Has the server finished loading yet?
	private boolean serverLoaded;
	
	//Do players connecting to this have their hp/ammo/etc reset?
	private final boolean reset;
	
	//Special designated events parsed from map.
	// Event run when a timer runs out or spectating host presses their interact button
	private Event globalTimer, spectatorActivation;
	
	/**
	 * Constructor is called upon player beginning a game.
	 * @param level: the level we are loading into
	 * @param mode: the mode of the level we are loading into
	 * @param reset: do we reset the old player's hp/fuel/ammo in the new playstate?
	 * @param startID: the id of the starting event the player should be spawned at
	 */
	public PlayState(HadalGame app, UnlockLevel level, GameMode mode, boolean reset, String startID) {
		super(app);
		this.level = level;
		this.mode = mode;
		this.reset = reset;

		//start id is set when loadlevel is called, but might be null otherwise; use empty string as default
		this.startID = null == startID ? "" : startID;
	}

	public void initializePlayState() {
		//Initialize box2d world
		world.setContactListener(new WorldContactListener());
		World.setVelocityThreshold(0);

		//Initialize sets to keep track of active entities and packet effects
		entityLists.add(hitboxes);
		entityLists.add(entities);
		entityLists.add(effects);

		//load map. We override the render so that we can apply a shader to the tileset
		initializeMap();

		this.cameraManager = new CameraManager(this, map);
		this.uiManager = new UIManager(this);
		this.timerManager = new TimerManager(this);

		//clears things like sounds, sprites and particles
		clearMemory();

		//The "worldDummy" will be the source of map-effects that want a perp (create after clearing memory b/c it creates an impact particle)
		worldDummy = new WorldDummy(this);

		//anchor is used to attach "static" entities without making them static
		anchor = new AnchorPoint(this);

		//Clear events in the TiledObjectUtil to avoid keeping reference to previous map's events.
		TiledObjectUtil.clearEvents();
		parseMap();
		processTeams();
	}

	public void initializeMap() {
		map = new TmxMapLoader().load(level.getMap());
	}

	public void clearMemory() {}

	public void parseMap() {
		//Only the server processes collision objects, events and triggers
		//Server must first reset each score at the start of a level (unless just a stage transition)
		if (reset) {
			for (User user : HadalGame.usm.getUsers().values()) {
				user.newLevelReset(this);
			}
		}

		mode.processSettings(this);

		TiledObjectUtil.parseTiledObjectLayer(this, map.getLayers().get("collision-layer").getObjects());
		TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get("event-layer").getObjects());

		//parse map-specific event layers (used for different modes in the same map)
		for (String layer : mode.getExtraLayers()) {
			if (map.getLayers().get(layer) != null) {
				TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get(layer).getObjects());
			}
		}
		TiledObjectUtil.parseTiledTriggerLayer();
		TiledObjectUtil.parseDesignatedEvents(this);
	}

	public void processTeams() {
		//if auto-assign team is on, we do the assignment here
		for (User user : getUserManager().getUsers().values()) {
			user.setTeamAssigned(false);
		}
		if (TeamMode.TEAM_AUTO.equals(mode.getTeamMode())) {
			AlignmentFilter.autoAssignTeams(mode.getTeamNum(), mode.getTeamMode(), mode.getTeamStartScore());
		} else if (TeamMode.HUMANS_VS_BOTS.equals(mode.getTeamMode())) {
			AlignmentFilter.autoAssignTeams(2, mode.getTeamMode(), mode.getTeamStartScore());
		} else {
			AlignmentFilter.resetTeams();
		}
	}

	public void resetController() {}

	@Override
	public void show() {}
	
	//these control the frequency that we process world physics.
	private float physicsAccumulator;

	//these control the frequency that we send sync packets for world entities.
	public static final float SYNC_TIME = 0.05f;
	public static final float SYNC_FAST_TIME = 1 / 60f;
	public static final float SYNC_INTERPOLATION = 0.125f;
	private float syncAccumulator;
	private float syncFastAccumulator;

	protected static final float SCORE_SYNC_TIME = 1.0f;
	protected static final float LOBBY_SYNC_TIME = 15.0f;
	protected float scoreSyncAccumulator = SCORE_SYNC_TIME;
	protected float lobbySyncAccumulator = LOBBY_SYNC_TIME;

	private float timer;
	/**
	 * Every engine tick, the GameState must process all entities in it according to the time elapsed.
	 */
	@Override
	public void update(float delta) {

		float modifiedDelta = delta * (1.0f + timeModifier);

		//On the very first tick, server tells all clients that it is loaded. Also, initiate bots if applicable
		if (!serverLoaded) {
	        serverLoaded = true;
			PacketManager.serverTCPAll(this, new Packets.ServerLoaded());
			BotManager.initiateBots(this);
		}

		//this makes the physics separate from the game framerate
		physicsAccumulator += modifiedDelta;
		while (physicsAccumulator >= PHYSICS_TIME) {
			physicsAccumulator -= PHYSICS_TIME;

			//The box2d world takes a step. This handles collisions + physics stuff.
			world.step(PHYSICS_TIME, 8, 3);
		}

		//All entities that are set to be added are added.
		for (HadalEntity entity : createList) {
			if (ObjectLayer.HBOX.equals(entity.getLayer())) {
				hitboxes.add(entity);
			} else if (ObjectLayer.EFFECT.equals(entity.getLayer())) {
				effects.add(entity);
			} else {
				entities.add(entity);
			}
			entity.create();
			//Upon creating an entity, tell the clients so they can follow suit (if the entity calls for it)
			Object packet = entity.onServerCreate(false);
			if (packet != null) {
				if (entity.isReliableCreate()) {
					PacketManager.serverTCPAll(this, packet);
				} else {
					PacketManager.serverUDPAll(this, packet);
				}
			}
		}
		createList.clear();

		//All entities that are set to be removed are removed.
		for (HadalEntity entity : removeList) {
			for (ObjectSet<HadalEntity> s : entityLists) {
				s.remove(entity);
			}
			entity.dispose();

			//Upon deleting an entity, tell the clients so they can follow suit.
			Object packet = entity.onServerDelete();
			if (packet != null) {
				if (entity.isReliableCreate()) {
					PacketManager.serverTCPAll(this, packet);
				} else {
					PacketManager.serverUDPAll(this, packet);
				}
			}
		}
		removeList.clear();

		//TODO: process all users only in this specific lobby
		for (User user : HadalGame.usm.getUsers().values()) {
			user.controller(this, delta);
		}

		//process camera, ui, any received packets
		processCommonStateProperties(delta, false);

		//This processes all entities in the world. (for example, player input/cooldowns/enemy ai)
		controllerEntities(modifiedDelta);

		//Send client a sync packet if the entity requires.
		if (HadalGame.server.getServer() != null) {

			syncAccumulator += delta;
			if (syncAccumulator >= SYNC_TIME) {
				syncAccumulator = 0;
				syncEntities();
			}
			syncFastAccumulator += delta;
			if (syncFastAccumulator >= SYNC_FAST_TIME) {
				syncFastAccumulator = 0;
				syncFastEntities();
			}
		}
		
		//send periodic sync packets for score (for users whose scores have changed
		scoreSyncAccumulator += delta;
		if (scoreSyncAccumulator >= SCORE_SYNC_TIME) {
			scoreSyncAccumulator = 0;
			for (User user : HadalGame.usm.getUsers().values()) {
				if (user.isScoreUpdated()) {
					user.setScoreUpdated(false);

					ScoreManager score = user.getScoreManager();
					PacketManager.serverUDPAll(this,
							new Packets.SyncScore(user.getConnID(), user.getStringManager().getNameShort(),
							user.getLoadoutManager().getSavedLoadout(), score.getWins(), score.getKills(), score.getDeaths(),
							score.getAssists(), score.getScore(), score.getExtraModeScore(),
							score.getLives(), score.getCurrency(), user.getPing(), user.isSpectator()));
				}
			}
		}
	}

	/**
	 * This method renders stuff to the screen after updating.
	 */
	@Override
	public void render(float delta) {}
	
	/**
	 * This is run in the update method and is just a helper to avoid repeating code in both the server/client states.
	 * This does all of the stuff that is needed for both server and client (processing packets, fade and some other misc stuff)
	 * postgame is used so that the state can continue processing packets in the results state
	 */
	public void processCommonStateProperties(float delta, boolean postGame) {
		
		//When we receive packets and don't want to process their effects right away, we store them in packetEffects
		//to run here. This way, they will be carried out at a predictable time.
		synchronized (addPacketEffects) {
			packetEffects.addAll(addPacketEffects);
			addPacketEffects.clear();
		}
		for (PacketEffect packetEffect : packetEffects) {
			packetEffect.execute();
		}
		packetEffects.clear();

		//send server info to matchmaking server
		lobbySyncAccumulator += delta;
		if (lobbySyncAccumulator >= LOBBY_SYNC_TIME) {
			lobbySyncAccumulator = 0;
			LobbyInfoDynamic lobbyInfo = new LobbyInfoDynamic(
					HadalGame.usm.getNumPlayers(),
					JSONManager.setting.getMaxPlayers() + 1,
					mode, level);

			//TODO: update lobby manager with data
		}

		if (!postGame) {
			//Increment the game timer, if exists
			timer += delta;
		}
	}
	
	/**
	 * Run the controller method for all entities in the world
	 */
	public void controllerEntities(float delta) {
		for (ObjectSet<HadalEntity> s : entityLists) {
			for (HadalEntity entity : s) {
				entity.controller(delta);
			}
		}
	}
	
	/**
	 * This sends a synchronization packet for every synced entity. syncFastEntities() is used for entities that are synced more frequently
	 */
	private final Array<Object> syncPackets = new Array<>();
	public void syncEntities() {

		for (ObjectSet<HadalEntity> s : entityLists) {
			for (HadalEntity entity : s) {
				entity.onServerSync();
			}
		}
		for (Object o : syncPackets) {
			PacketManager.serverUDPAll(this, o);
		}
		syncPackets.clear();
	}
	
	public void syncFastEntities() {
		for (ObjectSet<HadalEntity> s : entityLists) {
			for (HadalEntity entity : s) {
				entity.onServerSyncFast();
			}
		}
	}

	/**
	 * This is called upon exiting. Dispose of all created fields.
	 */
	@Override
	public void dispose() {
		for (ObjectSet<HadalEntity> s : entityLists) {
			for (HadalEntity entity : s) {
				entity.dispose();
			}
		}
		for (HadalEntity entity : removeList) {
			entity.dispose();
		}
		
		world.dispose();
		map.dispose();
		if (stage != null) {
			stage.dispose();
		}
		CameraUtil.resetCameraRotation(camera);
	}

	/**
	 * transition from one playstate to another with a new level.
	 * @param level: level of the new map
	 * @param mode: mode of the new map
	 * @param state: this will either be a new level or next stage to determine whether we reset hp
	 * @param nextStartID: The id of the start point to start at (if specified)
	 */
	public void loadLevel(UnlockLevel level, GameMode mode, TransitionState state, String nextStartID) {
		//The client should never run this; instead transitioning when the server tells it to.
		if (nextState == null) {

			//begin transitioning to the designated next level and tell all clients to start transitioning
			nextLevel = level;
			nextMode = mode;
			this.nextStartID = nextStartID;

			for (User user : HadalGame.usm.getUsers().values()) {
				user.getTransitionManager().beginTransition(this, new Transition().setNextState(state)
						.setReset(state == TransitionState.NEWLEVEL));
			}
		}
	}

	public void loadLevel(UnlockLevel level, TransitionState state, String nextStartID) {
		loadLevel(level, level.getModes()[0], state, nextStartID);
	}
	
	/**This creates a player to occupy the playstate
	 * @param start: start event to spawn the player at.
	 * @param name: player name
	 * @param loadout: the player's loadout
	 * @param old: player's old playerdata if retaining old values.
	 * @param reset: should we reset the new player's hp/fuel/ammo?
	 * @param client: is this the client's own player?
	 * @param hitboxFilter: the new player's collision filter
	 * @return the newly created player
	 */
	public Player createPlayer(Event start, String name, Loadout loadout, PlayerBodyData old, User user, boolean reset,
							   boolean client, short hitboxFilter) {

		Loadout newLoadout = new Loadout(loadout);

		//process mode-specific loadout changes
		mode.processNewPlayerLoadout(this, newLoadout, user.getConnID());
		user.getLoadoutManager().setActiveLoadout(newLoadout);

		//set start pont, generate one if a designated one isn't passed in
		Event spawn = start;
		if (spawn == null) {
			spawn = getSavePoint(user);
		}

		Vector2 overiddenSpawn = new Vector2();
		if (spawn != null) {
			//servers spawn at the starting point if existent. We prefer using the body's position,
			// but can also use the starting position if it hasn't been created yet.
			if (spawn.getBody() != null) {
				overiddenSpawn.set(spawn.getPixelPosition());
			} else {
				overiddenSpawn.set(spawn.getStartPos());
			}
		}

		Player p = spawnPlayer(name, old, user, client, spawn, overiddenSpawn);
		user.setPlayer(p);

		//mode-specific player modifications
		mode.modifyNewPlayer(this, newLoadout, p, hitboxFilter);
		return p;
	}

	public Player spawnPlayer(String name, PlayerBodyData old, User user, boolean client, Event spawn, Vector2 overiddenSpawn) {
		Player p;

		//process spawn overrides if the user specifies being spawned at a set location instead of at a start point
		if (user.getTransitionManager().isSpawnOverridden()) {
			overiddenSpawn.set(user.getTransitionManager().getOverrideSpawnLocation());
		}

		if (user.getConnID() < 0) {
			p = new PlayerBot(this, overiddenSpawn, name, old, user, reset, spawn);
		} else {
			if (0 == user.getConnID()) {
				p = new Player(this, overiddenSpawn, name, old, user, reset, spawn);
			} else {
				p = new PlayerClientOnHost(this, overiddenSpawn, name, old, user, reset, spawn);
			}
		}

		//teleportation particles for reset players (indicates returning to hub)
		if (reset && user.getEffectManager().isShowSpawnParticles()) {
			new ParticleEntity(this, new Vector2(p.getStartPos()).sub(0, p.getSize().y / 2),
					Particle.TELEPORT, 2.5f, true, SyncType.CREATESYNC).setPrematureOff(1.5f);
		}

		return p;
	}
	
	//This is a list of all the saved player fields (scores) from the completed playstate
	private final ObjectMap<AlignmentFilter, Integer> teamKills = new ObjectMap<>();
	private final ObjectMap<AlignmentFilter, Integer> teamDeaths = new ObjectMap<>();
	private final ObjectMap<AlignmentFilter, Integer> teamScores = new ObjectMap<>();
	private final Array<AlignmentFilter> teamScoresList = new Array<>();

	//this is used to avoid running this multiple times
	private boolean levelEnded;
	/**
	 * This is called when a level ends. Only called by the server. Begin a transition and tell all clients to follow suit.
	 * @param text: text displayed in results state
	 * @param victory: indicates a win for all players in pve
	 * @param incrementWins: Should player's wins be incremented? (false for arcade break room)
	 * @param fadeDelay: Duration of
	 */
	public void levelEnd(String text, boolean victory, boolean incrementWins, float fadeDelay) {
		if (levelEnded) { return; }
		levelEnded = true;

		String resultsText = text;

		//list of non-spectator users to be sorted
		Array<User> activeUsers = new Array<>();
		for (User user : HadalGame.usm.getUsers().values().toArray()) {
			if (!user.isSpectator()) {
				activeUsers.add(user);
			}
		}

		//magic word indicates that we generate the results text dynamically based on score
		if (ResultsState.MAGIC_WORD.equals(text)) {
			for (User user : HadalGame.usm.getUsers().values().toArray()) {
				if (!user.isSpectator()) {

					AlignmentFilter faction;
					if (AlignmentFilter.NONE.equals(user.getTeamFilter())) {
						faction = user.getHitboxFilter();
					} else {
						faction = user.getTeamFilter();
					}

					//add users's kills, deaths and scores to respective list and keep track of sum
					if (teamKills.containsKey(faction)) {
						teamKills.put(faction, teamKills.get(faction) + user.getScoreManager().getKills());
					} else {
						teamKills.put(faction, user.getScoreManager().getKills());
					}
					if (teamDeaths.containsKey(faction)) {
						teamDeaths.put(faction, teamDeaths.get(faction) + user.getScoreManager().getDeaths());
					} else {
						teamDeaths.put(faction, user.getScoreManager().getDeaths());
					}
					if (teamScores.containsKey(faction)) {
						teamScores.put(faction, teamScores.get(faction) + user.getScoreManager().getScore());
					} else {
						teamScores.put(faction, user.getScoreManager().getScore());
					}
				}
			}

			//sort scores and team scores according to score, then kills, then deaths
			activeUsers.sort((a, b) -> {
				int cmp = (b.getScoreManager().getScore() - a.getScoreManager().getScore());
				if (cmp == 0) { cmp = b.getScoreManager().getKills() - a.getScoreManager().getKills(); }
				if (cmp == 0) { cmp = a.getScoreManager().getDeaths() - b.getScoreManager().getDeaths(); }
				return cmp;
			});

			teamScoresList.addAll(teamScores.keys().toArray());
			teamScoresList.sort((a, b) -> {
				int cmp = (teamScores.get(b) - teamScores.get(a));
				if (cmp == 0) { cmp = teamKills.get(b) - teamKills.get(a); }
				if (cmp == 0) { cmp = teamDeaths.get(a) - teamDeaths.get(b); }
				return cmp;
			});

			//if free-for-all, the first player in the sorted list is the victor
			if (TeamMode.FFA.equals(mode.getTeamMode())) {
				resultsText = UIText.PLAYER_WINS.text(activeUsers.get(0).getStringManager().getNameShort());
			} else {

				//in team modes, get the winning team and display a win for that team (or individual if no alignment)
				AlignmentFilter winningTeam = teamScoresList.get(0);
				if (winningTeam.isTeam()) {
					resultsText = UIText.PLAYER_WINS.text(winningTeam.getColoredAdjective());
				} else {
					for (User user : activeUsers) {
						if (user.getHitboxFilter().equals(winningTeam)) {
							resultsText = UIText.PLAYER_WINS.text(user.getStringManager().getNameShort());
						}
					}
				}
			}

			AlignmentFilter winningTeam = teamScoresList.get(0);
			ScoreManager winningScore = activeUsers.get(0).getScoreManager();

			//give a win to all players with a winning alignment (team or solo)
			for (User user : activeUsers) {
				ScoreManager score = user.getScoreManager();
				if (TeamMode.FFA.equals(mode.getTeamMode())) {
					if (score.getScore() == winningScore.getScore() && score.getKills() == winningScore.getKills()
							&& score.getDeaths() == winningScore.getDeaths()) {
						if (incrementWins) {
							score.win();
						}
					}
				} else {
					AlignmentFilter faction;

					if (AlignmentFilter.NONE.equals(user.getTeamFilter())) {
						faction = user.getHitboxFilter();
					} else {
						faction = user.getTeamFilter();
					}

					if (teamScores.containsKey(faction)) {
						score.setTeamScore(teamScores.get(faction));
					}

					if (user.getHitboxFilter().equals(winningTeam) || user.getTeamFilter().equals(winningTeam)) {
						if (incrementWins) {
							score.win();
						}
					}
				}
			}
		} else if (victory) {
			//in coop, all players get a win if the team wins
			if (incrementWins) {
				for (User user : activeUsers) {
					user.getScoreManager().win();
				}
			}
        }

		if (SettingArcade.arcade) {
			SettingArcade.processEndOfRound(this, mode);
		} else {
			transitionToResultsState(resultsText, fadeDelay);
		}
	}

	/**
	 * This is run by the server to transition to the results screen
	 * @param resultsText: what text to display as the title of the results screen?
	 * @param fadeDelay: how many seconds of delay before transition begins?
	 */
	public void transitionToResultsState(String resultsText, float fadeDelay) {

		//mode-specific end-game processing (Atm this just cleans up bot pathfinding threads)
		mode.processGameEnd(this);

		this.resultsText = resultsText;

		//create list of user information to send to all clients
		UserDto[] users = new UserDto[HadalGame.usm.getUsers().size];

		int userIndex = 0;
		for (User user : HadalGame.usm.getUsers().values()) {
			users[userIndex] = new UserDto(user.getScoreManager(), user.getStatsManager(), user.getLoadoutManager().getActiveLoadout(),
					user.getStringManager().getName(), user.getConnID(), user.getPing(), user.isSpectator());
			userIndex++;
		}

		PacketManager.serverTCPAll(this, new Packets.SyncExtraResultsInfo(users, resultsText));

		//all users transition to results state
		for (User user : HadalGame.usm.getUsers().values()) {
			user.getTransitionManager().beginTransition(this,
					new Transition()
							.setNextState(TransitionState.RESULTS)
							.setFadeSpeed(0.0f)
							.setFadeDelay(fadeDelay)
							.setOverride(true));
		}
	}

	/**
	 * This is used to make a specific player a spectator after a transition.
	 * This is only run by the server
	 */
	public void becomeSpectator(User user, boolean notification) {
		if (!user.isSpectator()) {
			if (notification) {
				HadalGame.server.addNotificationToAll(this,"", UIText.SPECTATOR_ENTER.text(user.getStringManager().getName()),
						true, DialogType.SYSTEM);
			}

			startSpectator(user);

			//we die last so that the on-death transition does not occur (As it will not override the spectator transition unless it is a results screen.)
			if (null != user.getPlayer()) {
				if (null != user.getPlayer().getPlayerData()) {
					user.getPlayer().getPlayerData().die(worldDummy.getBodyData(), DamageSource.DISCONNECT);
				}
			}
		}
	}

	/**
	 * Make a specific player a spectator. Run by server only
	 * @param user: the user to become a spectator
	 */
	public void startSpectator(User user) {
		user.getTransitionManager().beginTransition(this,
				new Transition()
						.setNextState(TransitionState.SPECTATOR)
						.setFadeDelay(SHORT_FADE_DELAY));
		PacketManager.serverTCP(user.getConnID(),
				new Packets.ClientStartTransition(TransitionState.SPECTATOR, DEFAULT_FADE_OUT_SPEED, SHORT_FADE_DELAY,
						false, null));

		//set the spectator's player number to default so they don't take up a player slot
		user.getHitboxFilter().setUsed(false);
		user.setHitboxFilter(AlignmentFilter.NONE);
		user.setSpectator(true);
	}
	
	/**
	 * This is used to make a specific spectator a player after a transition.
	 * This is only run by the server
	 */
	public void exitSpectator(User user) {

		if (user != null) {
			if (user.isSpectator()) {
				//cannot exit spectator if server is full
				if (HadalGame.usm.getNumPlayers() >= JSONManager.setting.getMaxPlayers() + 1) {
					HadalGame.server.sendNotification(user.getConnID(), "", UIText.SERVER_FULL.text(), true, DialogType.SYSTEM);
					return;
				}

				HadalGame.server.addNotificationToAll(this, "", UIText.SPECTATOR_EXIT.text(user.getStringManager().getNameShort()),
						true, DialogType.SYSTEM);

				//give the new player a player slot
				user.setHitboxFilter(AlignmentFilter.getUnusedAlignment());
				user.setSpectator(false);

				//for host, start transition. otherwise, send transition packet
				user.getTransitionManager().beginTransition(this,
						new Transition()
								.setNextState(TransitionState.RESPAWN)
								.setFadeDelay(SHORT_FADE_DELAY));
			}
		}
	}

	/**
	 * This looks for an entity in the world with the given entityID
	 * this is kinda slow. don't overuse it.
	 */
	public HadalEntity findEntity(UUID entityID) {

		for (ObjectSet<HadalEntity> s : entityLists) {
			for (HadalEntity entity : s) {
				if (entity.getEntityID().equals(entityID)) {
					return entity;
				}
			}
		}
		return null;
	}

	public HadalEntity findEntity(long uuidMSB, long uuidLSB) {
		return findEntity(new UUID(uuidMSB, uuidLSB));
	}

	/**
	 * This is called by the server when a new client connects. We catch up the client by making them create all existing entities.
	 * @param connID: connID of the new client
	 */
	public void catchUpClient(int connID) {
		for (ObjectSet<HadalEntity> s : entityLists) {
			for (HadalEntity entity : s) {
				Object packet = entity.onServerCreate(true);
				if (packet != null) {
					PacketManager.serverUDP(connID, packet);
				}
			}
		}
	}
	
	/**
	 * This acquires the level's save points. If none, respawn at starting location. If many, choose one randomly
	 * @return a save point to spawn a respawned player at
	 */
	public StartPoint getSavePoint(String startID, User user) {
		Array<StartPoint> validStarts = new Array<>();
		Array<StartPoint> readyStarts = new Array<>();

		//get a list of all start points that match the startID
		for (StartPoint s : savePoints) {
			if (mode.isTeamDesignated() && AlignmentFilter.currentTeams.length > s.getTeamIndex()) {
				if (user.getTeamFilter().equals(AlignmentFilter.currentTeams[s.getTeamIndex()])) {
					validStarts.add(s);
				}
			} else if (s.getStartId().equals(startID)) {
				validStarts.add(s);
			}
		}

		//if no start points are found, we return the first save point (if existent)
		if (validStarts.isEmpty()) {
			if (mode.isTeamDesignated()) {
				validStarts.addAll(savePoints);
			} else {
				if (savePoints.isEmpty()) {
					return null;
				} else {
					return savePoints.get(0);
				}
			}
		}
		
		//add all valid starts that haven't had a respawn recently.
		for (StartPoint s : validStarts) {
			if (s.isReady()) {
				readyStarts.add(s);
			}
		}

		//if any start points haven't been used recently, pick one of them randomly. Otherwise pick a random valid start point
		if (readyStarts.isEmpty()) {
			int randomIndex = MathUtils.random(validStarts.size - 1);
			validStarts.get(randomIndex).startPointSelected();
			return validStarts.get(randomIndex);
		} else {
			int randomIndex = MathUtils.random(readyStarts.size - 1);
			readyStarts.get(randomIndex).startPointSelected();
			return readyStarts.get(randomIndex);
		}
	}
	
	/**
	 * This returns a single starting point for a newly spawned player to spawn at.
	 */
	public StartPoint getSavePoint(User user) {
		return getSavePoint(startID, user);
	}
	
	/**
	 * This adds a save point to the list of available spawns
	 */
	public void addSavePoint(StartPoint start) {
		savePoints.add(start);
	}
	
	/**
	 * This method is called by entities to be added to the set of entities to be deleted next engine tick.
	 * @param entity: delet this
	 */
	public void destroy(HadalEntity entity) {
		removeList.add(entity);
	}
	
	/**
	 * This method is called by entities to be added to the set of entities to be created next engine tick.
	 * @param entity: entity to be created
	 */
	public void create(HadalEntity entity) {
		createList.add(entity);
	}
	
	/**
	 * Add a new packet effect as a result of receiving a packet.
	 * @param effect: the code we want to run at the next engine tick
	 */
	public void addPacketEffect(PacketEffect effect) {
		synchronized (addPacketEffects) {
			addPacketEffects.add(effect);
		}
	}
	
	public float getRespawnTime(Player p) {
		return respawnTime * (1.0f + p.getPlayerData().getStat(Stats.RESPAWN_TIME));
	}

	public void beginTransition(TransitionState state, float fadeSpeed, float fadeDelay, boolean skipFade) {}

	public void returnToTitle(float delay) {}

	public UserManager getUserManager() { return HadalGame.usm; }

	/**
	 * This sets the game's boss, filling the boss ui.
	 * @param enemy: This is the boss whose hp will be used for the boss hp bar
	 */
	public void setBoss(Enemy enemy) {}

	/**
	 * This is called when the boss is defeated, clearing its hp bar from the ui.
	 * We also have to tell the client to do the same.
	 */
	public void clearBoss() {}

	public boolean isServer() { return true; }

	/**
	 * This sets a shader to be used as a "base-shader" for things like the background
	 */
	public void setShaderBase(Shader shader) {}

	public void setShaderTile(Shader shader) {}

	public void toggleVisibleHitboxes(boolean debugHitbox) {}

	public enum TransitionState {
		RESPAWN,
		RESULTS,
		SPECTATOR,
		NEWLEVEL,
		NEXTSTAGE,
		TITLE
	}

	/**
	 * Z-Axis Layers that entities can be added to. ATM, there is just 1 for hitboxes beneath everything else.
	 */
	public enum ObjectLayer {
		STANDARD,
		HBOX,
		EFFECT
	}

	public boolean isReset() { return reset; }

	public void addMapModifier(UnlockArtifact modifier) { this.mapModifiers.add(modifier); }

	public Array<UnlockArtifact> getMapModifiers() { return mapModifiers; }

	public void addMapEquipTag(UnlockTag mapEquipTag) { this.mapEquipTags.add(mapEquipTag); }

	public Array<UnlockTag> getMapEquipTag() { return mapEquipTags; }

	public Event getGlobalTimer() {	return globalTimer;	}

	public void setGlobalTimer(Event globalTimer) {	this.globalTimer = globalTimer;	}

	public Event getSpectatorActivation() {	return spectatorActivation;	}

	public void setSpectatorActivation(Event spectatorActivation) {	this.spectatorActivation = spectatorActivation;	}

	public World getWorld() { return world; }

	public TiledMap getMap() { return map; }

	public WorldDummy getWorldDummy() { return worldDummy; }
	
	public AnchorPoint getAnchor() { return anchor; }

	public UnlockLevel getLevel() { return level; }

	public GameMode getMode() { return mode; }

	public float getTimer() {return timer; }
	
	public void setTimer(float timer) { this.timer = timer; }

	public void setRespawnTime(float respawnTime) {	this.respawnTime = respawnTime; }

	public PositionDummy getDummyPoint(String id) {	return dummyPoints.get(id); }
	
	public void addDummyPoint(PositionDummy dummy, String id) {	dummyPoints.put(id, dummy); }

	public float getTimeModifier() { return timeModifier; }

	public void setTimeModifier(float timeModifier) { this.timeModifier = timeModifier; }

	public Array<Object> getSyncPackets() {	return syncPackets; }

	public void setNextState(TransitionState nextState) { this.nextState = nextState; }

	public TransitionState getNextState() { return nextState; }

	public void setNextLevel(UnlockLevel nextLevel) { this.nextLevel = nextLevel; }

	public void setNextMode(GameMode nextMode) { this.nextMode = nextMode; }

	public void setResultsText(String resultsText) { this.resultsText = resultsText; }

	public void setLevelEnded(boolean levelEnded) { this.levelEnded = levelEnded; }

	public InputProcessor getController() {	return controller; }

	public boolean isSpectatorMode() { return spectatorMode; }

	public CameraManager getCameraManager() { return cameraManager; }

	public UIManager getUIManager() { return uiManager; }

	public TimerManager getTimerManager() { return timerManager; }
}
