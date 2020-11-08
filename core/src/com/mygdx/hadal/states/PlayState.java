package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.*;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.event.utility.PositionDummy;
import com.mygdx.hadal.handlers.WorldContactListener;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.*;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.*;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.CameraStyles;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.TiledObjectUtil;

import java.util.*;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * The PlayState is the main state of the game and holds the Box2d world, all characters + gameplay.
 * @author Norroway Nigganov
 */
public class PlayState extends GameState {
	
	//This is an entity representing the player. Atm, player is not initialized here, but rather by a "Player Spawn" event in the map.
	protected Player player;
	
	//This is the player's controller that receives inputs
	protected InputProcessor controller;
	
	//This is the loadout that the player starts off with when they enter the playstate
	private final UnlockEquip[] mapMultitools;
	private final UnlockArtifact[] mapArtifacts;
	private final UnlockActives mapActiveItem;
	
	//These process and store the map parsed from the Tiled file.
	protected TiledMap map;
	protected OrthogonalTiledMapRenderer tmr;
	
	//world manages the Box2d world and physics. b2dr renders debug lines for testing
	protected Box2DDebugRenderer b2dr;
	protected World world;

	//This holds the mouse location
	private final MouseTracker mouse;

	//These represent the set of entities to be added to/removed from the world. This is necessary to ensure we do this between world steps.
	private final Set<HadalEntity> removeList;
	private final Set<HadalEntity> createList;
	
	//This is a set of all non-hitbox entities in the world
	private final Set<HadalEntity> entities;
	//This is a set of all hitboxes. This is separate to draw hitboxes underneath other bodies
	private final Set<HadalEntity> hitboxes;
	
	//This is a list of packetEffects, given when we receive packets with effects that we want to run in update() rather than whenever
	private final List<PacketEffect> packetEffects;
	private final List<PacketEffect> addPacketEffects;
	
	//sourced effects from the world are attributed to this dummy.
	private final WorldDummy worldDummy;
	private final AnchorPoint anchor;
	
	//this is the current level
	protected UnlockLevel level;
	
	//This is the id of the start event that we will be spawning on
	private String startId;
	
	//This is the coordinate that the camera tries to focus on when set to aim at an entity. When null, the camera focuses on the player.
	private Vector2 cameraTarget;
	
	//the camera is pointed offset of the target by this vector. (this is pretty much only used when focusing on the player)
	private final Vector2 cameraOffset = new Vector2();
	
	//coordinate the camera is looking at in spectator mode. Unlock cameraTarget, this shouldn't be null
	private final Vector2 spectatorTarget = new Vector2();
	
	//are we currently a spectator or not?
	protected boolean spectatorMode;
	
	//These are the bounds of the camera movement. We make the numbers really big so that the default is no bounds.
	private final float[] cameraBounds = {100000.0f, -100000.0f, 100000.0f, -100000.0f};
	private final float[] spectatorBounds = {100000.0f, -100000.0f, 100000.0f, -100000.0f};
	
	//are the spectator bounds distinct from the camera bounds?
	private boolean spectatorBounded;
	
	//do players have infinite lives in this map?
	private boolean unlimitedLife;
	
	//The current zoom of the camera
	private float zoom;

	//This is the zoom that the camera will lerp towards
	protected float zoomDesired;
	
	//If a player respawns, they will respawn at the coordinates of a safe point from this list.
	private final ArrayList<StartPoint> savePoints;
	
	//This is an arrayList of ids to dummy events. These are used for enemy ai processing
	private final HashMap<String, PositionDummy> dummyPoints;
	
	//Can players hurt each other? Is it the hub map? Is this the server?
	private final boolean pvp, hub, server, teamEnabled;
	
	//Various play state ui elements
	protected UIPlay uiPlay;
	protected UIObjective uiObjective;
	protected UIExtra uiExtra;
	protected UIArtifacts uiArtifact;
	protected UIHub uiHub;
	protected MessageWindow messageWindow;
	protected ChatWheel chatWheel;
	protected ScoreWindow scoreWindow;
	protected DialogBox dialogBox;
	
	//Background and black screen used for transitions
	private final TextureRegion bg;
	private Shader shaderBase;
	
	//if we are transitioning to another state, this is that state
	protected TransitionState nextState;
	
	//If we are transitioning to another level, this is that level.
	private UnlockLevel nextLevel;
	private String nextStartId;
	
	//If we are transitioning to a results screen, this is the displayed text;
	protected String resultsText;
	
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
	public static final float deathFadeDelay = 1.5f;

	//Special designated events parsed from map
	private Event globalTimer;
	
