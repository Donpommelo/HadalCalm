package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
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
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.FrameBufferManager;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.hub.Wallpaper;
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
import com.mygdx.hadal.statuses.Blinded;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.CameraUtil;
import com.mygdx.hadal.utils.TiledObjectUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mygdx.hadal.constants.Constants.PHYSICS_TIME;
import static com.mygdx.hadal.constants.Constants.PPM;

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
	protected final TiledMap map;
	protected final OrthogonalTiledMapRenderer tmr;
	
	//world manages the Box2d world and physics. b2dr renders debug lines for testing
	protected final Box2DDebugRenderer b2dr;
	protected final World world;

	private final CameraManager cameraManager;
	private final UIManager uiManager;
	private final TimerManager timerManager;

	private final SpawnManager spawnManager;
	private final TransitionManager transitionManager;
	private final SpectatorManager spectatorManager;
	private final EndgameManager endgameManager;

	//These represent the set of entities to be added to/removed from the world. This is necessary to ensure we do this between world steps.
	private final OrderedSet<HadalEntity> removeList = new OrderedSet<>();
	private final OrderedSet<HadalEntity> createList = new OrderedSet<>();

	//These sets are used by the Client for removing/adding entities.
	protected final OrderedSet<UUID> removeListClient = new OrderedSet<>();
	protected final OrderedSet<ClientState.CreatePacket> createListClient = new OrderedSet<>();

	//This is a set of all non-hitbox entities in the world
	private final OrderedSet<HadalEntity> entities = new OrderedSet<>();
	//This is a set of all hitboxes. This is separate to draw hitboxes underneath other bodies
	private final OrderedSet<HadalEntity> hitboxes = new OrderedSet<>();
	//This is a set of all particle effects. This is separate to draw effects above other bodies
	private final OrderedSet<HadalEntity> effects = new OrderedSet<>();

	private final Array<OrderedSet<HadalEntity>> entityLists = new Array<>();

	//this maps shaders to all current entities using them so they can be rendered in a batch
	private final ObjectMap<Shader, Array<HadalEntity>> dynamicShaderEntities = new ObjectMap<>();
	private final ObjectMap<Shader, Array<HadalEntity>> staticShaderEntities = new ObjectMap<>();

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

	//Background and black screen used for transitions
	private final TextureRegion bg, white;
	private Shader shaderBase = Shader.NOTHING, shaderTile = Shader.NOTHING;
	
	//Has the server finished loading yet?
	private boolean serverLoaded;
	
	//Do players connecting to this have their hp/ammo/etc reset?
	private final boolean reset;
	
	//do we draw the hitbox lines?
	private boolean debugHitbox;

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

		b2dr = new Box2DDebugRenderer();
		
		//Initialize sets to keep track of active entities and packet effects
		entityLists.add(hitboxes);
		entityLists.add(entities);
		entityLists.add(effects);

		PlayState me = this;
		//load map. We override the render so that we can apply a shader to the tileset
		map = new TmxMapLoader().load(level.getMap());
		tmr = new OrthogonalTiledMapRenderer(map, batch) {

			@Override
			public void render() {
				beginRender();

				if (shaderTile.getShaderProgram() != null) {
					batch.setShader(shaderTile.getShaderProgram());
					shaderTile.shaderPlayUpdate(me, timer);
					shaderTile.shaderDefaultUpdate(timer);
				}

				for (MapLayer layer : map.getLayers()) {
					renderMapLayer(layer);
				}

				if (shaderTile.getShaderProgram() != null) {
					if (shaderTile.isBackground()) {
						batch.setShader(null);
					}
				}

				endRender();
			}
		};

		this.cameraManager = new CameraManager(this, map);
		this.uiManager = new UIManager(this);
		this.timerManager = new TimerManager(this);

		this.spawnManager = new SpawnManager(this, startID);
		this.transitionManager = new TransitionManager(this);
		this.spectatorManager = new SpectatorManager(this);
		this.endgameManager = new EndgameManager(this);

		//We clear things like music/sound/shaders to periodically free up some memory
		StateManager.clearMemory();

		//we clear shaded cosmetics to avoid having too many cached fbos
		UnlockCosmetic.clearShadedCosmetics();

		//The "worldDummy" will be the source of map-effects that want a perp (create after clearing memory b/c it creates an impact particle)
		worldDummy = new WorldDummy(this);

		//anchor is used to attach "static" entities without making them static
		anchor = new AnchorPoint(this);

		if (map.getProperties().get("customShader", false, Boolean.class)) {
			shaderBase = Wallpaper.SHADERS[JSONManager.setting.getCustomShader()];
			shaderBase.loadShader();
		} else if (map.getProperties().get("shader", String.class) != null) {
			shaderBase = Shader.valueOf(map.getProperties().get("shader", String.class));
			shaderBase.loadShader();
		}
		
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
			if (HadalGame.usm.getOwnUser().isSpectator()) {
				getSpectatorManager().setSpectatorMode();
			}
		}

		this.reset = reset;

		//Init background image
		this.bg = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.BACKGROUND2.toString()));
		this.white = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.WHITE.toString()));

		//set whether to draw hitbox debug lines or not
		debugHitbox = JSONManager.setting.isDebugHitbox();
	}
			
	@Override
	public void show() {

		//b/c the play state can get shown multiple times without getting removed, we must get rid of stage if already created
		if (stage == null) {
			this.stage = new Stage();
		}

		getUIManager().initUIElements(stage);

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
			HadalGame.server.sendToAllTCP(new Packets.ServerLoaded());
			BotManager.initiateBots(this);
			HadalGame.usm.getOwnUser().getTransitionManager().levelStartSpawn(this, reset);
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
					HadalGame.server.sendToAllTCP(packet);
				} else {
					HadalGame.server.sendToAllUDP(packet);
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
					HadalGame.server.sendToAllTCP(packet);
				} else {
					HadalGame.server.sendToAllUDP(packet);
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
					HadalGame.server.sendToAllUDP(new Packets.SyncScore(user.getConnID(), user.getStringManager().getNameShort(),
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
		//Render Background
		batch.setProjectionMatrix(hud.combined);
		batch.disableBlending();
		batch.begin();

		//render shader
		if (shaderBase.getShaderProgram() != null) {
			batch.setShader(shaderBase.getShaderProgram());
			shaderBase.shaderPlayUpdate(this, timer);
			shaderBase.shaderDefaultUpdate(timer);
		}

		batch.draw(bg, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
		
		if (shaderBase.getShaderProgram() != null) {
			if (shaderBase.isBackground()) {
				batch.setShader(null);
			}
		}

		batch.end();
		batch.enableBlending();

		//Render Tiled Map + world
		tmr.setView(camera);
		tmr.render();

		//Render debug lines for box2d objects. THe 0 check prevents debug outlines from appearing in the freeze-frame
		if (debugHitbox && 0.0f != delta) {
			b2dr.render(world, camera.combined.scl(PPM));
			camera.combined.scl(1.0f / PPM);
		}
		
		//Iterate through entities in the world to render visible entities
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		Particle.drawParticlesBelow(batch, delta);
		renderEntities();
		Particle.drawParticlesAbove(batch, delta);

		if (shaderBase.getShaderProgram() != null) {
			if (!shaderBase.isBackground()) {
				batch.setShader(null);
			}
		}

		batch.end();

		//add white filter if the player is blinded
		if (null != HadalGame.usm.getOwnPlayer()) {
			if (HadalGame.usm.getOwnPlayer().getBlinded() > 0.0f) {
				batch.setProjectionMatrix(hud.combined);
				batch.begin();

				batch.setColor(1.0f, 1.0f, 1.0f, Blinded.getBlindAmount(HadalGame.usm.getOwnPlayer().getBlinded()));
				batch.draw(white, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
				batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);

				batch.end();
			}
		}
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
		}
	}
	
	/**
	 * Render all entities in the world
	 */
	public void renderEntities() {
		for (ObjectSet<HadalEntity> s : entityLists) {
			for (HadalEntity entity : s) {
				renderEntity(entity);
			}
		}
		renderShadedEntities();
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
			HadalGame.server.sendToAllUDP(o);
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

	private final Vector2 entityLocation = new Vector2();
	/**
	 * This method renders a single entity.
	 * @param entity: the entity we are rendering
	 */
	public void renderEntity(HadalEntity entity) {
		entityLocation.set(entity.getPixelPosition());
		if (entity.isVisible(entityLocation)) {

			//for shaded entities, add them to a map instead of rendering right away so we can render them at once
			if (entity.getShaderStatic() != null && entity.getShaderStatic() != Shader.NOTHING) {
				Array<HadalEntity> shadedEntities = staticShaderEntities.get(entity.getShaderStatic());
				if (null == shadedEntities) {
					shadedEntities = new Array<>();
					staticShaderEntities.put(entity.getShaderStatic(), shadedEntities);
				}
				shadedEntities.add(entity);
			} else if (entity.getShaderHelper().getShader() != null && entity.getShaderHelper().getShader() != Shader.NOTHING) {
				Array<HadalEntity> shadedEntities = dynamicShaderEntities.get(entity.getShaderHelper().getShader());
				if (null == shadedEntities) {
					shadedEntities = new Array<>();
					dynamicShaderEntities.put(entity.getShaderHelper().getShader(), shadedEntities);
				}
				shadedEntities.add(entity);
			} else {
				entity.render(batch, entityLocation);
			}
		}
	}

	/**
	 * This renders shaded entities so we can minimize shader switches
	 */
	public void renderShadedEntities() {

		//do same thing for static shaders
		for (ObjectMap.Entry<Shader, Array<HadalEntity>> entry : staticShaderEntities) {

			//we sometimes set static shaders without loading them (overrided static shaders that are conditional)
			if (null == entry.key.getShaderProgram()) {
				entry.key.loadStaticShader();
			}
			batch.setShader(entry.key.getShaderProgram());
			for (HadalEntity entity : entry.value) {
				entityLocation.set(entity.getPixelPosition());
				entity.render(batch, entityLocation);

				if (entity.getShaderHelper().getShaderStaticCount() <= 0.0f) {
					entity.getShaderHelper().setStaticShader(Shader.NOTHING, 0.0f);
				}
			}
		}
		staticShaderEntities.clear();

		for (ObjectMap.Entry<Shader, Array<HadalEntity>> entry : dynamicShaderEntities) {
			//for each shader, render all entities using it at once so we only need to set it once
			batch.setShader(entry.key.getShaderProgram());
			for (HadalEntity entity : entry.value) {
				entityLocation.set(entity.getPixelPosition());

				//unlike static shaders, dynamic shaders need controller updated
				entity.getShaderHelper().processShaderController(timer);
				entity.render(batch, entityLocation);

				if (entity.getShaderHelper().getShaderCount() <= 0.0f) {
					entity.getShaderHelper().setShader(Shader.NOTHING, 0.0f);
				}
			}
		}
		dynamicShaderEntities.clear();

		batch.setShader(null);
	}
	
	/**
	 * This is called upon exiting. Dispose of all created fields.
	 */
	@Override
	public void dispose() {
		b2dr.dispose();

		for (ObjectSet<HadalEntity> s : entityLists) {
			for (HadalEntity entity : s) {
				entity.dispose();
			}
		}
		for (HadalEntity entity : removeList) {
			entity.dispose();
		}
		
		world.dispose();
		tmr.dispose();
		map.dispose();
		if (stage != null) {
			stage.dispose();
		}
		CameraUtil.resetCameraRotation(camera);
	}

	@Override
	public void resize() {
		
		getCameraManager().resize();

		if (shaderBase.getShaderProgram() != null) {
			shaderBase.getShaderProgram().bind();
			shaderBase.shaderResize();
		}

		if (shaderTile.getShaderProgram() != null) {
			shaderTile.getShaderProgram().bind();
			shaderTile.shaderResize();
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
					HadalGame.server.sendToUDP(connID, packet);
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
	
	/**
	 * This sets a shader to be used as a "base-shader" for things like the background
	 */
	public void setShaderBase(Shader shader) {
		shaderBase = shader;
		shaderBase.loadShader();
	}

	public void setShaderTile(Shader shader) {
		shaderTile = shader;
		shaderTile.loadShader();
	}

	public float getRespawnTime(Player p) {
		return respawnTime * (1.0f + p.getPlayerData().getStat(Stats.RESPAWN_TIME));
	}

	/**
	 * Z-Axis Layers that entities can be added to. ATM, there is just 1 for hitboxes beneath everything else.
	 */
	public enum ObjectLayer {
		STANDARD,
		HBOX,
		EFFECT
	}

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

	public void toggleVisibleHitboxes(boolean debugHitbox) { this.debugHitbox = debugHitbox; }

	public PositionDummy getDummyPoint(String id) {	return dummyPoints.get(id); }
	
	public void addDummyPoint(PositionDummy dummy, String id) {	dummyPoints.put(id, dummy); }
	
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
