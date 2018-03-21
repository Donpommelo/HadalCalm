package com.mygdx.hadal.states;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal._retired.LoadoutState;
import com.mygdx.hadal.actors.UILevel;
import com.mygdx.hadal.actors.UIMomentum;
import com.mygdx.hadal.actors.UIPlay;
import com.mygdx.hadal.actors.UIReload;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.handlers.WorldContactListener;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
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
	private Player player;
	private PlayerController controller;
	
	private Loadout loadout;
	
	//These process and store the map parsed from the Tiled file.
	private TiledMap map;
	private OrthogonalTiledMapRenderer tmr;
    
    //rays will implement lighting.
	private RayHandler rays;
	
	//world manages the Box2d world and physics. b2dr renders debug lines for testing
	private Box2DDebugRenderer b2dr;
	private World world;
	
	public BitmapFont font;
	
	//These represent the set of entities to be added to/removed from the world. This is necessary to ensure we do this between world steps.
	private Set<HadalEntity> removeList;
	private Set<HadalEntity> createList;
	
	//This is a set of all non-hitbox entities in the world
	private Set<HadalEntity> entities;
	//This is a set of all hitboxes. This is separate to draw hitboxes underneath other bodies
	private Set<HadalEntity> hitboxes;
	
	//sourced effects from the world are attributed to this dummy.
	private Enemy worldDummy;
	
	//this exists so that schmucks can steer towards the mouse.
	private MouseTracker mouse;
	
	//TODO: Temporary tracker of number of enemies defeated. Will replace eventually
	private int score = 0;
	private UnlockLevel level;
	
	private boolean gameover = false;
	private boolean won = false;
	private static final float gameoverCd = 2.5f;
	private float gameoverCdCount;
	
	private float zoom;
	private int startX, startY;
	private int safeX, safeY;
	private boolean realFite;
	
	private PlayStateStage stage;