	/**
	 * Constructor is called upon player beginning a game.
	 * @param gsm: StateManager
	 * @param loadout: the loadout that the player should start with
	 * @param level: the level we are loading into
	 * @param server: is this the server or not?
	 * @param old: the data of the previous player (this exists if this play state is part of a stage transition with an existing player)
	 * @param reset: do we reset the old player's hp/fuel/ammo in the new playstate?
	 * @param startId: the id of the starting event the player should be spawned at
	 */
	public PlayState(GameStateManager gsm, Loadout loadout, UnlockLevel level, boolean server, PlayerBodyData old, boolean reset, String startId) {
		super(gsm);

		this.server = server;
		this.teamEnabled = gsm.getSetting().isTeamEnabled();
		
		//Maps can have a set loadout. This will override the loadout given as an input to the playstate.
		this.mapMultitools = level.getMultitools();
		this.mapArtifacts = level.getArtifacts();
		this.mapActiveItem = level.getActiveItem();
		this.level = level;
		this.startId = startId;
        
        //Initialize box2d world and related stuff
		world = new World(new Vector2(0, -9.81f), true);
		world.setContactListener(new WorldContactListener());
		World.setVelocityThreshold(0);

		b2dr = new Box2DDebugRenderer();
		
		//Initialize sets to keep track of active entities and packet effects
		entities = new LinkedHashSet<>();
		hitboxes = new LinkedHashSet<>();
		removeList = new LinkedHashSet<>();
		createList = new LinkedHashSet<>();
		packetEffects = new ArrayList<>();
		addPacketEffects = Collections.synchronizedList(new ArrayList<>());
		
		//The "worldDummy" will be the source of map-effects that want a perpetrator
		worldDummy = new WorldDummy(this);
		anchor = new AnchorPoint(this);
		
		//The mouse tracker is the player's mouse position
		mouse = new MouseTracker(this, true);

		//load map
		map = new TmxMapLoader().load(level.getMap());
		tmr = new OrthogonalTiledMapRenderer(map, batch);

		//Get map settings from the collision layer of the map
		this.pvp = map.getProperties().get("pvp", false, Boolean.class);
		this.hub = map.getProperties().get("hub", false, Boolean.class);
		this.unlimitedLife = map.getProperties().get("lives", false, boolean.class);
		this.zoom = map.getProperties().get("zoom", 1.0f, float.class);
		this.zoomDesired = zoom;	

		//load map shader
		this.shaderBase = Shader.NOTHING;
		if (map.getProperties().get("shader", String.class) != null) {
			shaderBase = Shader.valueOf(map.getProperties().get("shader", String.class));
			shaderBase.loadShader();
		}
		
		//Clear events in the TiledObjectUtil to avoid keeping reference to previous map's events.
		TiledObjectUtil.clearEvents();
		
		//Set up "save point" as starting point
		this.savePoints = new ArrayList<>();
				
		//Only the server processes collision objects, events and triggers
		if (server) {
			TiledObjectUtil.parseTiledObjectLayer(this, map.getLayers().get("collision-layer").getObjects());
			TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get("event-layer").getObjects());
			TiledObjectUtil.parseTiledTriggerLayer();
			TiledObjectUtil.parseDesignatedEvents(this);
		}

		//Create the player and make the camera focus on it
		StartPoint getSave = getSavePoint(startId);
		if (server) {
			short hitboxFilter = HadalGame.server.getUsers().get(0).getHitBoxFilter().getFilter();
			this.player = createPlayer(getSave, gsm.getLoadout().getName(), loadout, old, 0, reset, false, hitboxFilter);
		} else {
			this.player = createPlayer(getSave, gsm.getLoadout().getName(), loadout, old, 0, reset, false,
				Constants.PLAYER_HITBOX);
		}

		if (getSave != null) {
			this.camera.position.set(new Vector3(getSave.getStartPos().x, getSave.getStartPos().y, 0));
		}
		this.reset = reset;
		
		//Set up dummy points (AI rally points)
		this.dummyPoints = new HashMap<>();
				
