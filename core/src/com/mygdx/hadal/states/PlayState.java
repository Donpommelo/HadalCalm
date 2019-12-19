package com.mygdx.hadal.states;

import static com.mygdx.hadal.utils.Constants.PPM;

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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UIExtra;
import com.mygdx.hadal.actors.MessageWindow;
import com.mygdx.hadal.actors.ScoreWindow;
import com.mygdx.hadal.actors.UIActives;
import com.mygdx.hadal.actors.UIObjective;
import com.mygdx.hadal.actors.UIPlay;
import com.mygdx.hadal.actors.UIPlayClient;
import com.mygdx.hadal.actors.UIArtifacts;
import com.mygdx.hadal.equip.Loadout;
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
import com.mygdx.hadal.stages.PlayStateStage;
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
	private UnlockArtifact mapStartifact;
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
	protected HadalEntity cameraTarget;
	
	//These are the bounds of the camera movement
	private float[] cameraBounds = {0.0f, 0.0f, 0.0f, 0.0f};
	private boolean[] cameraBounded = {false, false, false, false};
	
	//If there is an objective target that has a display if offscreen, this is that entity.
	protected HadalEntity objectiveTarget;
	
	//This is the next state that we transition to if we are transitioning
	protected transitionState nextState;
	
	//The current zoom of the camera
	private float zoom;

	//This is the zoom that the camera will lerp towards
	protected float zoomDesired;
	
	//Starting position of players spawning into the world
	private int startX, startY;
	
	//If a player respawns, they will respawn at the coordinates of a safe point from this list.
	//That savepoint contains zoom and camera target that will be set.
	private ArrayList<SavePoint> savePoints;
	
	//This is an arrayList of ids to dummy events. These are used for enemy ai processing
	private HashMap<String, PositionDummy> dummyPoints;
	
	//Do players respawn after dying? Can players hurt each other? Is this a practice level (like the hub?) atm this only makes start-of-level effects not activate.
	protected boolean respawn, pvp, practice;
	
	//Is this playstate the server?
	protected boolean server;
	
	//Various play state ui elements
	private UIPlay uiPlay;
	private UIActives uiActive;
	private UIObjective uiObjective;
	private UIExtra uiExtra;
	protected UIArtifacts uiArtifact;
	protected MessageWindow messageWindow;
	protected ScoreWindow scoreWindow;
	
	//Background and black screen used for transitions
	protected Texture bg, black;
	
	private final static float defaultTransitionDelay = 0.5f;
	private final static float defaultFadeInSpeed = -0.015f;
	private final static float defaultFadeOutSpeed = 0.015f;
	
	//This is the amount of time between transition called for and fading actually beginning.
	protected float fadeInitialDelay = defaultTransitionDelay;
	
	//This is the how faded the black screen is. (starts off black)
	protected float fadeLevel = 1.0f;
			
	//This is how much the fade changes every engine tick (starts out fading in)
	protected float fadeDelta = defaultFadeInSpeed;
	
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
	
	/**
	 * Constructor is called upon player beginning a game.
	 * @param gsm: StateManager
	 */
	public PlayState(GameStateManager gsm, Loadout loadout, UnlockLevel level, boolean server, PlayerBodyData old, boolean reset, String startId) {
		super(gsm);

		this.server = server;
		
		//Maps can have a set loadout. This will override the loadout given as an input to the playstate.
		this.mapMultitools = level.getMultitools();
		this.mapStartifact = level.getStartifact();
		this.mapActiveItem = level.getActiveItem();
		this.level = level;
		this.startId = startId;
        
        //Initialize box2d world and related stuff
		world = new World(new Vector2(0, -9.81f), false);
		world.setContactListener(new WorldContactListener());
		World.setVelocityThreshold(0);

		b2dr = new Box2DDebugRenderer();
//		b2dr.setDrawBodies(false);
		
		//Initialize sets to keep track of active entities and packet effects
		entities = new LinkedHashSet<HadalEntity>();
		hitboxes = new LinkedHashSet<HadalEntity>();
		removeList = new LinkedHashSet<HadalEntity>();
		createList = new LinkedHashSet<HadalEntity>();
		removeListClient = new LinkedHashSet<String>();
		createListClient = new LinkedHashSet<Object[]>();
		packetEffects = Collections.synchronizedList(new ArrayList<PacketEffect>());
		
		//The "worldDummy" will be the source of map-effects that want a perpetrator
		worldDummy = new Enemy(this, 1, 1, -1000, -1000, enemyType.MISC, Constants.ENEMY_HITBOX, 100, null);
		
		//The mouse tracker is the player's mouse position
		mouse = new MouseTracker(this, true);

		//load map
		map = new TmxMapLoader().load(level.getMap());
		tmr = new OrthogonalTiledMapRenderer(map);

		//Get map settings from the collision layer of the map
		this.respawn = map.getProperties().get("respawn", false, Boolean.class);
		this.pvp = map.getProperties().get("pvp", false, Boolean.class);
		this.practice = map.getProperties().get("practice", false, Boolean.class);
		this.zoom = map.getProperties().get("zoom", 1.0f, float.class);
		this.zoomDesired = zoom;	

		//Clear events in the TiledObjectUtil to avoid keeping reference to previous map's events.
		TiledObjectUtil.clearEvents();
		
		//Only the server processes collision objects, events and triggers
		if (server) {
			TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision-layer").getObjects());
			TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get("event-layer").getObjects());
			TiledObjectUtil.parseTiledTriggerLayer();
		}
		
		//Create the player and make the camera focus on it
		this.player = createPlayer((int)(startX * PPM), (int)(startY * PPM), gsm.getRecord().getName(), loadout, old, reset);
		this.cameraTarget = player;
		this.camera.position.set(new Vector3(startX * PPM, startY * PPM, 0));
		this.sprite.position.set(new Vector3(startX * PPM, startY * PPM, 0));
		this.reset = reset;

		if (!reset) {
			fadeLevel = 0;
			fadeDelta = 0.0f;
		}
		
		controller = new PlayerController(player);	
		
		//Set up "save point" as starting point
		this.savePoints = new ArrayList<SavePoint>();
		savePoints.add(new SavePoint(new Vector2(startX, startY), zoomDesired, null));		
		
		//Set up dummy points (AI rally points)
		this.dummyPoints = new HashMap<String, PositionDummy>();
				
		//Init background image
		this.bg = HadalGame.assetManager.get(AssetList.BACKGROUND1.toString());
		this.black = HadalGame.assetManager.get(AssetList.BLACK.toString());
	}
			
	@Override
	public void show() {
		
		if (stage == null) {
			this.stage = new PlayStateStage(this) {
				
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
				uiPlay = new UIPlay(HadalGame.assetManager, this, player);
			} else {
				uiPlay = new UIPlayClient(HadalGame.assetManager, this, player);
			}
			
			uiActive = new UIActives(HadalGame.assetManager, player);
			uiObjective = new UIObjective(HadalGame.assetManager, this, player);
			uiArtifact = new UIArtifacts(HadalGame.assetManager, this, player);
			uiExtra = new UIExtra(HadalGame.assetManager, this);
			
			messageWindow = new MessageWindow(this);
			scoreWindow = new ScoreWindow(this);
		}
		
		//Add and sync ui elements in case of unpause or new playState
		this.stage.addActor(uiPlay);
		this.stage.addActor(uiActive);
		this.stage.addActor(uiObjective);
		this.stage.addActor(uiExtra);

		uiArtifact.addTable();
		uiArtifact.syncArtifact();

		messageWindow.addTable();
		scoreWindow.syncTable();
		
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
		uiActive.setPlayer(player);
		uiArtifact.setPlayer(player);
	}
	
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
		
		//The box2d world takes a step. This handles collisions + physics stuff. Maybe change delta to set framerate? 
		world.step(1 / 60f, 8, 3);
		
		//Let AI process time step
		GdxAI.getTimepiece().update(1 / 60f);
		
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
		
		//If we are in the delay period of a transition, decrement the delay
		if (fadeInitialDelay <= 0f) {
			
			if (fadeDelta < 0f) {
				
				//If we are fading in and not done yet, decrease fade.
				fadeLevel += fadeDelta;
				
				//If we just finished fading in, set fade to 0
				if (fadeLevel < 0f) {
					fadeLevel = 0f;
					fadeDelta = 0;
				}
			} else if (fadeDelta > 0f) {
				
				//If we are fading out and not done yet, increase fade.
				fadeLevel += fadeDelta;
				
				//If we just finished fading out, set fade to 1 and do a transition
				if (fadeLevel >= 1f) {
					fadeLevel = 1f;
					fadeDelta = 0;
					transitionState();
				}
			}
		} else {
			fadeInitialDelay -= delta;
		}
	}
	
	/**
	 * This method renders stuff to the screen after updating.
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(0/255f, 0/255f, 0/255f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Render Background
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		batch.draw(bg, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
		batch.end();
		
		//Render Tiled Map + world
		tmr.setView(sprite);
		tmr.render();				

		//Render debug lines for box2d objects.
		b2dr.render(world, camera.combined.scl(PPM));
		
		//Iterate through entities in the world to render
		batch.setProjectionMatrix(sprite.combined);
		batch.begin();

		for (HadalEntity hitbox : hitboxes) {
			if (hitbox.isVisible()) {
				hitbox.render(batch);
			}
		}
		for (HadalEntity schmuck : entities) {
			if (schmuck.isVisible()) {
				schmuck.render(batch);
			}
		}

		batch.end();
		
		//Render fade transitions
		if (fadeLevel > 0) {
			batch.setProjectionMatrix(hud.combined);
			batch.begin();
			batch.setColor(1f, 1f, 1f, fadeLevel);
			batch.draw(black, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
			batch.setColor(1f, 1f, 1f, 1);
			batch.end();
		}
	}	
	
	/**
	 * This is called every update. This resets the camera zoom and makes it move towards the player (or other designated target).
	 */
	Vector2 tmpVector2 = new Vector2();
	protected void cameraUpdate() {
		zoom = zoom + (zoomDesired - zoom) * 0.05f;
		
		camera.zoom = zoom;
		sprite.zoom = zoom;
		if (cameraTarget != null) {
			
			if (cameraTarget.getBody() != null && cameraTarget.isAlive()) {
				
				tmpVector2.set(cameraTarget.getPosition());
				
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
				
				tmpVector2.scl(PPM);
				CameraStyles.lerpToTarget(camera, tmpVector2);
				CameraStyles.lerpToTarget(sprite, tmpVector2);
			}
		}
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
	}
	
	/**
	 * This is called when ending a playstate by winning, losing or moving to a new playstate
	 */	
	public void transitionState() {
		switch (nextState) {
		case RESPAWN:
			SavePoint getSave = getSavePoint();
			
			//reset the player's camera to saved values
			boolean resetCamera = false;
			if (getSave.getZoomPoint() == null) {
				resetCamera = true;
			}
			
			//Create a new player
			player = createPlayer( 
					(int)(getSave.getLocation().x * PPM),
					(int)(getSave.getLocation().y * PPM), 
					gsm.getRecord().getName(), 
					player.getPlayerData().getLoadout(), player.getPlayerData(), true);
			
			((PlayerController)controller).setPlayer(player);
			
			this.zoomDesired = getSave.getZoom();
			if (resetCamera) {
				this.cameraTarget = player;
			} else {
				this.cameraTarget = getSave.getZoomPoint();
			}

			//Make the screen fade back in
			fadeDelta = defaultFadeInSpeed;
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case RESULTS:

			//get a results screen
			getGsm().addResultsState(this, resultsText, PlayState.class);
			break;
		case SPECTATOR:
			//When ded but other players alive, spectate a player
			
			//Make the screen fade back in
			fadeDelta = defaultFadeInSpeed;
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case NEWLEVEL:
			
			//remove this state and add a new play state with a fresh loadout
			getGsm().removeState(PlayState.class);
        	getGsm().addPlayState(nextLevel, new Loadout(gsm.getRecord()), null, TitleState.class, true, nextStartId);
			break;
		case NEXTSTAGE:
			
			//remove this state and add a new play state with the player's current loadout and stats
			getGsm().removeState(PlayState.class);
			getGsm().addPlayState(nextLevel, player.getPlayerData().getLoadout(), player.getPlayerData(), TitleState.class, false, nextStartId);
			break;
		case TITLE:
			getGsm().removeState(PlayState.class);
			break;
		default:
			break;
		}	
	}
	
	/**
	 * transition from one playstate to another with a new level.
	 * @param level: file of the new map
	 * @param state: this will either be newlevel or next stage to determine whether we reset hp
	 * @param instane: do we transition to the next area quickly?
	 */
	public void loadLevel(UnlockLevel level, transitionState state, boolean instant, String nextStartId) {
		
		//The client should never run this; instead transitioning when the server tells it to.
		if (!server) {
			return;
		}
		
		if (nextLevel == null) {
			
			if (instant) {
				
				//remove this state and add a new play state with the player's current loadout and stats
				getGsm().removeState(PlayState.class);
				getGsm().addPlayState(level, player.getPlayerData().getLoadout(), player.getPlayerData(), TitleState.class, false, nextStartId);
				
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
	 * @param x, y coordinates of the player's starting location
	 * @param name player name
	 * @param altLoadout the player's loadout
	 * @param old player's olf playerdata if retaining old values.
	 * @return
	 */
	public Player createPlayer(int x, int y, String name, Loadout altLoadout, PlayerBodyData old, boolean reset) {
		
		Loadout newLoadout = new Loadout(altLoadout);
		
		if (mapMultitools != null) {
			for (int i = 0; i < Loadout.getNumSlots(); i++) {
				if (mapMultitools.length > i) {
					newLoadout.multitools[i] = mapMultitools[i];
				}
			}
		}
		
		if (mapStartifact != null) {
			newLoadout.startifact = mapStartifact;
		}
		
		if (mapActiveItem != null) {
			newLoadout.activeItem = mapActiveItem;
		}
		
		return new Player(this, x, y, name, newLoadout, old, reset);
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
				
		if (!respawn) {
			
			//check if all players are out
			boolean allded = true;
			for (SavedPlayerFields f: HadalGame.server.getScores().values()) {
				if (f.isAlive()) {
					allded = false;
					break;
				}
			}
			if (allded) {
				
				resultsText = getGameOverText();
				
				beginTransition(transitionState.RESULTS, true, getGameOverText());
				HadalGame.server.sendToAllTCP(new Packets.ClientStartTransition(transitionState.RESULTS, true, getGameOverText()));
			} else {
				if (this.player.equals(player)) {
					beginTransition(transitionState.SPECTATOR, false, "");
				} else {
					
					//If a client dies, we tell them to transition to a spectator state.
					for (int connId : HadalGame.server.getPlayers().keySet()) {
						if (HadalGame.server.getPlayers().get(connId).equals(player)) {
							HadalGame.server.sendToTCP(connId, new Packets.ClientStartTransition(transitionState.SPECTATOR, false, ""));
						}
					}
				}
			}
		} else {
			if (this.player.equals(player)) {
				
				//Transition to the host respawning
				beginTransition(transitionState.RESPAWN, false, "");
			} else {
				
				//If a client dies, we tell them to transition to a respawn state.
				for (int connId : HadalGame.server.getPlayers().keySet()) {
					if (HadalGame.server.getPlayers().get(connId).equals(player)) {
						HadalGame.server.sendToTCP(connId, new Packets.ClientStartTransition(transitionState.RESPAWN, false, ""));
					}
				}
			}
		}
	}
	
	/**
	 * This is called when a level ends. Only called by the server. Begin a transition and tell all clients to follow suit.
	 * @param state: Is it ending as a gameover or a results screen?
	 */
	public void levelEnd(String text) {
		beginTransition(transitionState.RESULTS, false, text);
		HadalGame.server.sendToAllTCP(new Packets.ClientStartTransition(transitionState.RESULTS, false, text));
	}
	
	/**
	 * This is called whenever we transition to a new state. Begin transition and set new state.
	 * @param state: The state we are transitioning towards
	 * @param override: Does this transition override other transitions?
	 */
	public void beginTransition(transitionState state, boolean override, String resultsText) {

		//If we are already transitioning to a new results state, do not do this unless we tell it to override
		if (nextState == null || override) {
			this.resultsText = resultsText;
			nextState = state;
			fadeInitialDelay = defaultTransitionDelay;
			fadeDelta = defaultFadeOutSpeed;	
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
		beginTransition(transitionState.TITLE, true, "");
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
			HadalGame.server.sendToAllTCP(new Packets.SyncBoss());
		}
	}
	
	/**
	 * This gives a line of text on game over.
	 * @return
	 */
	public String getGameOverText() {
		return "YOU DED";
	}
	
	/**
	 * This is called by the server when a new client connects. We catch up the client by making them create all existing entities.
	 * @param connId: connId of the new client
	 */
	public void catchUpClient(int connId) {
		for (HadalEntity entity : entities) {
			Object packet = entity.onServerCreate();
			if (packet != null) {
				HadalGame.server.sendToTCP(connId, entity.onServerCreate());
			}
		}
		for (HadalEntity entity : hitboxes) {
			Object packet = entity.onServerCreate();
			if (packet != null) {
				HadalGame.server.sendToTCP(connId, entity.onServerCreate());
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
	 * @param effect
	 */
	public void addPacketEffect(PacketEffect effect) {
		synchronized(packetEffects) {
			packetEffects.add(effect);
		}
	}
	
	public boolean isPvp() {
		return pvp;
	}

	public boolean isPractice() {
		return practice;
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
	 * Getter for the player. This will return null if the player has not been spawned
	 * @return: The Player entity.
	 */
	public Player getPlayer() {
		return player;
	}

	public World getWorld() {
		return world;
	}
	
	public Enemy getWorldDummy() {
		return worldDummy;
	}

	public UnlockLevel getLevel() {
		return level;
	}
	
	public String getStartId() {
		return startId;
	}

	public void setStartId(String startId) {
		this.startId = startId;
	}

	public PlayStateStage getPlayStateStage() {
		return (PlayStateStage) stage;
	}
	
	public UIExtra getUiExtra() {
		return uiExtra;
	}

	public UIArtifacts getUiArtifact() {
		return uiArtifact;
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}
	
	public void setStart(int x, int y) {
		this.startX = (int) (x / PPM);
		this.startY = (int) (y / PPM);
	}
	
	public SavePoint getSavePoint() {
		if (savePoints.isEmpty()) {
			return new SavePoint(new Vector2(startX, startY), zoomDesired, null);
		}
		int randomIndex = GameStateManager.generator.nextInt(savePoints.size());
		return savePoints.get(randomIndex);
	}
	
	public void addSavePoint(Vector2 pos, float zoom, HadalEntity target, boolean clear) {
		if (clear) {
			savePoints.clear();
		}
		savePoints.add(new SavePoint(pos, zoom, target));
	}

	public PositionDummy getDummyPoint(String id) {
		return dummyPoints.get(id);
	}
	
	public void addDummyPoint(PositionDummy dummy, String id) {
		dummyPoints.put(id, dummy);
	}
	
	public void setCameraTarget(HadalEntity cameraTarget) {
		this.cameraTarget = cameraTarget;
	}
	
	public float[] getCameraBounds() {
		return cameraBounds;
	}

	public boolean[] getCameraBounded() {
		return cameraBounded;
	}

	public HadalEntity getObjectiveTarget() {
		return objectiveTarget;
	}
	
	public MouseTracker getMouse() {
		return mouse;
	}

	public InputProcessor getController() {
		return controller;
	}

	public MessageWindow getMessageWindow() {
		return messageWindow;
	}
	
	public ScoreWindow getScoreWindow() {
		return scoreWindow;
	}
	
	public void setObjectiveTarget(HadalEntity objectiveTarget) {
		this.objectiveTarget = objectiveTarget;
	}

	public float getZoom() {
		return zoom;
	}
	
	public void setZoom(float zoom) {
		this.zoomDesired = zoom;
	}

	public boolean isRespawn() {
		return respawn;
	}
		
	public boolean isServer() {
		return server;
	}

	public boolean isReset() {
		return reset;
	}
	
	/**
	 * Tentative tracker of player kill number.
	 * @param i: Number to increase score by.
	 */
	public void incrementScore(int i) {
		uiExtra.incrementScore(i);
	}
	
	public enum transitionState {
		RESPAWN,
		RESULTS,
		SPECTATOR,
		NEWLEVEL,
		NEXTSTAGE,
		TITLE
	}
}
