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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.*;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.*;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.audio.MusicTrackType;
import com.mygdx.hadal.bots.BotManager;
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
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.SettingTeamMode.TeamMode;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.*;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.SavedPlayerFieldsExtra;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.User.UserDto;
import com.mygdx.hadal.server.packets.PacketEffect;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.statuses.Blinded;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.utils.CameraUtil;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.TiledObjectUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * The PlayState is the main state of the game and holds the Box2d world, all characters + gameplay.
 * @author Norroway Nigganov
 */
public class PlayState extends GameState {
	
	//This is an entity representing the player's controlled entity.
	protected Player player;
	
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

	//This holds the mouse location
	private final MouseTracker mouse;

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
	private final String startId;
	
	//This is the coordinate that the camera tries to focus on when set to aim at an entity. When null, the camera focuses on the player.
	private Vector2 cameraTarget;
	
	//the camera offset from the target by this vector. (this is pretty much only used when focusing on the player)
	private final Vector2 cameraOffset = new Vector2();
	
	//coordinate the camera is looking at in spectator mode.
	private final Vector2 spectatorTarget = new Vector2();
	
	//are we currently a spectator or not?
	protected boolean spectatorMode;
	
	//These are the bounds of the camera movement. We make the numbers really big so that the default is no bounds.
	private final float[] cameraBounds = {100000.0f, -100000.0f, 100000.0f, -100000.0f};
	private final float[] spectatorBounds = {100000.0f, -100000.0f, 100000.0f, -100000.0f};
	
	//are the spectator bounds distinct from the camera bounds? (relevant mostly for 1-p maps with multiple sets of bounds)
	private boolean spectatorBounded;

	//The current zoom of the camera and the zoom that the camera will lerp towards
	private float zoom;
	protected float zoomDesired;
	
	//If a player respawns, they will respawn at the coordinates of a safe point from this list.
	private final Array<StartPoint> savePoints = new Array<>();
	
	//This is an arrayList of ids to dummy events. These are used for enemy ai processing
	private final ObjectMap<String, PositionDummy> dummyPoints = new ObjectMap<>();
	
	private float timeModifier = 1.0f;
	private float respawnTime = 1.5f;
	private final boolean server;

	//Various play state ui elements
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
	private String nextStartId;
	
	//If we are transitioning to a results screen, this is the displayed text;
	protected String resultsText = "";
	
	//Has the server finished loading yet?
	private boolean serverLoaded;
	
	//Do players connecting to this have their hp/ammo/etc reset?
	private final boolean reset;
	
	//do we draw the hitbox lines?
	private boolean debugHitbox;

	//global variables.
	public static final float spriteAnimationSpeedSlow = 0.15f;
	public static final float spriteAnimationSpeed = 0.08f;
	public static final float spriteAnimationSpeedFast = 0.04f;
	
	public static final float defaultFadeOutSpeed = 2.0f;
	public static final float defaultFadeDelay = 0.0f;
	public static final float shortFadeDelay = 0.5f;
	public static final float longFadeDelay = 1.5f;

	//Special designated events parsed from map. Event run when a spectating host presses their interact button
	private Event globalTimer, spectatorActivation;
	
