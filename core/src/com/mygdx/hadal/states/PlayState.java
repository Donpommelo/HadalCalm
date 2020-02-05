package com.mygdx.hadal.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UIExtra;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.DialogBox;
import com.mygdx.hadal.actors.MessageWindow;
import com.mygdx.hadal.actors.ScoreWindow;
import com.mygdx.hadal.actors.UIObjective;
import com.mygdx.hadal.actors.UIPlay;
import com.mygdx.hadal.actors.UIPlayClient;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.actors.UIArtifacts;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.utility.PositionDummy;
import com.mygdx.hadal.handlers.WorldContactListener;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy.enemyType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.PacketEffect;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.schmucks.SavePoint;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.utils.CameraStyles;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The PlayState is the main state of the game and holds the Box2d world, all characters + gameplay.
 * @author Zachary Tu
 *
 */
public class PlayState extends GameState {
	
	//This is an entity representing the player. Atm, player is not initialized here, but rather by a "Player Spawn" event in the map.
	protected Player player;
	
	//This is the player's controller that receives inputs
	protected InputProcessor controller;
	
	//This is the loadout that the player starts off with when they enter the playstate
	private UnlockEquip[] mapMultitools;
	private UnlockArtifact[] mapArtifacts;
	private UnlockActives mapActiveItem;
	
	//These process and store the map parsed from the Tiled file.
	protected TiledMap map;
	protected OrthogonalTiledMapRenderer tmr;
	
	//world manages the Box2d world and physics. b2dr renders debug lines for testing
	protected Box2DDebugRenderer b2dr;
	protected World world;

	//This holds the mouse location
	private MouseTracker mouse;

	//These represent the set of entities to be added to/removed from the world. This is necessary to ensure we do this between world steps.
	private Set<HadalEntity> removeList;
	private Set<HadalEntity> createList;
	
	//This is a set of all non-hitbox entities in the world
	private Set<HadalEntity> entities;
	//This is a set of all hitboxes. This is separate to draw hitboxes underneath other bodies
	private Set<HadalEntity> hitboxes;
	
	//These sets are used by the Client for removing/adding entities.
	protected Set<String> removeListClient;
	protected Set<Object[]> createListClient;
	
	//This is a list of packetEffects, given when we receive packets with effects that we want to run in update() rather than whenever
	protected List<PacketEffect> packetEffects;
	
	//sourced effects from the world are attributed to this dummy.
	private Enemy worldDummy;
	
	//this is the current level
	protected UnlockLevel level;
	
	//This is the id of the start event that we will be spawning on
	private String startId;
	
	//This is the entity that the camera tries to focus on
	protected Vector2 cameraTarget;
	
	//These are the bounds of the camera movement
	private float[] cameraBounds = {0.0f, 0.0f, 0.0f, 0.0f};
	private boolean[] cameraBounded = {false, false, false, false};
	
	//If there is an objective target that has a display if offscreen, this is that entity.
	protected HadalEntity objectiveTarget;
	
	private boolean unlimitedLife;
	
	//The current zoom of the camera
	private float zoom;

	//This is the zoom that the camera will lerp towards
	protected float zoomDesired;
	
	//Starting position of players spawning into the world
	private Vector2 startPosition = new Vector2();
	
	//If a player respawns, they will respawn at the coordinates of a safe point from this list.
	//That savepoint contains zoom and camera target that will be set.
	private ArrayList<SavePoint> savePoints;
	
	//This is an arrayList of ids to dummy events. These are used for enemy ai processing
	private HashMap<String, PositionDummy> dummyPoints;
	
	//Can players hurt each other? Is it the hub map? Is this the server?
	protected boolean pvp, hub, server;
	
	//Various play state ui elements
	protected UIPlay uiPlay;
	protected UIObjective uiObjective;
	protected UIExtra uiExtra;
	protected UIArtifacts uiArtifact;
	protected UIHub uiHub;
	protected MessageWindow messageWindow;
	protected ScoreWindow scoreWindow;
	protected DialogBox dialogBox;
	
	//Background and black screen used for transitions
	protected Texture bg;
	protected Shader shaderBase;
	
	protected TransitionState nextState;
	
	//If we are transitioning to another level, this is that level.
	private UnlockLevel nextLevel;
	private String nextStartId;
	
	//If we are transitioning to a results screen, this is the displayed text;
	protected String resultsText;
	
	//Has the server finished loading yet?
	private boolean serverLoaded = false;
	