		//Init background image
		this.bg = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.BACKGROUND2.toString()));

		//set whether to draw hitbox debug lines or not
		debugHitbox = gsm.getSetting().isDebugHitbox();
	}
			
	@Override
	public void show() {

		if (stage == null) {
			this.stage = new Stage() {
				
				//This precaution exists to prevent null pointer when player is not loaded in yet.
				@Override
				public void draw() {
				if (player.getPlayerData() != null) {
					super.draw();
				}
				}
			};
		}
		
		//If ui elements have not been created, create them. (upon first showing the state)
		if (uiPlay == null) {
			if (server) {
				uiPlay = new UIPlay(this);
			} else {
				uiPlay = new UIPlayClient(this);
			}
			
			uiObjective = new UIObjective(this);
			uiArtifact = new UIArtifacts(this);
			uiExtra = new UIExtra(this);
			uiExtra.changeTypes(map.getProperties().get("startUI", "", String.class), true);
			uiHub = new UIHub(this);
			
			messageWindow = new MessageWindow(this, stage);
			chatWheel = new ChatWheel(this, stage);
			scoreWindow = new ScoreWindow(this);
			dialogBox = new DialogBox(this, 0, (int) HadalGame.CONFIG_HEIGHT);
		}
		
		//Add and sync ui elements in case of unpause or new playState
		this.stage.addActor(uiPlay);
		this.stage.addActor(uiObjective);
		this.stage.addActor(uiExtra);
		this.stage.addActor(dialogBox);

		app.newMenu(stage);
		resetController();

		//if we faded out before transitioning to this stage, we should fade in upon showing
		if (gsm.getApp().getFadeLevel() >= 1.0f) {
			gsm.getApp().fadeIn();
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
	
	protected float scoreSyncAccumulator;
	protected static final float ScoreSyncTime = 1.0f;
	
	private float timer;
	/**
	 * Every engine tick, the GameState must process all entities in it according to the time elapsed.
	 */
	@Override
	public void update(float delta) {

		//On the very first tick, server tells all clients that it is loaded
		if (server && !serverLoaded) {
	        serverLoaded = true;
			HadalGame.server.sendToAllTCP(new Packets.ServerLoaded());
		}

		//this makes the physics separate from the game framerate
		physicsAccumulator += delta;
		while (physicsAccumulator >= physicsTime) {
			physicsAccumulator -= physicsTime;
			
			//The box2d world takes a step. This handles collisions + physics stuff.
			world.step(physicsTime, 8, 3);
		}

		//All entities that are set to be added are added.
		for (HadalEntity entity: createList) {
			if (entity instanceof Hitbox) {
				hitboxes.add(entity);
			} else {
				entities.add(entity);
			}
			entity.create();
			//Upon creating an entity, tell the clients so they can follow suit (if the entity calls for it)
			Object packet = entity.onServerCreate();
			if (packet != null) {
				HadalGame.server.sendToAllUDP(packet);
			}
		}
		createList.clear();

		//All entities that are set to be removed are removed.
		for (HadalEntity entity: removeList) {
			entities.remove(entity);
			hitboxes.remove(entity);
			entity.dispose();
			
			//Upon deleting an entity, tell the clients so they can follow suit.
			Object packet = entity.onServerDelete();
			if (packet != null) {
				HadalGame.server.sendToAllUDP(packet);
			}
		}
		removeList.clear();
		
		//process camera, ui, any received packets
		processCommonStateProperties(delta);
		
		//This processes all entities in the world. (for example, player input/cooldowns/enemy ai)
		controllerEntities(delta);
		
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
		if (scoreSyncAccumulator >= ScoreSyncTime) {
			scoreSyncAccumulator = 0;
			boolean changeMade = false;
			for (User user : HadalGame.server.getUsers().values()) {
				if (user.isScoreUpdated()) {
					changeMade = true;
					user.setScoreUpdated(false);
				}
				SavedPlayerFields score = user.getScores();
				HadalGame.server.sendToAllUDP(new Packets.SyncScore(user.getScores().getConnID(), score.getNameShort(), score.getWins(),
					score.getKills(), score.getDeaths(), score.getScore(), score.getLives(), score.getPing()));
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
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Render Background
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		batch.disableBlending();

		//render shader
		if (shaderBase.getShaderProgram() != null) {
			shaderBase.getShaderProgram().bind();
			shaderBase.shaderPlayUpdate(this, timer);
			shaderBase.shaderDefaultUpdate(timer);
			batch.setShader(shaderBase.getShaderProgram());
		}
		batch.draw(bg, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
		
		if (shaderBase.getShaderProgram() != null) {
			if (shaderBase.isBackground()) {
				batch.setShader(null);
			}
		}
		batch.enableBlending();
		batch.end();
		
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
		
		renderEntities(delta);
		
		if (shaderBase.getShaderProgram() != null) {
			if (!shaderBase.isBackground()) {
				batch.setShader(null);
			}
		}
		batch.end();
	}	
	
	/**
	 * This is run in the update method and is just a helper to avoid repeating code in both the server/client states.
	 * This does all of the stuff that is needed for both server and client (processing packets, fade and some other misc stuff)
	 */
	private float cameraAccumulator;
	private static final float cameraTime = 1 / 120f;
	public void processCommonStateProperties(float delta) {
		
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
	
	/**
	 * Render all entities in the world
	 */
	public void renderEntities(float delta) {
		for (HadalEntity schmuck : entities) {
			renderEntity(schmuck);
		}
		for (HadalEntity hitbox : hitboxes) {
			renderEntity(hitbox);
		}
	}
	
	/**
	 * Run the controller method for all entities in the world
	 */
	public void controllerEntities(float delta) {
		for (HadalEntity entity : hitboxes) {
			entity.controller(delta);
			entity.decreaseShaderCount(delta);
			entity.increaseAnimationTime(delta);
			entity.increaseEntityAge(delta);
		}
		for (HadalEntity entity : entities) {
			entity.controller(delta);
			entity.decreaseShaderCount(delta);
			entity.increaseAnimationTime(delta);
			entity.increaseEntityAge(delta);
		}
	}
	
	/**
	 * This sends a synchronization packet for every synced entity. syncFastEntities() is used for entities that are synced more frequently
	 */
	private final ArrayList<Object> syncPackets = new ArrayList<>();
	public void syncEntities() {
		
		for (HadalEntity entity : hitboxes) {
			entity.onServerSync();
		}
		for (HadalEntity entity : entities) {
			entity.onServerSync();
		}
		for (Object o: syncPackets) {
			HadalGame.server.sendToAllUDP(o);
		}
		syncPackets.clear();
	}
	
	public void syncFastEntities() {
		for (HadalEntity entity : hitboxes) {
			entity.onServerSyncFast();
		}
		for (HadalEntity entity : entities) {
			entity.onServerSyncFast();
		}
	}
	
	/**
	 * This method renders a single entity.
	 * @param entity: the entity we are rendering
	 */
	public void renderEntity(HadalEntity entity) {
		
		if (entity.isVisible()) {
			if (entity.getShaderCount() > 0 && entity.getShader() != null) {
				entity.processShaderController();
				batch.setShader(entity.getShader().getShaderProgram());
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
	private static final float spectatorCameraRange = 9000.0f;
	Vector2 tmpVector2 = new Vector2();
	Vector3 mousePosition = new Vector3();
	Vector2 mousePosition2 = new Vector2();
	protected void cameraUpdate() {
		zoom = zoom + (zoomDesired - zoom) * 0.1f;
		
		camera.zoom = zoom;
		if (cameraTarget == null) {
			if (player.getBody() != null && player.isAlive()) {
				tmpVector2.set(player.getPixelPosition());
			} else if (spectatorMode) {
				
				//in spectator mode, the camera tracks the mouse
				mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				HadalGame.viewportCamera.unproject(mousePosition);
				mousePosition2.set(mousePosition.x, mousePosition.y);
				if (spectatorTarget.dst2(mousePosition2) > spectatorCameraRange) {
					spectatorTarget.lerp(mousePosition2, 0.03f);
				}
				tmpVector2.set(spectatorTarget);
			} else {
				return;
			}
			//make camera target respect camera bounds if not focused on an object
			if (tmpVector2.x > cameraBounds[0]) {
				tmpVector2.x = cameraBounds[0];
			}
			if (tmpVector2.x < cameraBounds[1]) {
				tmpVector2.x = cameraBounds[1];
			}		
			if (tmpVector2.y > cameraBounds[2]) {
				tmpVector2.y = cameraBounds[2];
			}
			if (tmpVector2.y < cameraBounds[3]) {
				tmpVector2.y = cameraBounds[3];
			}
		} else {
			tmpVector2.set(cameraTarget);
		}

		//this makes the spectator target respect camera bounds
		tmpVector2.add(cameraOffset);
		spectatorTarget.set(tmpVector2);
		CameraStyles.lerpToTarget(camera, tmpVector2);
	}
	
	/**
	 * This is called upon exiting. Dispose of all created fields.
	 */
	@Override
	public void dispose() {
		b2dr.dispose();
		
		for (HadalEntity schmuck : entities) {
			schmuck.dispose();
		}
		for (HadalEntity hitbox : hitboxes) {
			hitbox.dispose();
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
	}
	
	@Override
	public void resize(int width, int height) {
		
		//This refocuses the camera to avoid camera moving after resizing
		if (cameraTarget == null) {
			if (player.getBody() != null && player.isAlive()) {
				this.camera.position.set(new Vector3(player.getPixelPosition().x, player.getPixelPosition().y, 0));
			}
		} else {
			this.camera.position.set(new Vector3(cameraTarget.x, cameraTarget.y, 0));
		}
		
		if(shaderBase.getShaderProgram() != null) {
			shaderBase.getShaderProgram().bind();
			shaderBase.shaderResize();
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
			
			StartPoint getSave = getSavePoint();
			
			//Create a new player
			short hitboxFilter = HadalGame.server.getUsers().get(0).getHitBoxFilter().getFilter();
			player = createPlayer(getSave, gsm.getLoadout().getName(), player.getPlayerData().getLoadout(), player.getPlayerData(),
				0, true, false, hitboxFilter);

			this.camera.position.set(new Vector3(getSave.getStartPos().x, getSave.getStartPos().y, 0));

			((PlayerController) controller).setPlayer(player);

			//Make nextState null so we can transition again
			nextState = null;
			break;
		case RESULTS:

			//get a results screen
			gsm.removeState(SettingState.class);
			gsm.removeState(PauseState.class);
			gsm.removeState(PlayState.class);
			gsm.addResultsState(this, resultsText, TitleState.class);
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
			gsm.removeState(SettingState.class);
			gsm.removeState(PauseState.class);
			gsm.removeState(PlayState.class);
			gsm.addPlayState(nextLevel, new Loadout(gsm.getLoadout()), player.getPlayerData(), TitleState.class, true, nextStartId);
			break;
		case NEXTSTAGE:
			
			//remove this state and add a new play state with the player's current loadout and stats
			gsm.removeState(SettingState.class);
			gsm.removeState(PauseState.class);
			gsm.removeState(PlayState.class);
			gsm.addPlayState(nextLevel, player.getPlayerData().getLoadout(), player.getPlayerData(), TitleState.class, false, nextStartId);
			break;
		case TITLE:
			gsm.removeState(SettingState.class);
			gsm.removeState(PauseState.class);
			gsm.removeState(PlayState.class);
			
			//add a notification to the title state if specified in transition state
			if (!gsm.getStates().isEmpty()) {
				if (gsm.getStates().peek() instanceof TitleState) {
					((TitleState) gsm.getStates().peek()).setNotification(resultsText);
				}
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * transition from one playstate to another with a new level.
	 * @param level: file of the new map
	 * @param state: this will either be a new level or next stage to determine whether we reset hp
	 * @param nextStartId: The id of the start point to start at (if specified)
	 */
	public void loadLevel(UnlockLevel level, TransitionState state, String nextStartId) {
		
		//The client should never run this; instead transitioning when the server tells it to.
		if (!server) { return; }

		if (nextState == null) {
			
			//begin transitioning to the designated next level
			nextLevel = level;
			this.nextStartId = nextStartId;
			beginTransition(state, false, "", defaultFadeOutSpeed, defaultFadeDelay);
			
			//Server tells clients to begin a transition to the new state
			HadalGame.server.sendToAllTCP(new Packets.ClientStartTransition(nextState, false, "", defaultFadeOutSpeed, defaultFadeDelay));
		}
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
							   boolean reset, boolean client, short hitboxFilter) {

		Loadout newLoadout = new Loadout(altLoadout);

		//for pvp matches, set loadout depending on pvp settings
		if (pvp && !hub) {
			switch(gsm.getSetting().getLoadoutType()) {
			
			//copy setting: each player starts with the same loadout as the host (used for custom games)
			case 0:
				for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
					newLoadout.multitools[i] = UnlockEquip.valueOf(gsm.getLoadout().getEquips()[i]);
				}
				for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
					newLoadout.artifacts[i] = UnlockArtifact.valueOf(gsm.getLoadout().getArtifacts()[i]);
				}
				newLoadout.activeItem = UnlockActives.valueOf(gsm.getLoadout().getActive());
				break;
			//select setting: each player starts with the weapons they selected in the hub
			case 1:
				break;
			//random setting: each player starts with random weapons
			case 2:
				for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
					newLoadout.multitools[i] = UnlockEquip.valueOf(UnlockEquip.getRandWeapFromPool(this, ""));
				}
				break;
			}
		}
		
		//some maps specify a specific loadout. Load these, if so (and override other loadout settings)
		if (mapMultitools != null) {
			for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
				if (mapMultitools.length > i) {
					newLoadout.multitools[i] = mapMultitools[i];
				}
			}
		}
		if (mapArtifacts != null) {
			for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
				if (mapArtifacts.length > i) {
					newLoadout.artifacts[i] = mapArtifacts[i];
				}
			}
		}
		if (mapActiveItem != null) {
			newLoadout.activeItem = mapActiveItem;
		}

		Player p;
		if (!client) {
			//servers spawn at the starting point if existent. We prefer using the body's position, but can also use the starting position if it hasn't been created yet.
			if (start != null) {
				if (start.getBody() != null) {
					p = new Player(this, start.getPixelPosition(), name, newLoadout, old, connID, reset, start);
				} else {
					p = new Player(this, start.getStartPos(), name, newLoadout, old, connID, reset, start);
				}
			} else {
				//no start point means we create the player at (0,0) I don't think this should ever happen.
				p = new Player(this, new Vector2(), name, newLoadout, old, connID, reset, null);
			}
		} else {
			//clients always spawn at (0,0), then move when the server tells them to.
			p = new ClientPlayer(this, new Vector2(), name, newLoadout, null, connID, reset, null);
		}
		
		//teleportation particles for reset players (indicates returning to hub)
		if (reset) {
			new ParticleEntity(this, new Vector2(p.getStartPos()).sub(0, p.getSize().y / 2), Particle.TELEPORT, 1.0f, true, particleSyncType.CREATESYNC);
		}

		//for own player, the server must update their user information
		if (isServer() && connID == 0) {
			HadalGame.server.getUsers().get(0).setPlayer(p);
			HadalGame.server.getUsers().get(0).setMouse(mouse);
		}

		//set player pvp hitbox filter.
		if (isPvp()) {
			if (teamEnabled && !isHub()) {
				if (newLoadout.team.equals(AlignmentFilter.NONE)) {
					p.setHitboxfilter(hitboxFilter);
				} else {
					p.setHitboxfilter(newLoadout.team.getFilter());
				}
			} else {
				p.setHitboxfilter(hitboxFilter);
			}
		}
		return p;
	}
	
	/**
	 * This is called whenever a player is killed. This is only called by the server.
	 * @param player: The player that dies
	 * @param perp: the schmuck that killed
	 */
	public void onPlayerDeath(Player player, Schmuck perp) {
		
		//Register the kill for score keeping purposes
		if (perp instanceof Player) {
			HadalGame.server.registerKill((Player) perp, player);
		} else {
			HadalGame.server.registerKill(null, player);
		}
				
		if (!unlimitedLife) {
			String resultsText = "";
			
			//check if all players are out
			boolean allded = true;
			
			//in pvp, game ends if all players left are on the same team.
			// (if only 1 player, do not register end until all lives are used. mostly for testing)
			if (pvp && HadalGame.server.getUsers().size() > 1) {
				
				short factionLeft = -1;
				for (User user: HadalGame.server.getUsers().values()) {
					if (user.getScores().getLives() > 0) {
						Player playerLeft = user.getPlayer();

						if (playerLeft != null) {
							if (teamEnabled && !isHub()) {

								//if team mode, display a win for the team instead
								if (!playerLeft.getPlayerData().getLoadout().team.equals(AlignmentFilter.NONE)) {
									resultsText = playerLeft.getPlayerData().getLoadout().team.toString() + " WINS";
								} else {
									resultsText = playerLeft.getName() + " WINS";
								}
							} else {
								resultsText = playerLeft.getName() + " WINS";
							}

							if (factionLeft == -1) {
								factionLeft = playerLeft.getHitboxfilter();
							} else {
								if (factionLeft != playerLeft.getHitboxfilter()) {
									allded = false;
								}
							}
						}
					}
				}
			} else {
				resultsText = "YOU DECEASED";
				
				//coop levels end when all players are dead
				for (User user : HadalGame.server.getUsers().values()) {
					if (user.getScores().getLives() > 0) {
						allded = false;
						break;
					}
				}
			}
			
			//if the match is over (all players dead in co-op or all but one team dead in pvp), all players go to results screen
			if (allded) {
				beginTransition(TransitionState.RESULTS, true, resultsText, defaultFadeOutSpeed, deathFadeDelay);
				HadalGame.server.sendToAllTCP(new Packets.ClientStartTransition(TransitionState.RESULTS, true, resultsText, defaultFadeOutSpeed, deathFadeDelay));
			} else {

				User dedUser = HadalGame.server.getUsers().get(player.getConnID());
				if (dedUser != null) {
					//the player that dies respawns if there are lives left and becomes a spectator otherwise
					if (this.player.equals(player)) {
						if (dedUser.getScores().getLives() > 0) {
							beginTransition(TransitionState.RESPAWN, false, "", defaultFadeOutSpeed, deathFadeDelay);
						} else {
							beginTransition(TransitionState.SPECTATOR, false, "", defaultFadeOutSpeed, deathFadeDelay);
						}
					} else {
						//If a client dies, we tell them to transition to a spectator or respawn state.
						if (dedUser.getScores().getLives() > 0) {
							HadalGame.server.sendToTCP(player.getConnID(), new Packets.ClientStartTransition(TransitionState.RESPAWN, false, "", defaultFadeOutSpeed, deathFadeDelay));
						} else {
							HadalGame.server.sendToTCP(player.getConnID(), new Packets.ClientStartTransition(TransitionState.SPECTATOR, false, "", defaultFadeOutSpeed, deathFadeDelay));
						}
					}
				}
			}
		} else {
			//if there are infinite lives, we respawn the dead player
			if (this.player.equals(player)) {
				beginTransition(TransitionState.RESPAWN, false, "", defaultFadeOutSpeed, deathFadeDelay);
			} else {
				HadalGame.server.sendToTCP(player.getConnID(), new Packets.ClientStartTransition(TransitionState.RESPAWN, false, "", defaultFadeOutSpeed, deathFadeDelay));
			}
		}
	}

	//This is a list of all the saved player fields (scores) from the completed playstate
	private final ArrayList<SavedPlayerFields> scores = new ArrayList<>();
	private final Map<AlignmentFilter, Integer> teamScores = new HashMap<>();
	private final ArrayList<AlignmentFilter> teamScoresList = new ArrayList<>();
	/**
	 * This is called when a level ends. Only called by the server. Begin a transition and tell all clients to follow suit.
	 * @param text: text displayed in results state
	 */
	public void levelEnd(String text) {
		String resultsText = text;
		if (text.equals(ResultsState.magicWord)) {
			for (User user: HadalGame.server.getUsers().values()) {
				scores.add(user.getScores());

				AlignmentFilter faction;

				if (user.getTeamFilter().equals(AlignmentFilter.NONE)) {
					faction = user.getHitBoxFilter();
				} else {
					faction = user.getTeamFilter();
				}

				if (teamScores.containsKey(faction)) {
					teamScores.put(faction, user.getScores().getScore() + teamScores.get(faction));
				} else {
					teamScores.put(faction, user.getScores().getScore());
				}
			}

			//Then, we sort according to score and give the winner(s) a win.
			scores.sort((a, b) -> b.getScore() - a.getScore());
			teamScoresList.addAll(teamScores.keySet());
			teamScoresList.sort((a, b) -> teamScores.get(b) - teamScores.get(a));

			if (teamEnabled) {
				AlignmentFilter winningTeam = teamScoresList.get(0);
				if (winningTeam.isTeam()) {
					resultsText = winningTeam.toString() + " WINS";
				} else {
					for (User user: HadalGame.server.getUsers().values()) {
						if (user.getHitBoxFilter().equals(winningTeam)) {
							resultsText = user.getScores().getNameShort() + " WINS";
						}
					}
				}
			} else {
				resultsText = scores.get(0).getNameShort() + " WINS";
			}

			AlignmentFilter winningTeam = teamScoresList.get(0);
			int winningScore = scores.get(0).getScore();

			for (User user : HadalGame.server.getUsers().values()) {
				SavedPlayerFields score = user.getScores();
				if (teamEnabled) {
					if (user.getHitBoxFilter().equals(winningTeam) || user.getTeamFilter().equals(winningTeam)) {
						score.win();
					}
				} else {
					if (score.getScore() == winningScore) {
						score.win();
					}
				}
			}
		}

		beginTransition(TransitionState.RESULTS, true, resultsText, defaultFadeOutSpeed, deathFadeDelay);
		HadalGame.server.sendToAllTCP(new Packets.ClientStartTransition(TransitionState.RESULTS, true, resultsText, defaultFadeOutSpeed, deathFadeDelay));
	}
	
	/**
	 * This is used to make a specific player a spectator after a transition.
	 * This is only run by the server
	 */
	public void becomeSpectator(Player player) {
		
		if (!player.isSpectator()) {
			player.setSpectator(true);
			
			HadalGame.server.addNotificationToAll(this, "", player.getName() + " became a spectator!", DialogType.SYSTEM);
			
			//for host, start transition. otherwise, send transition packet
			if (this.player.equals(player)) {
				beginTransition(TransitionState.SPECTATOR, false, "", defaultFadeOutSpeed, deathFadeDelay);
			} else {
				HadalGame.server.sendToTCP(player.getConnID(), new Packets.ClientStartTransition(TransitionState.SPECTATOR, false, "", defaultFadeOutSpeed, deathFadeDelay));
			}
			
			//we die last so that the on-death transition does not occur (As it will not override the spectator transition unless it is a results screen.)
			player.getPlayerData().die(worldDummy.getBodyData(), DamageTypes.DISCONNECT);

			//set the spectator's player number to default so they don't take up a player slot
			User user = HadalGame.server.playerToUser(player);
			if (user != null) {
				user.getHitBoxFilter().setUsed(false);
				user.setHitBoxFilter(AlignmentFilter.NONE);
			}
		}
	}
	
	/**
	 * This is used to make a specific spectator a player after a transition.
	 * This is only run by the server
	 */
	public void exitSpectator(Player player) {
		
		if (player.isSpectator()) {
			//cannot exit spectator if server is full
			if (HadalGame.server.getNumPlayers() >= gsm.getSetting().getMaxPlayers() + 1) {
				HadalGame.server.sendNotification(player.getConnID(), "", "Could not join! Server is full!", DialogType.SYSTEM);
				return;
			}
			
			player.setSpectator(false);
			HadalGame.server.addNotificationToAll(this, "", player.getName() + " stopped spectating and joined the game!", DialogType.SYSTEM);

			//for host, start transition. otherwise, send transition packet
			if (this.player.equals(player)) {
				beginTransition(TransitionState.RESPAWN, false, "", defaultFadeOutSpeed, deathFadeDelay);
			} else {
				HadalGame.server.sendToTCP(player.getConnID(), new Packets.ClientStartTransition(TransitionState.RESPAWN, false, "", defaultFadeOutSpeed, deathFadeDelay));
			}

			//give the new player a player slot
			User user = HadalGame.server.playerToUser(player);
			if (user != null) {
				user.setHitBoxFilter(AlignmentFilter.getUnusedAlignment());
			}
		}
	}

	/**
	 * This is called whenever we transition to a new state. Begin transition and set new state.
	 * @param state: The state we are transitioning towards
	 * @param override: Does this transition override other transitions?
	 * @param resultsText: text to be displayed if we transition to a results screen (or for notification in title screen)
	 * @param fadeSpeed: speed of transition
	 * @param fadeDelay: amount of delay before transition
	 */
	public void beginTransition(TransitionState state, boolean override, String resultsText, float fadeSpeed, float fadeDelay) {
		
		//If we are already transitioning to a new results state, do not do this unless we tell it to override
		if (nextState == null || override) {
			this.resultsText = resultsText;
			nextState = state;
			gsm.getApp().fadeSpecificSpeed(fadeSpeed, fadeDelay);
			gsm.getApp().setRunAfterTransition(this::transitionState);
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
		beginTransition(TransitionState.TITLE, true, "", defaultFadeOutSpeed, delay);
	}
	
	/**
	 * This looks for an entity in the world with the given entityId
	 * this is kinda slow. don't overuse it.
	 */
	public HadalEntity findEntity(String entityId) {

		for (HadalEntity schmuck : entities) {
			if (schmuck.getEntityID().toString().equals(entityId)) {
				return schmuck;
			}
		}
		for (HadalEntity hitbox : hitboxes) {
			if (hitbox.getEntityID().toString().equals(entityId)) {
				return hitbox;
			}
		}
		return null;
	}
	
	/**
	 * This sets the game's boss, filling the boss ui.
	 * @param enemy: This is the boss whose hp will be used for the boss hp bar
	 */
	public void setBoss(Enemy enemy) {
		uiPlay.setBoss(enemy, enemy.getName());
	}
	
	/**
	 * This is called when the boss is defeated, clearing its hp bar from the ui.
	 * We also have to tell the client to do the same.
	 */
	public void clearBoss() {
		uiPlay.clearBoss();
	}
	
	/**
	 * This is called by the server when a new client connects. We catch up the client by making them create all existing entities.
	 * @param connId: connId of the new client
	 */
	public void catchUpClient(int connId) {
		for (HadalEntity entity : entities) {
			Object packet = entity.onServerCreate();
			if (packet != null) {
				HadalGame.server.sendToUDP(connId, packet);
			}
		}
		for (HadalEntity entity : hitboxes) {
			Object packet = entity.onServerCreate();
			if (packet != null) {
				HadalGame.server.sendToUDP(connId, packet);
			}
		}
	}
	
	/**
	 * This acquires the level's save points. If none, respawn at starting location. If many, choose one randomly
	 * @return a save point to spawn a respawned player at
	 */
	public StartPoint getSavePoint(String startId) {
		ArrayList<StartPoint> validStarts = new ArrayList<>();
		ArrayList<StartPoint> readyStarts = new ArrayList<>();
		
		//get a list of all start points that match the startId
		for (StartPoint s: savePoints) {
			if (s.getStartId().equals(startId)) {
				validStarts.add(s);
			}
		}
		
		//if no start points are found, we return the first save point (if existent)
		if (validStarts.isEmpty()) {
			if (savePoints.isEmpty()) {
				return null;
			} else {
				return savePoints.get(0);
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
			int randomIndex = GameStateManager.generator.nextInt(validStarts.size());
			return validStarts.get(randomIndex);
		} else {
			int randomIndex = GameStateManager.generator.nextInt(readyStarts.size());
			return readyStarts.get(randomIndex);
		}
	}
	
	/**
	 * This returns a single starting point for a newly spawned player to spawn at.
	 */
	public StartPoint getSavePoint() {
		return getSavePoint(startId);
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
	 * This is used for pvp levels. When a player is spawned, they will get their hitbox filter here so they can hit each other.
	 */
	private static short nextFilter = -5;
	public static short getPVPFilter() {
		nextFilter--;
		return nextFilter;
	}
	
	/**
	 * This sets a shader to be used as a "base-shader" for things like the background
	 */
	public void setShaderBase(Shader shader) {
		shaderBase = shader;
		shaderBase.loadShader();
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
	
	public boolean isPvp() { return pvp; }
	
	public boolean isHub() { return hub; }

	public boolean isTeamEnabled() { return teamEnabled; }

	public boolean isSpectatorMode() { return spectatorMode; }

	public void setUnlimitedLife(boolean lives) { this.unlimitedLife = lives; }
	
	public Event getGlobalTimer() {	return globalTimer;	}

	public void setGlobalTimer(Event globalTimer) {	this.globalTimer = globalTimer;	}

	public Player getPlayer() {	return player; }
	
	public void setPlayer(Player player) { this.player = player; }

	public World getWorld() { return world; }
	
	public WorldDummy getWorldDummy() { return worldDummy; }
	
	public AnchorPoint getAnchor() { return anchor; }

	public UnlockLevel getLevel() { return level; }

	public float getTimer() {return timer; }
	
	public void setTimer(float timer) { this.timer = timer; }

	public void setStartId(String startId) { this.startId = startId; }

	public void toggleVisibleHitboxes(boolean debugHitbox) { this.debugHitbox = debugHitbox; }
	
	public UIExtra getUiExtra() { return uiExtra; }

	public UIArtifacts getUiArtifact() { return uiArtifact; }
	
	public UIHub getUiHub() { return uiHub; }
	
	public UIObjective getUiObjective() { return uiObjective; }

	public PositionDummy getDummyPoint(String id) {	return dummyPoints.get(id); }
	
	public void addDummyPoint(PositionDummy dummy, String id) {	dummyPoints.put(id, dummy); }
	
	public void setCameraTarget(Vector2 cameraTarget) {	this.cameraTarget = cameraTarget; }
	
	public void setCameraOffset(float offsetX, float offsetY) {	cameraOffset.set(offsetX, offsetY); }
	
	public Vector2 getCameraTarget() {	return cameraTarget; }

	public float[] getCameraBounds() { return cameraBounds; }

	public float[] getSpectatorBounds() { return spectatorBounds; }

	public void setSpectatorBounded(boolean spectatorBounded) { this.spectatorBounded = spectatorBounded; }

	public MouseTracker getMouse() { return mouse; }

	public InputProcessor getController() { return controller; }

	public MessageWindow getMessageWindow() { return messageWindow; }
	
	public ChatWheel getChatWheel() { return chatWheel; }
	
	public ScoreWindow getScoreWindow() { return scoreWindow; }	
	
	public DialogBox getDialogBox() { return dialogBox; }

	public void setZoom(float zoom) { this.zoomDesired = zoom; }

	public ArrayList<Object> getSyncPackets() {	return syncPackets; }

	public void setNextState(TransitionState nextState) { this.nextState = nextState; }
}