//	private Set<Zone> zones;
	
	private UIPlay uiPlay;
	private UIReload uiReload;
	private UIMomentum uiMomentum;
	private UILevel uiLevel;
	
	/**
	 * Constructor is called upon player beginning a game.
	 * @param gsm: StateManager
	 */
	public PlayState(GameStateManager gsm, Loadout loadout, UnlockLevel level, boolean realFite, PlayerBodyData old) {
		super(gsm);

		this.realFite = realFite;
		
		this.loadout = loadout;
		this.level = level;
        
		this.font = new BitmapFont();
		
        //Initialize box2d world and related stuff
		world = new World(new Vector2(0, -9.81f), false);
		world.setContactListener(new WorldContactListener());
		World.setVelocityThreshold(0);
		
		/*world.setContactFilter(new ContactFilter() {

			@Override
			public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});*/
		
		rays = new RayHandler(world);
        rays.setAmbientLight(1.0f);
        rays.setCulling(false);
        
 //       RayHandler.useDiffuseLight(true);
        rays.setCombinedMatrix(camera);

		b2dr = new Box2DDebugRenderer();
		
		
		
		//Initialize sets to keep track of active entities
		removeList = new HashSet<HadalEntity>();
		createList = new HashSet<HadalEntity>();
		entities = new HashSet<HadalEntity>();
		hitboxes = new HashSet<HadalEntity>();
		
		//The "worldDummy" will be the source of map-effects that want a perpetrator
		worldDummy = new Enemy(this, world, camera, rays, 1, 1, -1000, -1000);
		
		mouse = new MouseTracker(this, world, camera, rays);
		
		map = new TmxMapLoader().load(level.getMap());
		
		tmr = new OrthogonalTiledMapRenderer(map);
		
		this.startX = map.getLayers().get("collision-layer").getProperties().get("startX", Integer.class);
		this.startY = map.getLayers().get("collision-layer").getProperties().get("startY", Integer.class);
		
		TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision-layer").getObjects());
		
		TiledObjectUtil.parseTiledEventLayer(this, world, camera, rays, map.getLayers().get("event-layer").getObjects());
		
		TiledObjectUtil.parseTiledTriggerLayer(this, world, camera, rays);
		
		this.zoom = map.getLayers().get("collision-layer").getProperties().get("zoom", 1.0f, float.class);
		

		this.safeX = startX;
		this.safeY = startY;
		
		player = new Player(this, world, camera, rays, (int)(startX * PPM), (int)(startY * PPM), loadout.character, old);
		
		controller = new PlayerController(player, this);		
	}
	
	/**
	 * 
	 */
	public PlayState(GameStateManager gsm, Loadout loadout, UnlockLevel level, boolean realFite) {
		this(gsm, loadout, level, realFite, null);
	}
			
	@Override
	public void show() {
		this.stage = new PlayStateStage(this) {
			public void draw() {
				if (player.getPlayerData() != null) {
					super.draw();
				}
			}
		};
		
		uiPlay = new UIPlay(HadalGame.assetManager, this, player);
		uiReload = new UIReload(HadalGame.assetManager, this, player);
		uiMomentum = new UIMomentum(HadalGame.assetManager, this, player);
		uiLevel = new UILevel(HadalGame.assetManager, this);
		
		this.stage.addActor(uiPlay);
		this.stage.addActor(uiMomentum);
		this.stage.addActor(uiReload);
		this.stage.addActor(uiLevel);
		app.newMenu(stage);
		resetController();
	}

	/**
	 * This method gives input to the player as well as the menu.
	 * This is called when a player is created.
	 */
	public void resetController() {
		controller = new PlayerController(player, this);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		
		inputMultiplexer.addProcessor(stage);
		
		inputMultiplexer.addProcessor(controller);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		uiPlay.setPlayer(player);
		uiMomentum.setPlayer(player);
		uiReload.setPlayer(player);
	}
	
	/**
	 * transition from one playstate to another with a new level.
	 * @param level: file of the new map
	 */
	public void loadLevel(UnlockLevel level) {
		getGsm().removeState(PlayState.class);
		getGsm().removeState(LoadoutState.class);
		getGsm().removeState(HubState.class);
		getGsm().addPlayState(level, loadout, player.getPlayerData(), TitleState.class);
	}
	
	/**
	 * Every engine tick, the GameState must process all entities in it according to the time elapsed.
	 */
	@Override
	public void update(float delta) {
		
		//The box2d world takes a step. This handles collisions + physics stuff. Maybe change delta to set framerate? 
		world.step(1 / 60f, 6, 2);

		//All entities that are set to be removed are removed.
		for (HadalEntity entity : removeList) {
			entities.remove(entity);
			hitboxes.remove(entity);
			entity.dispose();
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
		}
		createList.clear();
		
		//This processes all entities in the world. (for example, player input/cooldowns/enemy ai)
		for (HadalEntity entity : hitboxes) {
			entity.controller(delta);
		}
		for (HadalEntity entity : entities) {
			entity.controller(delta);
		}
		
		//Update the game camera and batch.
		cameraUpdate();
		tmr.setView(camera);
		
		//process gameover
		if (gameover) {
			gameoverCdCount -= delta;
			if (gameoverCdCount < 0) {
				endGameProcessing();
			}
		}
	}
	
	/**
	 * This method renders stuff to the screen after updating.
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(0/255f, 0/255f, 135/255f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
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
				
		batch.setProjectionMatrix(hud.combined);
				
		batch.end();
		rays.setCombinedMatrix(camera);
		rays.updateAndRender();
		
	}	
	
	/**
	 * This is called every update. This resets the camera zoom and makes it move towards the player.
	 */
	private void cameraUpdate() {
		camera.zoom = zoom;
		sprite.zoom = zoom;
		if (player != null) {
			if (player.getBody() != null) {
				CameraStyles.lerpToTarget(camera, player.getBody().getPosition().scl(PPM));
				CameraStyles.lerpToTarget(sprite, player.getBody().getPosition().scl(PPM));
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
		world.dispose();
		tmr.dispose();
		map.dispose();
		if (stage != null) {
			stage.dispose();
		}
	}
	
	/**
	 * This is called when ending a playstate by winning or losing
	 */
	public void endGameProcessing() {
		if (realFite) {
			
			gsm.getRecord().updateScore(score, level.name());
			
			getGsm().removeState(PlayState.class);
			if (won) {
				getGsm().addState(State.VICTORY, TitleState.class);
			} else {
				getGsm().addState(State.GAMEOVER, TitleState.class);
			}			
		} else {
			player = new Player(this, world, camera, rays, (int)(player.getBody().getPosition().x * PPM),
					(int)(player.getBody().getPosition().y * PPM), loadout.character, null);
			
			controller.setPlayer(player);
			
			gameover = false;
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
	 * Getter for the player. This will return null if the player has not been spawned
	 * @return: The Player entity.
	 */
	public Player getPlayer() {
		return player;
	}	

	public Loadout getLoadout() {
		return loadout;
	}
	
	public RayHandler getRays() {
		return rays;
	}
	
	public Enemy getWorldDummy() {
		return worldDummy;
	}
	
	public MouseTracker getMouse() {
		return mouse;
	}

	public int getScore() {
		return score;
	}

	public UnlockLevel getLevel() {
		return level;
	}
	
	public PlayStateStage getStage() {
		return stage;
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
	}
	
	public int getSafeX() {
		return safeX;
	}

	public int getSafeY() {
		return safeY;
	}

	/**
	 * Tentative tracker of player kill number.
	 * @param i: Number to increase score by.
	 */
	public void incrementScore(int i) {
		score += i;
	}

	public void gameOver(boolean won) {
		this.won = won;
		gameover = true;
		gameoverCdCount = gameoverCd;
	}
	
}