	//Do players connecting to this have their hp/ammo/etc reset?
	private boolean reset;
	
	//global variables
	public static final float spriteAnimationSpeed = 0.08f;
	
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
	 * @startId: th id of the starting event the player should be spawned at
	 */
	public PlayState(GameStateManager gsm, Loadout loadout, UnlockLevel level, boolean server, PlayerBodyData old, boolean reset, String startId) {
		super(gsm);

		this.server = server;
		
		//Maps can have a set loadout. This will override the loadout given as an input to the playstate.
		this.mapMultitools = level.getMultitools();
		this.mapArtifacts = level.getArtifacts();
		this.mapActiveItem = level.getActiveItem();
		this.level = level;
		this.startId = startId;
        
        //Initialize box2d world and related stuff
		world = new World(new Vector2(0, -9.81f), false);
		world.setContactListener(new WorldContactListener());
		World.setVelocityThreshold(0);

		b2dr = new Box2DDebugRenderer();
		b2dr.setDrawBodies(false);
		
		//Initialize sets to keep track of active entities and packet effects
		entities = new LinkedHashSet<HadalEntity>();
		hitboxes = new LinkedHashSet<HadalEntity>();
		removeList = new LinkedHashSet<HadalEntity>();
		createList = new LinkedHashSet<HadalEntity>();
		removeListClient = new LinkedHashSet<String>();
		createListClient = new LinkedHashSet<Object[]>();
		packetEffects = Collections.synchronizedList(new ArrayList<PacketEffect>());
		
		//The "worldDummy" will be the source of map-effects that want a perpetrator
		worldDummy = new Enemy(this, new Vector2(-1000, -1000), new Vector2(1, 1), new Vector2(1, 1), enemyType.MISC, Constants.ENEMY_HITBOX, 100, null);
		
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

		this.shaderBase = Shader.NOTHING;
		if (map.getProperties().get("shader", String.class) != null) {
			shaderBase = Shader.valueOf(map.getProperties().get("shader", String.class));
			shaderBase.loadShader(this, null, 0);
		}
		
		//Clear events in the TiledObjectUtil to avoid keeping reference to previous map's events.
		TiledObjectUtil.clearEvents();
		
		//Set up "save point" as starting point
		this.savePoints = new ArrayList<SavePoint>();
				
		//Only the server processes collision objects, events and triggers
		if (server) {
			TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision-layer").getObjects());
			TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get("event-layer").getObjects());
			TiledObjectUtil.parseTiledTriggerLayer();
			TiledObjectUtil.parseDesignatedEvents(this);
		}
		TiledObjectUtil.parseTiledCommonEventLayer(this, map.getLayers().get("event-layer").getObjects());

		savePoints.add(new SavePoint(startPosition, null, zoomDesired));
		
		//Create the player and make the camera focus on it
		SavePoint getSave = getSavePoint();
		this.player = createPlayer(getSave.getLocation(), gsm.getRecord().getName(), loadout, old, 0, reset);
		
		this.camera.position.set(new Vector3(getSave.getLocation().x, getSave.getLocation().y, 0));
		this.reset = reset;
		
		controller = new PlayerController(player);	
		
		//Set up dummy points (AI rally points)
		this.dummyPoints = new HashMap<String, PositionDummy>();
				
