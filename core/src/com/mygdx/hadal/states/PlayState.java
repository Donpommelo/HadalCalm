package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.MusicIcon;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.audio.MusicTrackType;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.FrameBufferManager;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.utility.PositionDummy;
import com.mygdx.hadal.handlers.WorldContactListener;
import com.mygdx.hadal.input.CommonController;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.*;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.SettingTeamMode.TeamMode;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.entities.AnchorPoint;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.WorldDummy;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.PacketEffect;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.CameraUtil;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.UUIDUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mygdx.hadal.constants.Constants.PHYSICS_TIME;

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

	//world manages the Box2d world and physics.
	protected World world;

	protected RenderManager renderManager;
	protected CameraManager cameraManager;
	protected UIManager uiManager;
	protected TimerManager timerManager;
	protected SpawnManager spawnManager;
	protected TransitionManager transitionManager;
	protected SpectatorManager spectatorManager;
	protected EndgameManager endgameManager;

	//These represent the set of entities to be added to/removed from the world. This is necessary to ensure we do this between world steps.
	protected final OrderedSet<HadalEntity> removeList = new OrderedSet<>();
	private final OrderedSet<HadalEntity> createList = new OrderedSet<>();

	//These sets are used by the Client for removing/adding entities.
	protected final OrderedSet<Integer> removeListClient = new OrderedSet<>();
	protected final OrderedSet<ClientState.CreatePacket> createListClient = new OrderedSet<>();

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
	private final WorldDummy worldDummy;
	private final AnchorPoint anchor;
	
	//these the current level (map info) and mode (objective/modifier info)
	protected final UnlockLevel level;
	protected final GameMode mode;

	//This is an arrayList of ids to dummy events. These are used for enemy ai processing
	private final ObjectMap<String, PositionDummy> dummyPoints = new ObjectMap<>();

	//modifier that affects game engine speed used for special mode modifiers
	private float timeModifier = 0.0f;

	//default respawn time that can be changed in mode settings
	private float respawnTime = 1.5f;

	//is this the server or client?
	private final boolean server;

	//Has the server finished loading yet?
	private boolean serverLoaded;
	
	//Do players connecting to this have their hp/ammo/etc reset?
	private final boolean reset;
	
	//Special designated events parsed from map.
	// Event run when a timer runs out or spectating host presses their interact button
	private Event globalTimer, spectatorActivation;
	
	/**
	 * Constructor is called upon player beginning a game.
	 * @param app: the game
	 * @param level: the level we are loading into
	 * @param mode: the mode of the level we are loading into
	 * @param server: is this the server or not?
	 * @param reset: do we reset the old player's hp/fuel/ammo in the new playstate?
	 * @param startID: the id of the starting event the player should be spawned at
	 */
	public PlayState(HadalGame app, UnlockLevel level, GameMode mode, boolean server, boolean reset, String startID) {
		super(app);
		this.level = level;
		this.mode = mode;
		this.server = server;

        //Initialize box2d world and related stuff
		world = new World(new Vector2(0, -9.81f), true);
		world.setContactListener(new WorldContactListener());
		World.setVelocityThreshold(0);

		//Initialize sets to keep track of active entities and packet effects
		entityLists.add(hitboxes);
		entityLists.add(entities);
		entityLists.add(effects);

		initMap();

		//We clear things like music/sound/shaders to periodically free up some memory
		StateManager.clearMemory();
		UUIDUtil.nextPlayState();

		//we clear shaded cosmetics to avoid having too many cached fbos
		UnlockCosmetic.clearShadedCosmetics();

		//init managers. Must be done after clearing memory, otherwise new shader is cleared
		initManagers(startID);

		//The "worldDummy" will be the source of map-effects that want a perp (create after clearing memory b/c it creates an impact particle)
		worldDummy = new WorldDummy(this);

		//anchor is used to attach "static" entities without making them static
		anchor = new AnchorPoint(this);

		//Clear events in the TiledObjectUtil to avoid keeping reference to previous map's events.
		TiledObjectUtil.clearEvents();

		//Only the server processes collision objects, events and triggers
		if (server) {

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

		//if auto-assign team is on, we do the assignment here
		for (User user : HadalGame.usm.getUsers().values()) {
			user.setTeamAssigned(false);
		}
		if (TeamMode.TEAM_AUTO.equals(mode.getTeamMode()) && isServer()) {
			AlignmentFilter.autoAssignTeams(mode.getTeamNum(), mode.getTeamMode(), mode.getTeamStartScore());
		} else if (TeamMode.HUMANS_VS_BOTS.equals(mode.getTeamMode()) && isServer()) {
			AlignmentFilter.autoAssignTeams(2, mode.getTeamMode(), mode.getTeamStartScore());
		} else {
			AlignmentFilter.resetTeams();
		}

		//clear fbo of unused players. Need to do this after bots are set up and teams are assigned
		FrameBufferManager.clearAllFrameBuffers();

		if (server) {
			if (HadalGame.usm.getOwnUser() != null) {
				if (HadalGame.usm.getOwnUser().isSpectator()) {
					getSpectatorManager().setSpectatorMode();
				}
			}
		}

		this.reset = reset;
	}

	public void initManagers(String startID) {
		this.renderManager = new RenderManager(this, map);
		this.cameraManager = new CameraManager(this, map);
		this.uiManager = new UIManager(this);
		this.timerManager = new TimerManager(this);
		this.spawnManager = new SpawnManager(this, startID);
		this.transitionManager = new TransitionManager(this);
		this.spectatorManager = new SpectatorManager(this);
		this.endgameManager = new EndgameManager(this);
	}

	public void initMap() {
		map = new TmxMapLoader().load(level.getMap());
	}

	@Override
	public void show() {

		//b/c the play state can get shown multiple times without getting removed, we must get rid of stage if already created
		if (stage == null) {
			this.stage = new Stage();
		}

		getUIManager().initUIElementsShow(stage);

		app.newMenu(stage);
		resetController();

		//if we faded out before transitioning to this stage, we should fade in upon showing
		if (FadeManager.getFadeLevel() >= 1.0f) {
			FadeManager.fadeIn();
		}

		//play track corresponding to map properties or a random song from the combat ost list
		MusicTrack newTrack;
		if (map.getProperties().get("music", String.class) != null) {
			newTrack =  MusicPlayer.playSong(MusicTrackType.getByName(
					map.getProperties().get("music", String.class)), 1.0f);
		} else {
			newTrack = MusicPlayer.playSong(MusicTrackType.MATCH, 1.0f);
		}

		if (newTrack != null) {
			MusicIcon icon = new MusicIcon(newTrack);
			stage.addActor(icon);
			icon.animateIcon();
		}
	}

	/**
	 * This method gives input to the player as well as the menu.
	 * This is called when a player is created.
	 */
	public void resetController() {
		
		//we check if we are in a playstate (not paused or in setting menu) b/c we don't reset control in those states
		if (!StateManager.states.empty()) {
			if (StateManager.states.peek() instanceof PlayState) {
				controller = new PlayerController(HadalGame.usm.getOwnPlayer());

				InputMultiplexer inputMultiplexer = new InputMultiplexer();
				inputMultiplexer.addProcessor(stage);
				inputMultiplexer.addProcessor(controller);
				inputMultiplexer.addProcessor(new CommonController(this));
				Gdx.input.setInputProcessor(inputMultiplexer);
			}
		}
	}
	
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
		if (server && !serverLoaded) {
	        serverLoaded = true;
			PacketManager.serverTCPAll(new Packets.ServerLoaded());
			BotManager.initiateBots(this);
			if (HadalGame.usm.getOwnUser() != null) {
				HadalGame.usm.getOwnUser().getTransitionManager().levelStartSpawn(this, reset);
			}
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
					PacketManager.serverTCPAll(packet);
				} else {
					PacketManager.serverUDPAll(packet);
				}
			}
		}
		createList.clear();

		//All entities that are set to be removed are removed.
		for (HadalEntity entity : removeList) {
			entity.dispose();

			for (ObjectSet<HadalEntity> s : entityLists) {
				s.remove(entity);
			}

			//Upon deleting an entity, tell the clients so they can follow suit.
			Object packet = entity.onServerDelete();
			if (packet != null) {
				if (entity.isReliableCreate()) {
					PacketManager.serverTCPAll(packet);
				} else {
					PacketManager.serverUDPAll(packet);
				}
			}
		}
		removeList.clear();

		//process all users (atm this handles respawn times so only server runs it)
		if (server) {
			for (User user : HadalGame.usm.getUsers().values()) {
				user.controller(this, delta);
			}
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
			boolean changeMade = false;
			for (User user : HadalGame.usm.getUsers().values()) {
				if (user.isScoreUpdated()) {
					changeMade = true;
					user.setScoreUpdated(false);

					ScoreManager score = user.getScoreManager();
					PacketManager.serverUDPAll(new Packets.SyncScore(user.getConnID(), user.getStringManager().getNameShort(),
							user.getLoadoutManager().getSavedLoadout(), score.getWins(), score.getKills(), score.getDeaths(),
							score.getAssists(), score.getScore(), score.getExtraModeScore(),
							score.getLives(), score.getCurrency(), user.getPing(), user.isSpectator()));
				}
			}
			if (changeMade) {
				getUIManager().getScoreWindow().syncScoreTable();
			}
		}
	}

	/**
	 * This method renders stuff to the screen after updating.
	 */
	@Override
	public void render(float delta) {
		getRenderManager().render(batch, delta);
	}
	
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

		if (isServer()) {
			//send server info to matchmaking server
			lobbySyncAccumulator += delta;
			if (lobbySyncAccumulator >= LOBBY_SYNC_TIME) {
				lobbySyncAccumulator = 0;
				if (HadalGame.socket != null) {
					if (HadalGame.socket.connected()) {
						JSONObject lobbyData = new JSONObject();
						try {
							lobbyData.put("playerNum", HadalGame.usm.getNumPlayers());
							lobbyData.put("playerCapacity", JSONManager.setting.getMaxPlayers() + 1);
							lobbyData.put("gameMode", mode.getName());
							lobbyData.put("gameMap", level.getName());
						} catch (JSONException jsonException) {
							Gdx.app.log("LOBBY", "FAILED TO SEND LOBBY INFO " + jsonException);
						}
						HadalGame.socket.emit("updateLobby", lobbyData.toString());
					}
				}
			}
		}

		if (!postGame) {
			//Increment the game timer, if exists
			getTimerManager().incrementTimer(delta);
			timer += delta;
			getCameraManager().controller(delta);
			getUIManager().controller();
		}
	}
	
	/**
	 * Run the controller method for all entities in the world
	 */
	public void controllerEntities(float delta) {
		for (ObjectSet<HadalEntity> s : entityLists) {
			for (HadalEntity entity : s) {
				entity.controller(delta);
				entity.getShaderHelper().decreaseShaderCount(delta);
				entity.increaseAnimationTime(delta);
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
			PacketManager.serverUDPAll(o);
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
		if (getRenderManager() != null) {
			getRenderManager().getWorldManager().dispose();
		}

		for (ObjectSet<HadalEntity> s : entityLists) {
			for (HadalEntity entity : s) {
				entity.dispose();
			}
		}

		world.dispose();
		map.dispose();

		if (stage != null) {
			stage.dispose();
		}

		if (camera != null) {
			CameraUtil.resetCameraRotation(camera);
		}
	}

	@Override
	public void resize() {
		getCameraManager().resize();
		getRenderManager().resize();
	}
	
	/**
	 * This looks for an entity in the world with the given entityID
	 * this is kinda slow. don't overuse it.
	 */
	public HadalEntity findEntity(int entityID) {
		for (ObjectSet<HadalEntity> s : entityLists) {
			for (HadalEntity entity : s) {
				if (entity.getEntityID() == entityID) {
					return entity;
				}
			}
		}
		return null;
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

	public Array<OrderedSet<HadalEntity>> getEntityLists() { return entityLists; }

	public boolean isServer() { return server; }

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

	public RenderManager getRenderManager() { return renderManager; }

	public CameraManager getCameraManager() { return cameraManager; }

	public UIManager getUIManager() { return uiManager; }

	public TimerManager getTimerManager() { return timerManager; }

	public SpawnManager getSpawnManager() { return spawnManager; }

	public TransitionManager getTransitionManager() { return transitionManager; }

	public SpectatorManager getSpectatorManager() { return spectatorManager; }

	public EndgameManager getEndgameManager() { return endgameManager; }

	public InputProcessor getController() { return controller; }

	public float getTimeModifier() { return timeModifier; }

	public void setTimeModifier(float timeModifier) { this.timeModifier = timeModifier; }

	public Array<Object> getSyncPackets() {	return syncPackets; }
}