	/**
	 * Constructor is called upon player beginning a game.
	 * @param gsm: StateManager
	 * @param loadout: the loadout that the player should start with
	 * @param level: the level we are loading into
	 * @param mode: the mode of the level we are loading into
	 * @param server: is this the server or not?
	 * @param old: the data of the previous player (this exists if this play state is part of a stage transition with an existing player)
	 * @param reset: do we reset the old player's hp/fuel/ammo in the new playstate?
	 * @param startId: the id of the starting event the player should be spawned at
	 */
	public PlayState(GameStateManager gsm, Loadout loadout, UnlockLevel level, GameMode mode,
					 boolean server, PlayerBodyData old, boolean reset, String startId) {
		super(gsm);
		this.level = level;
		this.mode = mode;
		this.server = server;
		this.startId = startId;
        
        //Initialize box2d world and related stuff
		world = new World(new Vector2(0, -9.81f), true);
		world.setContactListener(new WorldContactListener());
		World.setVelocityThreshold(0);

		b2dr = new Box2DDebugRenderer();
		
		//Initialize sets to keep track of active entities and packet effects
		entityLists.add(hitboxes);
		entityLists.add(entities);
		entityLists.add(effects);

		//The "worldDummy" will be the source of map-effects that want a perpetrator
		worldDummy = new WorldDummy(this);
		anchor = new AnchorPoint(this);
		
		//The mouse tracker is the player's mouse position
		mouse = new MouseTracker(this, true);

		PlayState me = this;
		//load map. We override the render so that we can apply a shader to the tileset
		map = new TmxMapLoader().load(level.getMap());
		tmr = new OrthogonalTiledMapRenderer(map, batch) {

			@Override
			public void render () {
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
		this.zoom = map.getProperties().get("zoom", 1.0f, float.class);
		this.zoomDesired = zoom;

		//We clear things like music/sound/shaders to periodically free up some memory
		GameStateManager.clearMemory();

		if (map.getProperties().get("customShader", false, Boolean.class)) {
			shaderBase = Wallpaper.shaders[gsm.getSetting().getCustomShader()];
			shaderBase.loadShader();
		} else if (map.getProperties().get("shader", String.class) != null) {
			shaderBase = Shader.valueOf(map.getProperties().get("shader", String.class));
			shaderBase.loadShader();
		}
		
		//Clear events in the TiledObjectUtil to avoid keeping reference to previous map's events.
		TiledObjectUtil.clearEvents();

		//Only the server processes collision objects, events and triggers
		if (server) {
			TiledObjectUtil.parseTiledObjectLayer(this, map.getLayers().get("collision-layer").getObjects());
			TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get("event-layer").getObjects());

			//parse map-specific event layers (used for different modes in the same map)
			for (String layer: mode.getExtraLayers()) {
				if (map.getLayers().get(layer) != null) {
					TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get(layer).getObjects());
				}
			}

			//Server must first reset each score at the start of a level (unless just a stage transition)
			if (reset) {
				for (User user : HadalGame.server.getUsers().values()) {
					user.newLevelReset();
					user.getScoresExtra().newLevelReset();
				}
			}

			mode.processSettings(this);

			TiledObjectUtil.parseTiledTriggerLayer();
			TiledObjectUtil.parseDesignatedEvents(this);
		}

		//if auto-assign team is on, we do the assignment here
		if (mode.getTeamMode().equals(TeamMode.TEAM_AUTO) && isServer()) {
			AlignmentFilter.autoAssignTeams(mode.getTeamNum(), mode.getTeamMode());
		} else if (mode.getTeamMode().equals(TeamMode.HUMANS_VS_BOTS) && isServer()) {
			AlignmentFilter.autoAssignTeams(2, mode.getTeamMode());
		} else {
			AlignmentFilter.resetTeams();
		}

		//Create the player and make the camera focus on it
		if (server) {
			User user = HadalGame.server.getUsers().get(0);
			StartPoint getSave = getSavePoint(startId, user);
			short hitboxFilter = user.getHitBoxFilter().getFilter();

			//if the host is a spectator, just set their player to their old one instead of creating a new one.
			if (user.isSpectator()) {
				this.player = user.getPlayer();

				//setting the old player's state is a janky way of getting its old input processor to register new state's events
				this.player.setState(this);
				setSpectatorMode();
			} else {
				this.player = createPlayer(getSave, gsm.getLoadout().getName(), loadout, old, 0, user, reset,
						false, false, hitboxFilter);
			}

			if (getSave != null) {
				this.camera.position.set(new Vector3(getSave.getStartPos().x, getSave.getStartPos().y, 0));
				this.cameraFocusAim.set(getSave.getStartPos());
			}
		} else {
			this.player = createPlayer(null, gsm.getLoadout().getName(), loadout, old, 0, null, reset,
					false, false, Constants.PLAYER_HITBOX);
		}

		this.reset = reset;

		//Init background image
		this.bg = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.BACKGROUND2.toString()));
		this.white = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.WHITE.toString()));

		//set whether to draw hitbox debug lines or not
		debugHitbox = gsm.getSetting().isDebugHitbox();
	}
			
	@Override
	public void show() {

		//b/c the play state can get shown multiple times without getting removed, we must get rid of stage if already created
		if (stage == null) {
			this.stage = new Stage();
		}

		//If ui elements have not been created, create them. (upon first showing the state)
		if (uiPlay == null) {
			if (server) {
				uiPlay = new UIPlay(this);
			} else {
				uiPlay = new UIPlayClient(this);
			}
			
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
		if (gsm.getApp().getFadeLevel() >= 1.0f) {
			gsm.getApp().fadeIn();
		}

		MusicTrack newTrack;
		if (map.getProperties().get("music", String.class) != null) {
			newTrack =  HadalGame.musicPlayer.playSong(MusicTrackType.getByName(
					map.getProperties().get("music", String.class)), 1.0f);
		} else {
			newTrack = HadalGame.musicPlayer.playSong(MusicTrackType.MATCH, 1.0f);
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
		if (!gsm.getStates().empty()) {
			if (gsm.getStates().peek() instanceof PlayState) {
				controller = new PlayerController(player);
				
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
	private static final float physicsTime = 0.005f;
	
	//these control the frequency that we send sync packets for world entities.
	private float syncAccumulator;
	private float syncFastAccumulator;
	public static final float syncTime = 0.05f;
	public static final float syncFastTime = 1 / 60f;
	public static final float syncInterpolation = 0.125f;
	
	protected float scoreSyncAccumulator, lobbySyncAccumulator;
	protected static final float scoreSyncTime = 1.0f;
	protected static final float lobbySyncTime = 15.0f;

	private float timer;
	/**
	 * Every engine tick, the GameState must process all entities in it according to the time elapsed.
	 */
	@Override
	public void update(float delta) {

		float modifiedDelta = delta * timeModifier;

		//On the very first tick, server tells all clients that it is loaded
		if (server && !serverLoaded) {
	        serverLoaded = true;
			HadalGame.server.sendToAllTCP(new Packets.ServerLoaded());
			BotManager.initiateBots(this);
		}

		//this makes the physics separate from the game framerate
		physicsAccumulator += modifiedDelta;
		while (physicsAccumulator >= physicsTime) {
			physicsAccumulator -= physicsTime;

			//The box2d world takes a step. This handles collisions + physics stuff.
			world.step(physicsTime, 8, 3);
		}

		//All entities that are set to be added are added.
		for (HadalEntity entity: createList) {
			if (entity instanceof Hitbox) {
				hitboxes.add(entity);
			} else if (entity instanceof ParticleEntity) {
				effects.add(entity);
			} else {
				entities.add(entity);
			}
			entity.create();
			//Upon creating an entity, tell the clients so they can follow suit (if the entity calls for it)
			Object packet = entity.onServerCreate(false);
			if (packet != null) {
				HadalGame.server.sendToAllUDP(packet);
			}
		}
		createList.clear();

		//All entities that are set to be removed are removed.
		for (HadalEntity entity: removeList) {
			for (ObjectSet<HadalEntity> s: entityLists) {
				s.remove(entity);
			}
			entity.dispose();

			//Upon deleting an entity, tell the clients so they can follow suit.
			Object packet = entity.onServerDelete();
			if (packet != null) {
				HadalGame.server.sendToAllUDP(packet);
			}
		}
		removeList.clear();

		//process all users (atm this handles respawn times)
		if (server) {
			for (User user: HadalGame.server.getUsers().values()) {
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
			if (syncAccumulator >= syncTime) {
				syncAccumulator = 0;
				syncEntities();
			}
			syncFastAccumulator += delta;
			if (syncFastAccumulator >= syncFastTime) {
				syncFastAccumulator = 0;
				syncFastEntities();
			}
		}
		
		//send periodic sync packets for score
		scoreSyncAccumulator += delta;
		if (scoreSyncAccumulator >= scoreSyncTime) {
			scoreSyncAccumulator = 0;
			boolean changeMade = false;
			for (User user : HadalGame.server.getUsers().values()) {
				if (user.isScoreUpdated()) {
					changeMade = true;
					user.setScoreUpdated(false);
				}
				SavedPlayerFields score = user.getScores();
				HadalGame.server.sendToAllUDP(new Packets.SyncScore(user.getScores().getConnID(), score.getNameShort(), score.getWins(),
					score.getKills(), score.getDeaths(), score.getScore(), score.getLives(), score.getPing(), user.isSpectator()));
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

		//Render debug lines for box2d objects.
		if (debugHitbox) {
			b2dr.render(world, camera.combined.scl(PPM));
			camera.combined.scl(1.0f / PPM);
		}
		
		//Iterate through entities in the world to render visible entities
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		renderEntities();

		if (shaderBase.getShaderProgram() != null) {
			if (!shaderBase.isBackground()) {
				batch.setShader(null);
			}
		}

		batch.end();

		//add white filter if the player is blinded
		if (player.getBlinded() > 0.0f) {
			batch.setProjectionMatrix(hud.combined);
			batch.begin();

			batch.setColor(1.0f, 1.0f, 1.0f, Blinded.getBlindAmount(player.getBlinded()));
			batch.draw(white, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);

			batch.end();
		}
	}
	
	/**
	 * This is run in the update method and is just a helper to avoid repeating code in both the server/client states.
	 * This does all of the stuff that is needed for both server and client (processing packets, fade and some other misc stuff)
	 * postgame is used so that the state can continue processing packets in the results state
	 */
	private float cameraAccumulator;
	private static final float cameraTime = 1 / 120f;
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
			if (lobbySyncAccumulator >= lobbySyncTime) {
				lobbySyncAccumulator = 0;
				if (HadalGame.socket != null) {
					if (HadalGame.socket.connected()) {
						JSONObject lobbyData = new JSONObject();
						try {
							lobbyData.put("playerNum", HadalGame.server.getNumPlayers());
							lobbyData.put("playerCapacity", gsm.getSetting().getMaxPlayers() + 1);
						} catch (JSONException jsonException) {
							Gdx.app.log("LOBBY", "FAILED TO SEND LOBBY INFO " + jsonException);
						}
						HadalGame.socket.emit("updateLobby", lobbyData.toString());
					}
				}
			}
		}

		if (!postGame) {
			//Update the game camera.
			cameraAccumulator += delta;
			while (cameraAccumulator >= cameraTime) {
				cameraAccumulator -= cameraTime;
				cameraUpdate();
			}

			//Increment the game timer, if exists
			uiExtra.incrementTimer(delta);
			timer += delta;
		}
	}
	
	/**
	 * Render all entities in the world
	 */
	public void renderEntities() {
		for (ObjectSet<HadalEntity> s: entityLists) {
			for (HadalEntity entity : s) {
				renderEntity(entity);
			}
		}
	}
	
	/**
	 * Run the controller method for all entities in the world
	 */
	public void controllerEntities(float delta) {
		for (ObjectSet<HadalEntity> s: entityLists) {
			for (HadalEntity entity : s) {
				entity.controller(delta);
				entity.decreaseShaderCount(delta);
				entity.increaseAnimationTime(delta);
				entity.increaseEntityAge(delta);
			}
		}
	}
	
	/**
	 * This sends a synchronization packet for every synced entity. syncFastEntities() is used for entities that are synced more frequently
	 */
	private final Array<Object> syncPackets = new Array<>();
	public void syncEntities() {

		for (ObjectSet<HadalEntity> s: entityLists) {
			for (HadalEntity entity : s) {
				entity.onServerSync();
			}
		}
		for (Object o: syncPackets) {
			HadalGame.server.sendToAllUDP(o);
		}
		syncPackets.clear();
	}
	
	public void syncFastEntities() {
		for (ObjectSet<HadalEntity> s: entityLists) {
			for (HadalEntity entity : s) {
				entity.onServerSyncFast();
			}
		}
	}
	
	/**
	 * This method renders a single entity.
	 * @param entity: the entity we are rendering
	 */
	public void renderEntity(HadalEntity entity) {

		if (entity.isVisible()) {
			if (entity.getShaderCount() > 0 && entity.getShader() != null) {
				batch.setShader(entity.getShader().getShaderProgram());
				entity.processShaderController(timer);
			}

			entity.render(batch);

			if (entity.getShaderCount() > 0 && entity.getShader() != null) {
				batch.setShader(null);
			}
		}
	}
	
	/**
	 * This is called every update. This resets the camera zoom and makes it move towards the player (or other designated target).
	 */
	private static final float mouseCameraTrack = 0.5f;
	private static final float cameraInterpolation = 0.08f;
	private static final float cameraAimInterpolation = 0.025f;
	final Vector2 aimFocusVector = new Vector2();
	final Vector3 mousePosition = new Vector3();
	final Vector2 cameraFocusAim = new Vector2();
	protected void cameraUpdate() {
		zoom = zoom + (zoomDesired - zoom) * 0.1f;

		camera.zoom = zoom;

		if (cameraTarget == null) {

			//the camera should be draggable as a spectator or during respawn time
			if (spectatorMode || killFeed.isRespawnSpectator()) {
				//in spectator mode, the camera moves when dragging the mouse
				uiSpectator.spectatorDragCamera(spectatorTarget);
				aimFocusVector.set(spectatorTarget);
			} else if (player.getPlayerData() != null) {

				//we check for play data being null so client does not try to lerp camera towards starting position of (0, 0)
				aimFocusVector.set(player.getPixelPosition());

				//if enabled, camera tracks mouse position
				if (gsm.getSetting().isMouseCameraTrack()) {
					mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
					HadalGame.viewportCamera.unproject(mousePosition);

					cameraFocusAim.x = (int) (cameraFocusAim.x + (mousePosition.x - cameraFocusAim.x) * cameraAimInterpolation);
					cameraFocusAim.y = (int) (cameraFocusAim.y + (mousePosition.y - cameraFocusAim.y) * cameraAimInterpolation);
					aimFocusVector.mulAdd(cameraFocusAim, mouseCameraTrack).scl(1.0f / (1.0f + mouseCameraTrack));
				}
			}

			//make camera target respect camera bounds if not focused on an object
			CameraUtil.obeyCameraBounds(aimFocusVector, camera, cameraBounds);
		} else {
			aimFocusVector.set(cameraTarget);
		}
		aimFocusVector.add(cameraOffset);

		//process camera shaking
		CameraUtil.shake(camera, aimFocusVector, cameraTime);
		spectatorTarget.set(aimFocusVector);
		CameraUtil.lerpToTarget(camera, aimFocusVector, cameraInterpolation);
	}

	/**
	 * This is called upon exiting. Dispose of all created fields.
	 */
	@Override
	public void dispose() {
		b2dr.dispose();

		for (ObjectSet<HadalEntity> s: entityLists) {
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

	final Vector2 resizeTmpVector2 = new Vector2();
	@Override
	public void resize(int width, int height) {
		
		//This refocuses the camera to avoid camera moving after resizing
		if (gsm.getSetting().isMouseCameraTrack()) {
			resizeTmpVector2.set(aimFocusVector.x, aimFocusVector.y);
		} else if (cameraTarget == null) {
			if (player.getBody() != null && player.isAlive()) {
				resizeTmpVector2.set(player.getPixelPosition().x, player.getPixelPosition().y);
			}
		} else {
			resizeTmpVector2.set(cameraTarget.x, cameraTarget.y);
		}
		CameraUtil.obeyCameraBounds(resizeTmpVector2, camera, cameraBounds);

		this.camera.position.set(new Vector3(resizeTmpVector2.x, resizeTmpVector2.y, 0));

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
			gsm.getApp().fadeIn();
			spectatorMode = false;
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case RESULTS:

			//create snapshot to use for transition to results state
			FrameBuffer fbo = resultsStateFreeze();

			//get a results screen
			gsm.removeState(SettingState.class, false);
			gsm.removeState(AboutState.class, false);
			gsm.removeState(PauseState.class, false);
			gsm.removeState(PlayState.class, false);
			gsm.addResultsState(this, resultsText, LobbyState.class, fbo);
			gsm.addResultsState(this, resultsText, TitleState.class, fbo);
			break;
		case SPECTATOR:
			//When ded but other players alive, spectate a player
			gsm.getApp().fadeIn();
			setSpectatorMode();
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case NEWLEVEL:
			
			//remove this state and add a new play state with a fresh loadout
			gsm.removeState(SettingState.class, false);
			gsm.removeState(AboutState.class, false);
			gsm.removeState(PauseState.class, false);
			gsm.removeState(PlayState.class, false);
			gsm.addPlayState(nextLevel, nextMode, new Loadout(gsm.getLoadout()), player.getPlayerData(), LobbyState.class, true, nextStartId);
			gsm.addPlayState(nextLevel, nextMode, new Loadout(gsm.getLoadout()), player.getPlayerData(), TitleState.class, true, nextStartId);
			break;
		case NEXTSTAGE:
			
			//remove this state and add a new play state with the player's current loadout and stats
			gsm.removeState(SettingState.class, false);
			gsm.removeState(AboutState.class, false);
			gsm.removeState(PauseState.class, false);
			gsm.removeState(PlayState.class, false);
			gsm.addPlayState(nextLevel, nextMode, player.getPlayerData().getLoadout(), player.getPlayerData(), LobbyState.class, false, nextStartId);
			gsm.addPlayState(nextLevel, nextMode, player.getPlayerData().getLoadout(), player.getPlayerData(), TitleState.class, false, nextStartId);
			break;
		case TITLE:
			gsm.removeState(ResultsState.class);
			gsm.removeState(SettingState.class, false);
			gsm.removeState(AboutState.class, false);
			gsm.removeState(PauseState.class, false);
			gsm.removeState(PlayState.class);
			
			//add a notification to the title state if specified in transition state
			if (!gsm.getStates().isEmpty()) {
				if (gsm.getStates().peek() instanceof TitleState) {
					((TitleState) gsm.getStates().peek()).setNotification(resultsText);
				}
				if (gsm.getStates().peek() instanceof LobbyState) {
					((LobbyState) gsm.getStates().peek()).setNotification(resultsText);
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
	 * @param nextStartId: The id of the start point to start at (if specified)
	 */
	public void loadLevel(UnlockLevel level, GameMode mode, TransitionState state, String nextStartId) {
		
		//The client should never run this; instead transitioning when the server tells it to.
		if (!server) { return; }

		if (nextState == null) {

			//begin transitioning to the designated next level
			nextLevel = level;
			nextMode = mode;
			this.nextStartId = nextStartId;

			for (User user: HadalGame.server.getUsers().values()) {
				user.beginTransition(this, state, false, defaultFadeOutSpeed, defaultFadeDelay);
			}
		}
	}

	public void loadLevel(UnlockLevel level, TransitionState state, String nextStartId) {
		loadLevel(level, level.getModes()[0], state, nextStartId);
	}
	
	/**This creates a player to occupy the playstate
	 * @param start: start event to spawn the player at.
	 * @param name: player name
	 * @param altLoadout: the player's loadout
	 * @param old: player's old playerdata if retaining old values.
	 * @param connID: the player's connection id (0 if server)
	 * @param reset: should we reset the new player's hp/fuel/ammo?
	 * @param client: is this the client's own player?
	 * @param hitboxFilter: the new player's collision filter
	 * @return the newly created player
	 */
	public Player createPlayer(StartPoint start, String name, Loadout altLoadout, PlayerBodyData old, int connID,
	   		User user, boolean reset, boolean client, boolean justJoined, short hitboxFilter) {

		Loadout newLoadout = new Loadout(altLoadout);

		mode.processNewPlayerLoadout(this, newLoadout, connID, justJoined);

		StartPoint spawn = start;
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
			if (user.isSpawnOverridden()) {
				overiddenSpawn.set(user.getOverrideSpawnLocation());
			}
		}

		Player p;
		if (connID < 0) {
			p = new PlayerBot(this, overiddenSpawn, name, newLoadout, old, connID, user, reset, spawn);
		} else if (!client) {
			p = new Player(this, overiddenSpawn, name, newLoadout, old, connID, user, reset, spawn);
		} else {
			//clients always spawn at (0,0), then move when the server tells them to.
			p = new PlayerClient(this, new Vector2(), name, newLoadout, null, connID, user, reset, null);
		}
		
		//teleportation particles for reset players (indicates returning to hub)
		if (reset && isServer()) {
			new ParticleEntity(this, new Vector2(p.getStartPos()).sub(0, p.getSize().y / 2),
					Particle.TELEPORT, 1.0f, true, SyncType.CREATESYNC);
		}

		//for own player, the server must update their user information
		if (isServer() && connID == 0) {
			HadalGame.server.getUsers().get(0).setPlayer(p);
		}

		mode.modifyNewPlayer(this, newLoadout, p, hitboxFilter);
		return p;
	}
	
	//This is a list of all the saved player fields (scores) from the completed playstate
	private final Array<SavedPlayerFields> scores = new Array<>();
	private final ObjectMap<AlignmentFilter, Integer> teamKills = new ObjectMap<>();
	private final ObjectMap<AlignmentFilter, Integer> teamDeaths = new ObjectMap<>();
	private final ObjectMap<AlignmentFilter, Integer> teamScores = new ObjectMap<>();
	private final Array<AlignmentFilter> teamScoresList = new Array<>();
	/**
	 * This is called when a level ends. Only called by the server. Begin a transition and tell all clients to follow suit.
	 * @param text: text displayed in results state
	 */
	public void levelEnd(String text, boolean victory, float fadeDelay) {
		String resultsText = text;

		//magic word indicates that we generate the results text dynamically based on score
		Array<User> users = HadalGame.server.getUsers().values().toArray();
		if (text.equals(ResultsState.magicWord)) {
			for (User user: users) {
				if (!user.isSpectator()) {
					scores.add(user.getScores());

					AlignmentFilter faction;
					if (user.getTeamFilter().equals(AlignmentFilter.NONE)) {
						faction = user.getHitBoxFilter();
					} else {
						faction = user.getTeamFilter();
					}

					if (teamKills.containsKey(faction)) {
						teamKills.put(faction, teamKills.get(faction) + user.getScores().getKills());
					} else {
						teamKills.put(faction, user.getScores().getKills());
					}
					if (teamDeaths.containsKey(faction)) {
						teamDeaths.put(faction, teamDeaths.get(faction) + user.getScores().getDeaths());
					} else {
						teamDeaths.put(faction, user.getScores().getDeaths());
					}
					if (teamScores.containsKey(faction)) {
						teamScores.put(faction, teamScores.get(faction) + user.getScores().getScore());
					} else {
						teamScores.put(faction, user.getScores().getScore());
					}
				}
			}

			scores.sort((a, b) -> {
				int cmp = (b.getScore() - a.getScore());
				if (cmp == 0) { cmp = b.getKills() - a.getKills(); }
				if (cmp == 0) { cmp = a.getDeaths() - b.getDeaths(); }
				return cmp;
			});

			teamScoresList.addAll(teamScores.keys().toArray());
			teamScoresList.sort((a, b) -> {
				int cmp = (teamScores.get(b) - teamScores.get(a));
				if (cmp == 0) { cmp = teamKills.get(b) - teamKills.get(a); }
				if (cmp == 0) { cmp = teamDeaths.get(a) - teamDeaths.get(b); }
				return cmp;
			});

			if (mode.getTeamMode().equals(TeamMode.FFA)) {
				resultsText = HText.PLAYER_WINS.text(scores.get(0).getNameShort());
			} else {
				AlignmentFilter winningTeam = teamScoresList.get(0);
				if (winningTeam.isTeam()) {
					resultsText = HText.PLAYER_WINS.text(winningTeam.getColoredAdjective());
				} else {
					for (User user: users) {
						if (!user.isSpectator()) {
							if (user.getHitBoxFilter().equals(winningTeam)) {
								resultsText = HText.PLAYER_WINS.text(user.getScores().getNameShort());
							}
						}
					}
				}
			}

			AlignmentFilter winningTeam = teamScoresList.get(0);
			SavedPlayerFields winningScore = scores.get(0);

			for (User user : users) {
				if (!user.isSpectator()) {
					SavedPlayerFields score = user.getScores();
					if (mode.getTeamMode().equals(TeamMode.FFA)) {
						if (score.getScore() == winningScore.getScore() && score.getKills() == winningScore.getKills()
								&& score.getDeaths() == winningScore.getDeaths()) {
							score.win();
						}
					} else {
						AlignmentFilter faction;

						if (user.getTeamFilter().equals(AlignmentFilter.NONE)) {
							faction = user.getHitBoxFilter();
						} else {
							faction = user.getTeamFilter();
						}

						if (teamScores.containsKey(faction)) {
							score.setTeamScore(teamScores.get(faction));
						}

						if (user.getHitBoxFilter().equals(winningTeam) || user.getTeamFilter().equals(winningTeam)) {
							score.win();
						}
					}
				}
			}
		} else if (victory) {
            for (User user: users) {
                if (!user.isSpectator()) {
					user.getScores().win();
				}
            }
        }

		transitionToResultsState(resultsText, fadeDelay);
	}

	/**
	 * This is run by the server to transition to the results screen
	 */
	public void transitionToResultsState(String resultsText, float fadeDelay) {

		mode.processGameEnd(this);

		this.resultsText = resultsText;

		UserDto[] users = new UserDto[HadalGame.server.getUsers().size];

		int userIndex = 0;
		for (User user : HadalGame.server.getUsers().values()) {
			SavedPlayerFields score = user.getScores();
			Player resultsPlayer = user.getPlayer();
			if (resultsPlayer != null) {
				Loadout loadoutTemp = resultsPlayer.getPlayerData().getLoadout();

				//this removes any unusable weapons in loadout (Saved in a slot the player cannot use)
				for (int i = (int) (Loadout.baseWeaponSlots + resultsPlayer.getPlayerData().getStat(Stats.WEAPON_SLOTS)); i < Loadout.maxWeaponSlots ; i++) {
					loadoutTemp.multitools[i] = UnlockEquip.NOTHING;
				}
				user.getScoresExtra().setLoadout(loadoutTemp);
			}

			SavedPlayerFieldsExtra scoreExtra = user.getScoresExtra();
			users[userIndex] = new UserDto(score, scoreExtra, user.isSpectator());
			userIndex++;
		}
		HadalGame.server.sendToAllTCP(new Packets.SyncExtraResultsInfo(users, resultsText));

		for (User user: HadalGame.server.getUsers().values()) {
			user.beginTransition(this, TransitionState.RESULTS, true, 0.0f, fadeDelay);
		}
	}

	private static final float endTextY = 700;
	private static final float endTextWidth = 400;
	private static final float endTextScale = 2.5f;
	/**
	 * @return a snapshot of the player's current perspective. Used for transitioning to results state
	 */
	protected FrameBuffer resultsStateFreeze() {
		FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA4444, 1280, 720, true);
		fbo.begin();

		//clear buffer, set camera
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());

		render(0.0f);

		//draw extra ui elements for snapshot
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		HadalGame.FONT_UI.getData().setScale(endTextScale);
		HadalGame.FONT_UI.draw(batch, HText.GAME.text(),HadalGame.CONFIG_WIDTH / 2 - endTextWidth / 2, endTextY, endTextWidth,
				Align.center, true);
		batch.end();

		fbo.end();

		return fbo;
	}

	/**
	 * This is used to make a specific player a spectator after a transition.
	 * This is only run by the server
	 */
	public void becomeSpectator(Player player, boolean notification) {
		User user = HadalGame.server.playerToUser(player);

		if (user != null) {
			if (!user.isSpectator()) {
				if (notification) {
					HadalGame.server.addNotificationToAll(this,"", HText.SPECTATOR_ENTER.text(player.getName()),
							DialogType.SYSTEM);
				}

				startSpectator(user, player.getConnId());

				//we die last so that the on-death transition does not occur (As it will not override the spectator transition unless it is a results screen.)
				player.getPlayerData().die(worldDummy.getBodyData(), DamageTypes.DISCONNECT);
			}
		}
	}

	public void startSpectator(User user, int connId) {
		user.beginTransition(this, TransitionState.SPECTATOR, false, defaultFadeOutSpeed, shortFadeDelay);
		HadalGame.server.sendToTCP(connId, new Packets.ClientStartTransition(TransitionState.SPECTATOR, defaultFadeOutSpeed, shortFadeDelay));

		//set the spectator's player number to default so they don't take up a player slot
		user.getHitBoxFilter().setUsed(false);
		user.setHitBoxFilter(AlignmentFilter.NONE);
		user.setSpectator(true);
	}
	
	/**
	 * This is used to make a specific spectator a player after a transition.
	 * This is only run by the server
	 */
	public void exitSpectator(User user) {

		if (user != null) {
			if (user.isSpectator()) {
				SavedPlayerFields score = user.getScores();

				//cannot exit spectator if server is full
				if (HadalGame.server.getNumPlayers() >= gsm.getSetting().getMaxPlayers() + 1) {
					HadalGame.server.sendNotification(score.getConnID(), "", HText.SERVER_FULL.text(), DialogType.SYSTEM);
					return;
				}

				HadalGame.server.addNotificationToAll(this, "", HText.SPECTATOR_EXIT.text(score.getNameShort()), DialogType.SYSTEM);

				//give the new player a player slot
				user.setHitBoxFilter(AlignmentFilter.getUnusedAlignment());
				user.setSpectator(false);

				//for host, start transition. otherwise, send transition packet
				user.beginTransition(this, TransitionState.RESPAWN, false, defaultFadeOutSpeed, shortFadeDelay);
			}
		}
	}

	/**
	 * This is called whenever we transition to a new state. Begin transition and set new state.
	 * @param state: The state we are transitioning towards
	 * @param fadeSpeed: speed of transition
	 * @param fadeDelay: amount of delay before transition
	 */
	public void beginTransition(TransitionState state, float fadeSpeed, float fadeDelay) {

		//If we are already transitioning to a new results state, do not do this unless we tell it to override
		nextState = state;
		gsm.getApp().fadeSpecificSpeed(fadeSpeed, fadeDelay);
		gsm.getApp().setRunAfterTransition(this::transitionState);

		//fadeSpeed means we skip the fade. Only relevant during special transitions
		if (state.equals(TransitionState.RESPAWN) && fadeSpeed != 0.0f) {
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

		beginTransition(TransitionState.TITLE, defaultFadeOutSpeed, delay);
	}
	
	/**
	 * This looks for an entity in the world with the given entityId
	 * this is kinda slow. don't overuse it.
	 */
	public HadalEntity findEntity(UUID entityId) {

		for (ObjectSet<HadalEntity> s: entityLists) {
			for (HadalEntity entity : s) {
				if (entity.getEntityID().equals(entityId)) {
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
	 * @param connId: connId of the new client
	 */
	public void catchUpClient(int connId) {
		for (ObjectSet<HadalEntity> s: entityLists) {
			for (HadalEntity entity : s) {
				Object packet = entity.onServerCreate(true);
				if (packet != null) {
					HadalGame.server.sendToUDP(connId, packet);
				}
			}
		}
	}
	
	/**
	 * This acquires the level's save points. If none, respawn at starting location. If many, choose one randomly
	 * @return a save point to spawn a respawned player at
	 */
	public StartPoint getSavePoint(String startId, User user) {

		if (!isServer()) { return null; }

		Array<StartPoint> validStarts = new Array<>();
		Array<StartPoint> readyStarts = new Array<>();
		
		//get a list of all start points that match the startId
		for (StartPoint s: savePoints) {
			if (mode.isTeamDesignated() && AlignmentFilter.currentTeams.length > s.getTeamIndex()) {
				if (user.getTeamFilter().equals(AlignmentFilter.currentTeams[s.getTeamIndex()])) {
					validStarts.add(s);
				}
			} else if (s.getStartId().equals(startId)) {
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
		for (StartPoint s: validStarts) {
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
		return getSavePoint(startId, user);
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
	
	private static final float spectatorDefaultZoom = 1.5f;
	/**
	 * Player enters spectator mode. Set up spectator camera and camera bounds
	 */
	public void setSpectatorMode() {
		spectatorMode = true;
		spectatorTarget.set(camera.position.x, camera.position.y);
		
		this.zoomDesired = map.getProperties().get("zoom", spectatorDefaultZoom, float.class);
		this.cameraTarget = null;
		this.cameraOffset.set(0, 0);
		if (spectatorBounded) {
			System.arraycopy(spectatorBounds, 0, cameraBounds, 0, 4);
		}

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
	
	public boolean isServer() { return server; }

	public boolean isReset() { return reset; }

	public float getTimeModifier() { return timeModifier; }

	public void setTimeModifier(float timeModifier) { this.timeModifier = timeModifier; }

	public boolean isSpectatorMode() { return spectatorMode; }

	public void addMapModifier(UnlockArtifact modifier) { this.mapModifiers.add(modifier); }

	public Array<UnlockArtifact> getMapModifiers() { return mapModifiers; }

	public void addMapEquipTag(UnlockTag mapEquipTag) { this.mapEquipTags.add(mapEquipTag); }

	public Array<UnlockTag> getMapEquipTag() { return mapEquipTags; }

	public Event getGlobalTimer() {	return globalTimer;	}

	public void setGlobalTimer(Event globalTimer) {	this.globalTimer = globalTimer;	}

	public Event getSpectatorActivation() {	return spectatorActivation;	}

	public void setSpectatorActivation(Event spectatorActivation) {	this.spectatorActivation = spectatorActivation;	}

	public Player getPlayer() {	return player; }
	
	public void setPlayer(Player player) { this.player = player; }

	public World getWorld() { return world; }

	public TiledMap getMap() { return map; }

	public WorldDummy getWorldDummy() { return worldDummy; }
	
	public AnchorPoint getAnchor() { return anchor; }

	public UnlockLevel getLevel() { return level; }

	public GameMode getMode() { return mode; }

	public float getTimer() {return timer; }
	
	public void setTimer(float timer) { this.timer = timer; }

	public float getRespawnTime() { return respawnTime; }

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
	
	public void setCameraTarget(Vector2 cameraTarget) {	this.cameraTarget = cameraTarget; }
	
	public void setCameraOffset(float offsetX, float offsetY) {	cameraOffset.set(offsetX, offsetY); }
	
	public Vector2 getCameraTarget() {	return cameraTarget; }

	public Vector2 getCameraFocusAim() { return cameraFocusAim; }

	public float[] getCameraBounds() { return cameraBounds; }

	public float[] getSpectatorBounds() { return spectatorBounds; }

	public void setSpectatorBounded(boolean spectatorBounded) { this.spectatorBounded = spectatorBounded; }

	public boolean isSpectatorBounded() { return spectatorBounded; }

	public MouseTracker getMouse() { return mouse; }

	public InputProcessor getController() { return controller; }

	public MessageWindow getMessageWindow() { return messageWindow; }

	public KillFeed getKillFeed() { return killFeed; }

	public ChatWheel getChatWheel() { return chatWheel; }
	
	public ScoreWindow getScoreWindow() { return scoreWindow; }	
	
	public DialogBox getDialogBox() { return dialogBox; }

	public void setZoom(float zoom) { this.zoomDesired = zoom; }

	public Array<Object> getSyncPackets() {	return syncPackets; }

	public void setNextState(TransitionState nextState) { this.nextState = nextState; }

	public TransitionState getNextState() { return nextState; }

	public void setResultsText(String resultsText) { this.resultsText = resultsText; }
}
