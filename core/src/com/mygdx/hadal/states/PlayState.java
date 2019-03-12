package com.mygdx.hadal.states;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.LinkedHashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UIExtra;
import com.mygdx.hadal.actors.UIActives;
import com.mygdx.hadal.actors.UIObjective;
import com.mygdx.hadal.actors.UIPlay;
import com.mygdx.hadal.actors.UIReload;
import com.mygdx.hadal.actors.UIStatuses;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.handlers.WorldContactListener;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.save.Record;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.stages.PlayStateStage;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.utils.CameraStyles;
import com.mygdx.hadal.utils.TiledObjectUtil;

import box2dLight.RayHandler;

/**
 * The PlayState is the main state of the game and holds the Box2d world, all characters + gameplay.
 * @author Zachary Tu
 *
 */
public class PlayState extends GameState {
	
	//This is an entity representing the player. Atm, player is not initialized here, but rather by a "Player Spawn" event in the map.
	protected Player player;
	
	private PlayerController controller;
	
	private Loadout loadout;
	
	//These process and store the map parsed from the Tiled file.
	protected TiledMap map;
	protected OrthogonalTiledMapRenderer tmr;
    
    //rays will implement lighting.
	protected RayHandler rays;
	
	//world manages the Box2d world and physics. b2dr renders debug lines for testing
	protected Box2DDebugRenderer b2dr;
	protected World world;

	//This holds the mouse location
	private MouseTracker mouse;

	//These represent the set of entities to be added to/removed from the world. This is necessary to ensure we do this between world steps.
	protected Set<HadalEntity> removeList;
	protected Set<HadalEntity> createList;
	
	//This is a set of all non-hitbox entities in the world
	private Set<HadalEntity> entities;
	//This is a set of all hitboxes. This is separate to draw hitboxes underneath other bodies
	private Set<HadalEntity> hitboxes;
	
	//sourced effects from the world are attributed to this dummy.
	private Enemy worldDummy;
	
	//this is the current level
	protected UnlockLevel level;
	
	protected HadalEntity cameraTarget;
	protected HadalEntity objectiveTarget;
	
	private transitionState nextState;
	
	private float zoom, zoomDesired;
	private int startX, startY;
	private int safeX, safeY;
	private float saveZoom;
	protected HadalEntity saveCameraPoint;
	
	private boolean realFite;
	
	protected PlayStateStage stage;
	
	private UIPlay uiPlay;
	private UIReload uiReload;
	private UIActives uiActive;
	private UIObjective uiObjective;
	private UIExtra uiExtra;
	private UIStatuses uiStatus;
	
	protected Texture bg, black;
	
	protected float fadeInitialDelay = 1.0f;
	protected float fadeLevel = 1f, fadeDelta = -0.015f;
	private UnlockLevel nextLevel;
	
	/**
	 * Constructor is called upon player beginning a game.
	 * @param gsm: StateManager
	 */
	public PlayState(GameStateManager gsm, Loadout loadout, UnlockLevel level, boolean realFite, PlayerBodyData old) {
		super(gsm);

		this.realFite = realFite;
		
		if (level.getLoadout() != null) {
			this.loadout = level.getLoadout();
		} else {
			this.loadout = loadout;
		}
		this.level = level;
        
        //Initialize box2d world and related stuff
		world = new World(new Vector2(0, -9.81f), false);
		world.setContactListener(new WorldContactListener());
		World.setVelocityThreshold(0);
		
		rays = new RayHandler(world);
        rays.setAmbientLight(1.0f);
        rays.setCulling(false);
        
 //       RayHandler.useDiffuseLight(true);
        rays.setCombinedMatrix(camera);

		b2dr = new Box2DDebugRenderer();
//		b2dr.setDrawBodies(false);
		
		//Initialize sets to keep track of active entities
		removeList = new LinkedHashSet<HadalEntity>();
		createList = new LinkedHashSet<HadalEntity>();
		entities = new LinkedHashSet<HadalEntity>();
		hitboxes = new LinkedHashSet<HadalEntity>();
		
		//The "worldDummy" will be the source of map-effects that want a perpetrator
		worldDummy = new Enemy(this, 1, 1, -1000, -1000);
		mouse = new MouseTracker(this, true);

		map = new TmxMapLoader().load(level.getMap());
		
		tmr = new OrthogonalTiledMapRenderer(map);
		
		this.startX = map.getLayers().get("collision-layer").getProperties().get("startX", 0, Integer.class);
		this.startY = map.getLayers().get("collision-layer").getProperties().get("startY", 0, Integer.class);
		
		TiledObjectUtil.clearEvents();
		TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision-layer").getObjects());
		TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get("event-layer").getObjects());
		TiledObjectUtil.parseTiledTriggerLayer();
		
		this.zoom = map.getLayers().get("collision-layer").getProperties().get("zoom", 1.0f, float.class);
		this.zoomDesired = zoom;	
		
		this.player = new Player(this, (int)(startX * PPM), (int)(startY * PPM), loadout.character, old);
		this.cameraTarget = player;

		
		controller = new PlayerController(player);	
		
