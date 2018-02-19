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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UILevel;
import com.mygdx.hadal.actors.UIMomentum;
import com.mygdx.hadal.actors.UIPlay;
import com.mygdx.hadal.actors.UIReload;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.handlers.WorldContactListener;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.stages.PlayStateStage;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
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
	public Player player;
	public PlayerController controller;
	
	public Loadout loadout;
	
	//These process and store the map parsed from the Tiled file.
	private TiledMap map;
	OrthogonalTiledMapRenderer tmr;
	
	//The font is for writing text.
    public BitmapFont font;
    
    //TODO: rays will eventually implement lighting.
	public RayHandler rays;
	
	//world manages the Box2d world and physics. b2dr renders debug lines for testing
	private Box2DDebugRenderer b2dr;
	private World world;
		
	//These represent the set of entities to be added to/removed from the world. This is necessary to ensure we do this between world steps.
	private Set<HadalEntity> removeList;
	private Set<HadalEntity> createList;
	
	//This is a set of all non-hitbox entities in the world
	private Set<HadalEntity> entities;
	//This is a set of all hitboxes. This is separate to draw hitboxes underneath other bodies
	private Set<HadalEntity> hitboxes;
	
	//sourced effects from the world are attributed to this dummy.
	public Enemy worldDummy;
	
	//TODO: Temporary tracker of number of enemies defeated. Will replace eventually
	public int score = 0;
	public String level;
	
	public boolean gameover = false;
	public boolean won = false;
	public static final float gameoverCd = 2.5f;
	public float gameoverCdCount;
	
	public float zoom;
	public boolean realFite;
	
	public PlayStateStage stage;
//	public Set<Zone> zones;
	
	/**
	 * Constructor is called upon player beginning a game.
	 * @param gsm: StateManager
	 */
	public PlayState(GameStateManager gsm, Loadout loadout, String level, boolean realFite, Player old) {
		super(gsm);
		
		this.realFite = realFite;
		
		this.loadout = loadout;
		this.level = level;
		
		//Initialize font and text camera for ui purposes.
        font = new BitmapFont();
        
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
		
		map = new TmxMapLoader().load(level);
		
		tmr = new OrthogonalTiledMapRenderer(map);
		
		TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision-layer").getObjects());
		
		TiledObjectUtil.parseTiledEventLayer(this, world, camera, rays, map.getLayers().get("event-layer").getObjects());
		
		TiledObjectUtil.parseTiledTriggerLayer(this, world, camera, rays);
		
		this.zoom = map.getLayers().get("collision-layer").getProperties().get("zoom", 1.0f, float.class);
		
		if (old == null) {
			player = new Player(this, world, camera, rays, 0, 0, loadout.playerSprite);

		} else {
			player = old;
		}
		
		controller = new PlayerController(player, this);		
	}
	
	//This constructor is used when entering a new playstate.
	//The null represents that there is no existing player and a new one should be made.
	public PlayState(GameStateManager gsm, Loadout loadout, String level, boolean realFite) {
		this(gsm, loadout, level, realFite, null);
	}
	
	@Override
	public void show() {
		this.stage = new PlayStateStage(this);
		this.stage.addActor(new UIPlay(HadalGame.assetManager, this, player));
		this.stage.addActor(new UIMomentum(HadalGame.assetManager, this, player));
		this.stage.addActor(new UIReload(HadalGame.assetManager, this, player));
		this.stage.addActor(new UILevel(HadalGame.assetManager, this));
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
		
		//atm, stage is not null if the playstate is displayed within another state (loadout) (show is not called.)
		if (stage != null) {
			inputMultiplexer.addProcessor(stage);
		} else {
			inputMultiplexer.addProcessor(Gdx.input.getInputProcessor());
		}
		
		inputMultiplexer.addProcessor(controller);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	/**
	 * transition from one playstate to another with a new level.
	 * @param level: file of the new map
	 */
	public void loadLevel(String level) {
		getGsm().removeState(PlayState.class);
		getGsm().removeState(LoadoutState.class);
		getGsm().removeState(HubState.class);
		getGsm().addPlayState(level, loadout, TitleState.class);
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
				if (realFite) {
					getGsm().removeState(PlayState.class);
					if (won) {
						getGsm().addState(State.VICTORY, TitleState.class);
					} else {
						getGsm().addState(State.GAMEOVER, TitleState.class);
					}
				} else {
					player = new Player(this, world, camera, rays, (int)(player.getBody().getPosition().x * PPM),
							(int)(player.getBody().getPosition().y * PPM), loadout.playerSprite);
					
					controller.setPlayer(player);
					
					gameover = false;
				}
 				
			}
		}
	}
	
	/**
	 * This method renders stuff to the screen after updating.
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Render Tiled Map + world
		tmr.render();				

		//Render debug lines for box2d objects.
		b2dr.render(world, camera.combined.scl(PPM));
		
		//Iterate through entities in the world to render
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		font.getData().setScale(1.0f);

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
	
	public void addActor(Actor actor) {
		stage.addActor(actor);
	}
	
	/**
	 * Getter for the player. This will return null if the player has not been spawned
	 * @return: The Player entity.
	 */
	public Player getPlayer() {
		return player;
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
