package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.*;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.*;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.audio.MusicTrackType;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.FrameBufferManager;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.event.hub.Wallpaper;
import com.mygdx.hadal.event.utility.PositionDummy;
import com.mygdx.hadal.handlers.WorldContactListener;
import com.mygdx.hadal.input.CommonController;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.FadeManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.SettingArcade;
import com.mygdx.hadal.map.SettingTeamMode.TeamMode;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.entities.*;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.PacketEffect;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.managers.CameraManager;
import com.mygdx.hadal.statuses.Blinded;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.users.User.UserDto;
import com.mygdx.hadal.utils.CameraUtil;
import com.mygdx.hadal.utils.TiledObjectUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mygdx.hadal.constants.Constants.PHYSICS_TIME;
import static com.mygdx.hadal.constants.Constants.PPM;
import static com.mygdx.hadal.managers.SkinManager.FONT_UI;
import static com.mygdx.hadal.users.Transition.*;

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

	//is this the server or client?
	private final boolean server;

	//Various play state ui elements. Some are initialixed right away while others require the stage to be made first.
	protected final UIArtifacts uiArtifact = new UIArtifacts(this);
	protected final UIExtra uiExtra = new UIExtra(this);
	protected final UIObjective uiObjective = new UIObjective(this);
	protected final UISpectator uiSpectator = new UISpectator(this);
	protected final ChatWheel chatWheel = new ChatWheel(this);
	protected final DialogBox dialogBox = new DialogBox(this);

	protected UIPlay uiPlay;
	protected UIHub uiHub;
	protected MessageWindow messageWindow;
	protected KillFeed killFeed;
	protected ScoreWindow scoreWindow;

	//Background and black screen used for transitions
	private final TextureRegion bg, white;
	private Shader shaderBase = Shader.NOTHING, shaderTile = Shader.NOTHING;
	
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
	
	//do we draw the hitbox lines?
	private boolean debugHitbox;

	//global variables.
	public static final float SPRITE_ANIMATION_SPEED_SLOW = 0.15f;
	public static final float SPRITE_ANIMATION_SPEED = 0.08f;
	public static final float SPRITE_ANIMATION_SPEED_FAST = 0.04f;
	public static final float SPRITE_ANIMATION_SPEED_SUPER_FAST = 0.02f;

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
		this.startID = startID;

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
				setSpectatorMode();
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

		//If ui elements have not been created, create them. (upon first showing the state)
		if (uiPlay == null) {
			uiPlay = new UIPlay(this);
			
			uiHub = new UIHub(this);
			messageWindow = new MessageWindow(this, stage);
			killFeed = new KillFeed(this);
			scoreWindow = new ScoreWindow(this);
		}

		//Add and sync ui elements in case of unpause or new playState
		stage.addActor(uiPlay);
		stage.addActor(uiObjective);
		stage.addActor(uiExtra);
		stage.addActor(dialogBox);
		stage.addActor(uiSpectator);

		chatWheel.addTable(stage);
		uiArtifact.addTable(stage);

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
				scoreWindow.syncScoreTable();
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
			uiExtra.incrementTimer(delta);
			timer += delta;

			cameraManager.controller(delta);
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
		
		cameraManager.resize();

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
	 * This is called when ending a playstate by winning, losing or moving to a new playstate
	 */	
	public void transitionState() {
		switch (nextState) {
		case RESPAWN:
			FadeManager.fadeIn();
			spectatorMode = false;
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case RESULTS:

			//create snapshot to use for transition to results state
			FrameBuffer fbo = resultsStateFreeze();

			//get a results screen
			StateManager.removeState(SettingState.class, false);
			StateManager.removeState(AboutState.class, false);
			StateManager.removeState(PauseState.class, false);
			StateManager.removeState(PlayState.class, false);
			StateManager.addResultsState(this, resultsText, LobbyState.class, fbo);
			StateManager.addResultsState(this, resultsText, TitleState.class, fbo);
			break;
		case SPECTATOR:
			//When ded but other players alive, spectate a player
			FadeManager.fadeIn();
			setSpectatorMode();
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case NEWLEVEL:
			
			//remove this state and add a new play state with a fresh loadout
			StateManager.removeState(SettingState.class, false);
			StateManager.removeState(AboutState.class, false);
			StateManager.removeState(PauseState.class, false);
			StateManager.removeState(PlayState.class, false);
			StateManager.addPlayState(app, nextLevel, nextMode, LobbyState.class, true, nextStartID);
			StateManager.addPlayState(app, nextLevel, nextMode, TitleState.class, true, nextStartID);
			break;
		case NEXTSTAGE:
			//remove this state and add a new play state with the player's current loadout and stats
			StateManager.removeState(SettingState.class, false);
			StateManager.removeState(AboutState.class, false);
			StateManager.removeState(PauseState.class, false);
			StateManager.removeState(PlayState.class, false);

			StateManager.addPlayState(app, nextLevel, nextMode, LobbyState.class, false, nextStartID);
			StateManager.addPlayState(app, nextLevel, nextMode, TitleState.class, false, nextStartID);
			break;
		case TITLE:
			StateManager.removeState(ResultsState.class);
			StateManager.removeState(SettingState.class, false);
			StateManager.removeState(AboutState.class, false);
			StateManager.removeState(PauseState.class, false);
			StateManager.removeState(PlayState.class);
			
			//add a notification to the title state if specified in transition state
			if (!StateManager.states.isEmpty()) {
				if (StateManager.states.peek() instanceof TitleState titleState) {
					titleState.setNotification(resultsText);
				}
				if (StateManager.states.peek() instanceof LobbyState lobbyState) {
					lobbyState.setNotification(resultsText);
				}
			}
			break;
		default:
			break;
		}
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
		if (!server) { return; }

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

		//process spawn overrides if the user specifies being spawned at a set location instead of at a start point
		if (isServer()) {
			if (user.getTransitionManager().isSpawnOverridden()) {
				overiddenSpawn.set(user.getTransitionManager().getOverrideSpawnLocation());
			}
		}

		Player p;
		if (user.getConnID() < 0) {
			p = new PlayerBot(this, overiddenSpawn, name, old, user, reset, spawn);
		} else if (isServer()) {
			if (0 == user.getConnID()) {
				p = new Player(this, overiddenSpawn, name, old, user, reset, spawn);
			} else {
				p = new PlayerClientOnHost(this, overiddenSpawn, name, old, user, reset, spawn);
			}
		} else {
			if (!client) {
				p = new Player(this, overiddenSpawn, name, old, user, reset, spawn);
			} else {
				p = new PlayerSelfOnClient(this, overiddenSpawn, name, null, user, reset, spawn);
			}
		}

		//teleportation particles for reset players (indicates returning to hub)
		if (reset && isServer() && user.getEffectManager().isShowSpawnParticles()) {
			new ParticleEntity(this, new Vector2(p.getStartPos()).sub(0, p.getSize().y / 2),
					Particle.TELEPORT, 2.5f, true, SyncType.CREATESYNC).setPrematureOff(1.5f);
		}

		//for own player, the server must update their user information
		if (isServer() && user.getConnID() == 0) {
			HadalGame.usm.getOwnUser().setPlayer(p);
		}
		user.setPlayer(p);

		//mode-specific player modifications
		mode.modifyNewPlayer(this, newLoadout, p, hitboxFilter);
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
	 */
	public void levelEnd(String text, boolean victory, float fadeDelay) {
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
						score.win();
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
						score.win();
					}
				}
			}
		} else if (victory) {
			//in coop, all players get a win if the team wins
            for (User user : activeUsers) {
				user.getScoreManager().win();
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
		HadalGame.server.sendToAllTCP(new Packets.SyncExtraResultsInfo(users, resultsText));

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

	private static final float END_TEXT_Y = 700;
	private static final float END_TEXT_WIDTH = 400;
	private static final float END_TEXT_SCALE = 2.5f;
	/**
	 * @return a snapshot of the player's current perspective. Used for transitioning to results state
	 */
	protected FrameBuffer resultsStateFreeze() {
		FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA4444, 1280, 720, false);
		fbo.begin();

		//clear buffer, set camera
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());

		render(1.0f / 60.0f);

		//draw extra ui elements for snapshot
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		FONT_UI.getData().setScale(END_TEXT_SCALE);
		FONT_UI.draw(batch, UIText.GAME.text(),HadalGame.CONFIG_WIDTH / 2 - END_TEXT_WIDTH / 2, END_TEXT_Y, END_TEXT_WIDTH,
				Align.center, true);
		batch.end();

		fbo.end();

		return fbo;
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
		HadalGame.server.sendToTCP(user.getConnID(),
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
	 * This is called whenever we transition to a new state. Begin transition and set new state.
	 * @param state: The state we are transitioning towards
	 * @param fadeSpeed: speed of transition
	 * @param fadeDelay: amount of delay before transition
	 */
	public void beginTransition(TransitionState state, float fadeSpeed, float fadeDelay, boolean skipFade) {
		//If we are already transitioning to a new results state, do not do this unless we tell it to override
		if (!skipFade) {
			FadeManager.fadeSpecificSpeed(fadeSpeed, fadeDelay);
			FadeManager.setRunAfterTransition(this::transitionState);

			//null nextState is used by user transition for non-timed respawn
			if (null != state) {
				nextState = state;
			} else {
				nextState = TransitionState.RESPAWN;
			}
		} else {
			spectatorMode = false;
			nextState = null;
		}

		//fadeSpeed = 0 means we skip the fade. Only relevant during special transitions
		if (TransitionState.RESPAWN.equals(state) && !skipFade && fadeDelay != 0.0f) {
			killFeed.addKillInfo(fadeDelay + 1.0f / fadeSpeed);
		}
	}
	
	/**
	 * Return to the title screen after a disconnect or selecting return in the pause menu. Overrides other transitions.
	 */
	public void returnToTitle(float delay) {
		if (server) {
			if (HadalGame.server.getServer() != null) {
				HadalGame.server.getServer().stop();
			}
		} else {
			HadalGame.client.getClient().stop();
		}

		beginTransition(TransitionState.TITLE, DEFAULT_FADE_OUT_SPEED, delay, false);
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
	 * This sets the game's boss, filling the boss ui.
	 * @param enemy: This is the boss whose hp will be used for the boss hp bar
	 */
	public void setBoss(Enemy enemy) {
		uiPlay.setBoss(enemy, enemy.getName());
		uiExtra.setBoss();
	}
	
	/**
	 * This is called when the boss is defeated, clearing its hp bar from the ui.
	 * We also have to tell the client to do the same.
	 */
	public void clearBoss() {
		uiPlay.clearBoss();
		uiExtra.clearBoss();
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
	 * This acquires the level's save points. If none, respawn at starting location. If many, choose one randomly
	 * @return a save point to spawn a respawned player at
	 */
	public StartPoint getSavePoint(String startID, User user) {

		if (!isServer()) { return null; }

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
	 * Player enters spectator mode. Set up spectator camera and camera bounds
	 */
	public void setSpectatorMode() {
		spectatorMode = true;
		uiSpectator.enableSpectatorUI();

		cameraManager.setSpectator();

		//this makes the player's artifacts disappear as a spectator
		uiArtifact.syncArtifact();
	}
	
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

	public boolean isServer() { return server; }

	public boolean isReset() { return reset; }

	public boolean isSpectatorMode() { return spectatorMode; }

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

	public UIPlay getUiPlay() { return uiPlay; }

	public UIExtra getUiExtra() { return uiExtra; }

	public UIArtifacts getUiArtifact() { return uiArtifact; }
	
	public UIHub getUiHub() { return uiHub; }
	
	public UIObjective getUiObjective() { return uiObjective; }

	public UISpectator getUiSpectator() { return uiSpectator; }

	public PositionDummy getDummyPoint(String id) {	return dummyPoints.get(id); }
	
	public void addDummyPoint(PositionDummy dummy, String id) {	dummyPoints.put(id, dummy); }
	
	public CameraManager getCameraManager() { return cameraManager; }

	public InputProcessor getController() { return controller; }

	public MessageWindow getMessageWindow() { return messageWindow; }

	public KillFeed getKillFeed() { return killFeed; }

	public ChatWheel getChatWheel() { return chatWheel; }
	
	public ScoreWindow getScoreWindow() { return scoreWindow; }	
	
	public DialogBox getDialogBox() { return dialogBox; }

	public float getTimeModifier() { return timeModifier; }

	public void setTimeModifier(float timeModifier) { this.timeModifier = timeModifier; }

	public Array<Object> getSyncPackets() {	return syncPackets; }

	public void setNextState(TransitionState nextState) { this.nextState = nextState; }

	public TransitionState getNextState() { return nextState; }

	public void setNextLevel(UnlockLevel nextLevel) { this.nextLevel = nextLevel; }

	public void setNextMode(GameMode nextMode) { this.nextMode = nextMode; }

	public void setResultsText(String resultsText) { this.resultsText = resultsText; }

	public void setLevelEnded(boolean levelEnded) { this.levelEnded = levelEnded; }
}