		//Set up "save point" as starting point
		this.safeX = startX;
		this.safeY = startY;
		this.saveZoom = zoomDesired;
		this.saveCameraPoint = cameraTarget;
		
		//Init background image
		this.bg = HadalGame.assetManager.get(AssetList.BACKGROUND1.toString());
		this.black = HadalGame.assetManager.get(AssetList.BLACK.toString());
	}
	
	/**
	 * This constructor is used for levels without a custom level/loadout.
	 */
	public PlayState(GameStateManager gsm, Record record, boolean realFite, PlayerBodyData old) {
		this(gsm, new Loadout(record), UnlockLevel.valueOf(record.getLevel()), realFite, old);
	}
			
	@Override
	public void show() {
		this.stage = new PlayStateStage(this) {
			
			//This precaution exists to prevent null pointer when player is not loaded in yet.
			@Override
			public void draw() {
				if (player.getPlayerData() != null) {
					super.draw();
				}
			}
		};
		
		uiPlay = new UIPlay(HadalGame.assetManager, this, player);
		uiReload = new UIReload(HadalGame.assetManager, this, player);
		uiActive = new UIActives(HadalGame.assetManager, this, player);
		uiObjective = new UIObjective(HadalGame.assetManager, this, player);
		uiStatus = new UIStatuses(HadalGame.assetManager, this, player);
		
		if (uiExtra == null) {
			uiExtra = new UIExtra(HadalGame.assetManager, this);
		}
		
		this.stage.addActor(uiPlay);
		this.stage.addActor(uiActive);
		this.stage.addActor(uiReload);
		this.stage.addActor(uiExtra);
		this.stage.addActor(uiObjective);

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
		uiReload.setPlayer(player);
	}
	
	/**
	 * Every engine tick, the GameState must process all entities in it according to the time elapsed.
	 */
	@Override
	public void update(float delta) {
		
		//The box2d world takes a step. This handles collisions + physics stuff. Maybe change delta to set framerate? 
		world.step(1 / 60f, 6, 2);
		
		//Let AI process time step
		GdxAI.getTimepiece().update(1 / 60f);
		
		//All entities that are set to be removed are removed.
		for (HadalEntity entity : removeList) {
			entities.remove(entity);
			hitboxes.remove(entity);
			entity.dispose();
			HadalGame.server.server.sendToAllTCP(new Packets.DeleteEntity(entity.getEntityID().toString()));
		}
		removeList.clear();
		
		//All entities that are set to be added are added.		
		for (HadalEntity entity : createList) {
			if (entity instanceof Hitbox) {
				hitboxes.add(entity);
			} else {
				entities.add(entity);
			}
			entity.create();
			
			Object packet = entity.onServerCreate();
			if (packet != null) {
				HadalGame.server.server.sendToAllTCP(packet);
			}
		}
		createList.clear();
		
		//This processes all entities in the world. (for example, player input/cooldowns/enemy ai)
		for (HadalEntity entity : hitboxes) {
			entity.controller(delta);
			Object packet = entity.onServerSync();
			if (packet != null) {
				HadalGame.server.server.sendToAllTCP(packet);
			}
			
		}
		for (HadalEntity entity : entities) {
			entity.controller(delta);
			Object packet = entity.onServerSync();
			if (packet != null) {
				HadalGame.server.server.sendToAllTCP(packet);
			}
		}
		
		//Update the game camera and batch.
		cameraUpdate();
		tmr.setView(camera);
		
		//process fade transitions
		if (fadeInitialDelay <= 0f) {
			if (fadeLevel > 0f && fadeDelta < 0f) {
				fadeLevel += fadeDelta;
				if (fadeLevel < 0f) {
					fadeLevel = 0f;
				}
			} else if (fadeLevel < 1f && fadeDelta > 0f) {
				fadeLevel += fadeDelta;
				if (fadeLevel > 1f) {
					fadeLevel = 1f;
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
		tmr.render();				

		//Render debug lines for box2d objects.
		b2dr.render(world, camera.combined.scl(PPM));
		
		//Iterate through entities in the world to render
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		for (HadalEntity hitbox : hitboxes) {
			hitbox.render(batch);
		}
		for (HadalEntity schmuck : entities) {
			schmuck.render(batch);
		}

		batch.end();
		
		//Render lighting
		rays.setCombinedMatrix(camera);
		rays.updateAndRender();
		
		//Render fade transitions
		if (fadeLevel > 0) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			batch.setProjectionMatrix(hud.combined);
			batch.setColor(1f, 1f, 1f, fadeLevel);
			batch.draw(black, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
			batch.setColor(1f, 1f, 1f, 1);
			batch.end();
		}
	}	
	
	/**
	 * This is called every update. This resets the camera zoom and makes it move towards the player.
	 */
	protected void cameraUpdate() {
		
		zoom = zoom + (zoomDesired - zoom) * 0.05f;
		
		camera.zoom = zoom;
		sprite.zoom = zoom;
		if (cameraTarget != null) {
			if (cameraTarget.getBody() != null) {
				CameraStyles.lerpToTarget(camera, cameraTarget.getBody().getPosition().scl(PPM));
				CameraStyles.lerpToTarget(sprite, cameraTarget.getBody().getPosition().scl(PPM));
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
		entities.clear();
		hitboxes.clear();
		createList.clear();
		removeList.clear();
		
		world.dispose();
		tmr.dispose();
		map.dispose();
		rays.dispose();
		if (stage != null) {
			stage.dispose();
		}
	}
	
	/**
	 * This is called when ending a playstate by winning, losing or moving to a new playstate
	 */	
	public void transitionState() {
		getGsm().removeState(PlayState.class);
		switch (nextState) {
		case LOSE:
			if (realFite) {
				gsm.getRecord().updateScore(uiExtra.getScore(), level.name());
				getGsm().addState(State.GAMEOVER, TitleState.class);
			} else {
				uiStatus.clearStatus();
				
				boolean resetCamera = false;
				if (saveCameraPoint.equals(player)) {
					resetCamera = true;
				}
				player = new Player(this, (int)(getSafeX() * PPM),
						(int)(getSafeY() * PPM), loadout.character, null);
				
				controller.setPlayer(player);
				
				this.zoomDesired = saveZoom;
				if (resetCamera) {
					this.cameraTarget = player;
				} else {
					this.cameraTarget = saveCameraPoint;
				}
				
				fadeDelta = -0.015f;
			}
			break;
		case WIN:
			gsm.getRecord().updateScore(uiExtra.getScore(), level.name());
			getGsm().addState(State.VICTORY, TitleState.class);
			break;
		case NEWLEVEL:
			getGsm().removeState(PlayState.class);
			getGsm().removeState(HubState.class);
        	getGsm().addState(State.PLAY, TitleState.class);
			break;
		case NEXTSTAGE:
			getGsm().removeState(PlayState.class);
			getGsm().removeState(HubState.class);
			getGsm().addPlayState(nextLevel, loadout, player.getPlayerData(), TitleState.class);
			break;
		default:
			break;
		}	
	}
	
	/**
	 * transition from one playstate to another with a new level.
	 * @param level: file of the new map
	 * @param reset: should this warp reset the player's loadout/hp and stuff?
	 */
	public void loadLevel(UnlockLevel level, boolean reset) {
		if (nextLevel == null) {
			nextLevel = level;
			if (reset) {
				nextState = transitionState.NEWLEVEL;
			} else {
				nextState = transitionState.NEXTSTAGE;
			}
			fadeDelta = 0.015f;
			
			//TODO: player flash and particles
		}
	}
	
	public void catchUpClient(int connId) {
		for (HadalEntity entity : entities) {
			Object packet = entity.onServerCreate();
			if (packet != null) {
				HadalGame.server.server.sendToTCP(connId, entity.onServerCreate());
			}
		}
		for (HadalEntity entity : hitboxes) {
			Object packet = entity.onServerCreate();
			if (packet != null) {
				HadalGame.server.server.sendToTCP(connId, entity.onServerCreate());
			}
		}
	}
	
	public void gameOver(boolean won) {
		if (won) {
			nextState = transitionState.WIN;
		} else {
			nextState = transitionState.LOSE;
		}
		fadeInitialDelay = 1.0f;
		fadeDelta = 0.015f;
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
	 * Getter for the player. This will return null if the player has not been spawned
	 * @return: The Player entity.
	 */
	public Player getPlayer() {
		return player;
	}

	public Loadout getLoadout() {
		return loadout;
	}
	
	public World getWorld() {
		return world;
	}
	
	public RayHandler getRays() {
		return rays;
	}
	
	public Enemy getWorldDummy() {
		return worldDummy;
	}

	public UnlockLevel getLevel() {
		return level;
	}
	
	public PlayStateStage getStage() {
		return stage;
	}	
	
	public UIExtra getUiExtra() {
		return uiExtra;
	}

	public UIStatuses getUiStatus() {
		return uiStatus;
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

	public void setSafe(int x, int y) {
		this.safeX = x;
		this.safeY = y;
		this.saveZoom = zoomDesired;
		this.saveCameraPoint = cameraTarget;
	}
	
	public int getSafeX() {
		return safeX;
	}

	public int getSafeY() {
		return safeY;
	}

	public void setCameraTarget(HadalEntity cameraTarget) {
		this.cameraTarget = cameraTarget;
	}
	
	public HadalEntity getObjectiveTarget() {
		return objectiveTarget;
	}
	
	public MouseTracker getMouse() {
		return mouse;
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

	public boolean isRealFite() {
		return realFite;
	}
	
	/**
	 * Tentative tracker of player kill number.
	 * @param i: Number to increase score by.
	 */
	public void incrementScore(int i) {
		uiExtra.incrementScore(i);
	}
	
	private enum transitionState {
		LOSE,
		WIN,
		NEWLEVEL,
		NEXTSTAGE
	}
}