		//Init background image
		this.bg = HadalGame.assetManager.get(AssetList.BACKGROUND2.toString());
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
				uiPlay = new UIPlay(this, player);
			} else {
				uiPlay = new UIPlayClient(this, player);
			}
			
			uiObjective = new UIObjective(this, player);
			uiArtifact = new UIArtifacts(this, player);
			uiExtra = new UIExtra(this);
			uiHub = new UIHub(this);
			
			messageWindow = new MessageWindow(this);
			scoreWindow = new ScoreWindow(this);
			dialogBox = new DialogBox(this, 0, HadalGame.CONFIG_HEIGHT);
		}
		
		//Add and sync ui elements in case of unpause or new playState
		this.stage.addActor(uiPlay);
		this.stage.addActor(uiObjective);
		this.stage.addActor(uiExtra);
		this.stage.addActor(dialogBox);

		app.newMenu(stage);
		resetController();
	}

	/**
	 * This method gives input to the player as well as the menu.
	 * This is called when a player is created.
	 */
	public void resetController() {
		controller = new PlayerController(player);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		
		inputMultiplexer.addProcessor(stage);
		
		inputMultiplexer.addProcessor(controller);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		uiPlay.setPlayer(player);
		uiArtifact.setPlayer(player);
	}
	
	private float accumulator = 0.0f;
	private final static float physicsTime = 1 / 300f;
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
		
		accumulator += delta;
		
		while (accumulator >= physicsTime) {
			accumulator -= physicsTime;
			
			//The box2d world takes a step. This handles collisions + physics stuff. Maybe change delta to set framerate? 
			world.step(physicsTime, 6, 2);
			
			//Let AI process time step
			GdxAI.getTimepiece().update(physicsTime);
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
				HadalGame.server.sendToAllTCP(packet);
			}
		}
		createList.clear();
				
		//All entities that are set to be removed are removed.
		for (HadalEntity entity: removeList) {
			entities.remove(entity);
			hitboxes.remove(entity);
			entity.dispose();
			
			//Upon deleting an entity, tell the clients so they can follow suit.
			HadalGame.server.sendToAllTCP(new Packets.DeleteEntity(entity.getEntityID().toString()));
		}
		removeList.clear();
		
		//This processes all entities in the world. (for example, player input/cooldowns/enemy ai)
		//We also send client a sync packet if the entity requires.
		if (HadalGame.server.getServer() != null) {
			for (HadalEntity entity : hitboxes) {
				entity.controller(delta);
				entity.onServerSync();
			}
			for (HadalEntity entity : entities) {
				entity.controller(delta);
				entity.onServerSync();
			}
		}
		
		processCommonStateProperties(delta);
	}
	
	/**
	 * This method renders stuff to the screen after updating.
	 */
	private float timer;
	@Override
	public void render(float delta) {
		timer += delta;
		
		Gdx.gl.glClearColor(0/255f, 0/255f, 0/255f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Render Background
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		if (shaderBase.getShader() != null) {
			shaderBase.getShader().begin();
			shaderBase.shaderUpdate(this, timer);
			shaderBase.getShader().end();
			batch.setShader(shaderBase.getShader());
		}

		batch.draw(bg, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
		
		if (shaderBase.getShader() != null) {
			if (shaderBase.isBackground()) {
				batch.setShader(null);
			}
		}
		batch.end();
		
		//Render Tiled Map + world
		tmr.setView(camera);
		tmr.render();				

		//Render debug lines for box2d objects.
		//b2dr.render(world, camera.combined.scl(32));
		
		//Iterate through entities in the world to render visible entities
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		renderEntities(delta);
		
		if (shaderBase.getShader() != null) {
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
	public void processCommonStateProperties(float delta) {
		//When we receive packets and don't want to process their effects right away, we store them in packetEffects
		//to run here. This way, they will be carried out at a predictable time.
		synchronized(packetEffects) {
			for (PacketEffect effect: packetEffects) {
				effect.execute();
			}
			packetEffects.clear();
		}
		
		//Update the game camera and batch.
		cameraUpdate();
		
		//Increment the game timer, if exists
		uiExtra.incrementTimer(delta);
	}
	
	/**
	 * Render all entities in the world
	 */
	public void renderEntities(float delta) {
		for (HadalEntity hitbox : hitboxes) {
			renderEntity(hitbox, delta);
		}
		for (HadalEntity schmuck : entities) {
			renderEntity(schmuck, delta);
		}
	}
	
	/**
	 * This method renders a single entity.
	 * @param entity
	 * @param delta
	 */
	public void renderEntity(HadalEntity entity, float delta) {
		
		entity.decreaseShaderCount(delta);
		entity.increaseAnimationTime(delta);
		
		if (entity.isVisible()) {
			if (entity.getShaderCount() > 0) {
				batch.setShader(entity.getShader());
			}
			
			entity.render(batch);
			
			if (entity.getShaderCount() > 0) {
				batch.setShader(null);
			}
		}
	}
	
	/**
	 * This is called every update. This resets the camera zoom and makes it move towards the player (or other designated target).
	 */
	Vector2 tmpVector2 = new Vector2();
	protected void cameraUpdate() {
		zoom = zoom + (zoomDesired - zoom) * 0.05f;
		
		camera.zoom = zoom;
		
		if (cameraTarget == null) {
			if (player.getBody() != null && player.isAlive()) {
				tmpVector2.set(player.getPixelPosition());
			} else {
				return;
			}
		} else {
			tmpVector2.set(cameraTarget);
		}
		
		//make camera target respect camera bounds
		if (cameraBounded[0] && tmpVector2.x > cameraBounds[0]) {
			tmpVector2.x = cameraBounds[0];
		}
		
		if (cameraBounded[1] && tmpVector2.x < cameraBounds[1]) {
			tmpVector2.x = cameraBounds[1];
		}		
		
		if (cameraBounded[2] && tmpVector2.y > cameraBounds[2]) {
			tmpVector2.y = cameraBounds[2];
		}
		
		if (cameraBounded[3] && tmpVector2.y < cameraBounds[3]) {
			tmpVector2.y = cameraBounds[3];
		}
		
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
		for (HadalEntity hitbox : removeList) {
			hitbox.dispose();
		}
		
		world.dispose();
		tmr.dispose();
		map.dispose();
		if (stage != null) {
			stage.dispose();
		}
		if(shaderBase.getShader() != null) {
			shaderBase.getShader().dispose();
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
		
		if(shaderBase.getShader() != null) {
			shaderBase.getShader().begin();
			shaderBase.shaderResize(this);
			shaderBase.getShader().end();
		}
	}
	
	/**
	 * This is called when ending a playstate by winning, losing or moving to a new playstate
	 */	
	public void transitionState() {
		
		switch (nextState) {
		case RESPAWN:
			SavePoint getSave = getSavePoint();
			
			//Create a new player
			player = createPlayer(getSave.getLocation(), gsm.getRecord().getName(), player.getPlayerData().getLoadout(), player.getPlayerData(), 0, true);
			
			((PlayerController)controller).setPlayer(player);
			
			this.cameraTarget = getSave.getZoomLocation();
			this.zoomDesired = getSave.getZoom();

			//Make nextState null so we can transition again
			nextState = null;
			break;
		case RESULTS:

			//get a results screen
			gsm.addResultsState(this, resultsText, PlayState.class);
			break;
		case SPECTATOR:
			//When ded but other players alive, spectate a player
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case NEWLEVEL:
			
			//remove this state and add a new play state with a fresh loadout
			gsm.removeState(PlayState.class);
			gsm.addPlayState(nextLevel, new Loadout(gsm.getRecord()), null, TitleState.class, true, nextStartId);
			break;
		case NEXTSTAGE:
			
			//remove this state and add a new play state with the player's current loadout and stats
			gsm.removeState(PlayState.class);
			gsm.addPlayState(nextLevel, player.getPlayerData().getLoadout(), player.getPlayerData(), TitleState.class, false, nextStartId);
			break;
		case TITLE:
			gsm.removeState(PlayState.class);
			break;
		default:
			break;
		}
	}
	
	/**
	 * transition from one playstate to another with a new level.
	 * @param level: file of the new map
	 * @param state: this will either be newlevel or next stage to determine whether we reset hp
	 * @param instant: do we transition to the next area quickly?
	 * @param nextStartId: The id of the start point to start at (if specified)
	 */
	public void loadLevel(UnlockLevel level, TransitionState state, boolean instant, String nextStartId) {
		
		//The client should never run this; instead transitioning when the server tells it to.
		if (!server) {
			return;
		}
		
		if (nextLevel == null) {
			
			if (instant) {
				
				//remove this state and add a new play state with the player's current loadout and stats
				gsm.removeState(PlayState.class);
				gsm.addPlayState(level, player.getPlayerData().getLoadout(), player.getPlayerData(), TitleState.class, false, nextStartId);
				
			} else {
				
				//begin transitioning to the designated next level
				nextLevel = level;
				this.nextStartId = nextStartId;
				beginTransition(state, false, "");
				
				//Server tells clients to begin a transition to the new state
				HadalGame.server.sendToAllTCP(new Packets.ClientStartTransition(nextState, false, ""));
			}
		}
	}
	
	/**This creates a player to occupy the playestate
	 * @param startPosition: coordinates of the player's starting location
	 * @param name: player name
	 * @param altLoadout: the player's loadout
	 * @param old: player's old playerdata if retaining old values.
	 * @param connID: the player's connection id (0 if server)
	 * @param reset: should we reset the new player's hp/fuel/ammo?
	 * @return the newly created player
	 */
	public Player createPlayer(Vector2 startPosition, String name, Loadout altLoadout, PlayerBodyData old, int connID, boolean reset) {

		Loadout newLoadout = new Loadout(altLoadout);
		
		//for pvp matches, set loadout depending on pvp settings
		if (pvp && !hub) {
			switch(gsm.getSetting().getLoadoutType()) {
			
			//default setting: each player starts with a basic weapon
			case 0:
				newLoadout.multitools[0] = UnlockEquip.SPEARGUN;
				for (int i = 1; i < Loadout.maxWeaponSlots; i++) {
					newLoadout.multitools[i] = UnlockEquip.NOTHING;
				}
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
		
		return new Player(this, startPosition, name, newLoadout, old, connID, reset);
	}
	
	/**
	 * This is called whenever a player is killed. This is only called by the server.
	 * @param player: The player that dies
	 * @param perp: the schmuck that killed
	 */
	public void onPlayerDeath(Player player, Schmuck perp) {
		
		//Register the kill for score keeping purposes
		if (perp instanceof Player) {
			HadalGame.server.registerKill((Player)perp, player);
		} else {
			HadalGame.server.registerKill(null, player);
		}
				
		if (!unlimitedLife) {
			
			String resultsText = "";
			
			//check if all players are out
			boolean allded = true;
			
			//in pvp, game ends if all players left are on the same team
			if (pvp) {
				
				short factionLeft = -1;
				for (int f: HadalGame.server.getScores().keySet()) {
					if (HadalGame.server.getScores().get(f).getLives() > 0) {
						Player playerLeft;
						if (f == 0) {
							playerLeft = this.player;
						} else {
							playerLeft = HadalGame.server.getPlayers().get(f);
						}
						
						if (playerLeft != null) {
							resultsText = playerLeft.getName() + " WINS";
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
				for (SavedPlayerFields f: HadalGame.server.getScores().values()) {
					if (f.getLives() > 0) {
						allded = false;
						break;
					}
				}
			}
			
			//if athe match is over (all players dead in co-op or all but one team dead in pvp), all players go to results screen
			if (allded) {
				beginTransition(TransitionState.RESULTS, true, resultsText);
				HadalGame.server.sendToAllTCP(new Packets.ClientStartTransition(TransitionState.RESULTS, true, resultsText));
			} else {
				
				//the player that dies respawns if there are lives left and becomes a spectator otherwise
				if (this.player.equals(player)) {
					if (HadalGame.server.getScores().get(player.getConnID()).getLives() > 0) {
						beginTransition(TransitionState.RESPAWN, false, "");
					} else {
						beginTransition(TransitionState.SPECTATOR, false, "");
					}
				} else {
					
					//If a client dies, we tell them to transition to a spectator or respawn state.
					if (HadalGame.server.getScores().get(player.getConnID()).getLives() > 0) {
						HadalGame.server.sendToTCP(player.getConnID(), new Packets.ClientStartTransition(TransitionState.RESPAWN, false, ""));
					} else {
						HadalGame.server.sendToTCP(player.getConnID(), new Packets.ClientStartTransition(TransitionState.SPECTATOR, false, ""));
					}
				}
			}
		} else {
			
			//if there are infinite lives, we respawn the dead player
			if (this.player.equals(player)) {
				beginTransition(TransitionState.RESPAWN, false, "");
			} else {
				HadalGame.server.sendToTCP(player.getConnID(), new Packets.ClientStartTransition(TransitionState.RESPAWN, false, ""));
			}
		}
	}
	
	/**
	 * This is called when a level ends. Only called by the server. Begin a transition and tell all clients to follow suit.
	 * @param state: Is it ending as a gameover or a results screen?
	 */
	public void levelEnd(String text) {
		beginTransition(TransitionState.RESULTS, false, text);
		HadalGame.server.sendToAllTCP(new Packets.ClientStartTransition(TransitionState.RESULTS, false, text));
	}
	
	/**
	 * This is called whenever we transition to a new state. Begin transition and set new state.
	 * @param state: The state we are transitioning towards
	 * @param override: Does this transition override other transitions?
	 */
	public void beginTransition(TransitionState state, boolean override, String resultsText) {
		//If we are already transitioning to a new results state, do not do this unless we tell it to override
		if (nextState == null || override) {
			this.resultsText = resultsText;
			nextState = state;
			gsm.getApp().fadeOut();
			gsm.getApp().setRunAfterTransition(new Runnable() {

				@Override
				public void run() {
					transitionState();
				}
			});
		}
	}
	
	/**
	 * Return to the title screen after a disconnect or selecting return in the pause menu. Overrides other transitions.
	 */
	public void returnToTitle() {
		if (server) {
			if (HadalGame.server.getServer() != null) {
				HadalGame.server.getServer().stop();
			}
		} else {
			HadalGame.client.client.stop();
		}
		beginTransition(TransitionState.TITLE, true, "");
	}
	
	/**
	 * This sets the game's boss, filling the boss ui.
	 * @param enemy: THis is the boss whose hp will be used for the boss hp bar
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
		if (server) {
			HadalGame.server.sendToAllTCP(new Packets.SyncBoss(0.0f));
		}
	}
	
	/**
	 * This is called by the server when a new client connects. We catch up the client by making them create all existing entities.
	 * @param connId: connId of the new client
	 */
	public void catchUpClient(int connId) {
		for (HadalEntity entity : entities) {
			Object packet = entity.onServerCreate();
			if (packet != null) {
				HadalGame.server.sendToTCP(connId, packet);
			}
		}
		for (HadalEntity entity : hitboxes) {
			Object packet = entity.onServerCreate();
			if (packet != null) {
				HadalGame.server.sendToTCP(connId, packet);
			}
		}
	}
	
	/**
	 * This acquires the level's save points. If none, respawn at starting location. If many, choose one randomly
	 * @return a save point to spawn a respawned player at
	 */
	public SavePoint getSavePoint() {
		if (savePoints.isEmpty()) {
			return new SavePoint(startPosition, null, zoomDesired);
		}
		int randomIndex = GameStateManager.generator.nextInt(savePoints.size());
		return savePoints.get(randomIndex);
	}
	
	/**
	 * This adds a save point to the list of available spawns
	 * @param pos: the position the player will respawn at
	 * @param zoomPos: the position that the camera will be zoomed on (if null, focus o nthe player)
	 * @param zoom: the amount that the camera should zoom in
	 * @param clear: should we remove all existing save points before adding this one?
	 */
	public void addSavePoint(Vector2 pos, Vector2 zoomPos, float zoom, boolean clear) {
		if (clear) {
			savePoints.clear();
		}
		savePoints.add(new SavePoint(pos, zoomPos, zoom));
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
	 * @param effect
	 */
	public void addPacketEffect(PacketEffect effect) {
		synchronized(packetEffects) {
			packetEffects.add(effect);
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
	
	public void setShaderBase(Shader shader) {
		shaderBase = shader;
		shaderBase.loadShader(this, null, 0);
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
	
	public boolean isUnlimitedLife() {return unlimitedLife; }
	
	public void setUnlimitedLife(boolean lives) { this.unlimitedLife = lives; }
	
	public Event getGlobalTimer() {	return globalTimer;	}

	public void setGlobalTimer(Event globalTimer) {	this.globalTimer = globalTimer;	}

	public Player getPlayer() {	return player; }

	public World getWorld() { return world; }
	
	public Enemy getWorldDummy() { return worldDummy; }

	public UnlockLevel getLevel() { return level; }
	
	public String getStartId() { return startId; }

	public void setStartId(String startId) { this.startId = startId; }

	public UIExtra getUiExtra() { return uiExtra; }

	public UIArtifacts getUiArtifact() { return uiArtifact; }
	
	public UIHub getUiHub() { return uiHub; }

	public Vector2 getStartPosition() {	return startPosition; }
	
	public PositionDummy getDummyPoint(String id) {	return dummyPoints.get(id); }
	
	public void addDummyPoint(PositionDummy dummy, String id) {	dummyPoints.put(id, dummy); }
	
	public void setCameraTarget(Vector2 cameraTarget) {	this.cameraTarget = cameraTarget; }
	
	public float[] getCameraBounds() { return cameraBounds; }

	public void setCameraBounds(float[] cameraBounds) { this.cameraBounds = cameraBounds; }

	public boolean[] getCameraBounded() { return cameraBounded; }

	public void setCameraBounded(boolean[] cameraBounded) { this.cameraBounded = cameraBounded; }
	
	public HadalEntity getObjectiveTarget() { return objectiveTarget; }
	
	public MouseTracker getMouse() { return mouse; }

	public InputProcessor getController() { return controller; }

	public MessageWindow getMessageWindow() { return messageWindow; }
	
	public ScoreWindow getScoreWindow() { return scoreWindow; }	
	
	public DialogBox getDialogBox() { return dialogBox; }
	
	public void setObjectiveTarget(HadalEntity objectiveTarget) { this.objectiveTarget = objectiveTarget; }

	public float getZoom() { return zoom; }
	
	public void setZoom(float zoom) { this.zoomDesired = zoom; }
}
