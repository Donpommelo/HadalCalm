package com.mygdx.hadal.states;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.handlers.WorldContactListener;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.utils.CameraStyles;
import com.mygdx.hadal.utils.TiledObjectUtil;

import box2dLight.RayHandler;

public class PlayState extends GameState {
	
	public Player player;
	
	private TiledMap map;
	OrthogonalTiledMapRenderer tmr;
	
    public BitmapFont font;
    private OrthographicCamera hud;
    
	private RayHandler rays;
	private Box2DDebugRenderer b2dr;
	private World world;
	
	private Set<HadalEntity> removeList;
	private Set<HadalEntity> createList;
		
	private Set<HadalEntity> schmucks;
	
	public int score = 0;
	
//	private float controllerCounter = 0;
	
	public PlayState(GameStateManager gsm) {
		super(gsm);
		
        font = new BitmapFont();
        hud = new OrthographicCamera();
        hud.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
		world = new World(new Vector2(0, -9.81f), false);
		world.setContactListener(new WorldContactListener());
		rays = new RayHandler(world);
        rays.setAmbientLight(.4f);
        
		b2dr = new Box2DDebugRenderer();
		
		removeList = new HashSet<HadalEntity>();
		createList = new HashSet<HadalEntity>();
		schmucks = new HashSet<HadalEntity>();
		
//		player = new Player(this, world, camera, rays, 300, 300);
	
		map = new TmxMapLoader().load("Maps/test_map_large.tmx");
		tmr = new OrthogonalTiledMapRenderer(map);
		TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision-layer").getObjects());
		TiledObjectUtil.parseTiledEventLayer(this, world, camera, rays, map.getLayers().get("event-layer").getObjects());
	}

	@Override
	public void update(float delta) {
		world.step(delta, 6, 2);
		
		for (HadalEntity schmuck : removeList) {
			schmucks.remove(schmuck);
			schmuck.dispose();
		}
		removeList.clear();
		
		for (HadalEntity schmuck : createList) {
			schmucks.add(schmuck);
			schmuck.create();
		}
		createList.clear();
		
/*		controllerCounter += delta;
		
		if (controllerCounter >= 1/60f) {
			controllerCounter  -= 1/60f;
			for (HadalEntity schmuck : schmucks) {
				schmuck.controller(1 / 60f);
			}
		}*/
		
		for (HadalEntity schmuck : schmucks) {
			schmuck.controller(delta);
		}
		
		cameraUpdate();
		tmr.setView(camera);
		batch.setProjectionMatrix(camera.combined);
//		rays.setCombinedMatrix(camera.combined.cpy().scl(PPM));
	}
	
	public void destroy(HadalEntity body) {
		removeList.add(body);
	}
	
	public void create(HadalEntity body) {
		createList.add(body);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		tmr.render();				

		b2dr.render(world, camera.combined.scl(PPM));

		rays.updateAndRender();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		
		for (HadalEntity schmuck : schmucks) {
			schmuck.render(batch);
		}
		
		batch.setProjectionMatrix(hud.combined);
		
		if (player != null) {
			if (player.getPlayerData() != null) {
				font.getData().setScale(1);
				font.draw(batch, "Score: " + score+ " Hp: " + player.getPlayerData().currentHp + " Fuel: " + player.getPlayerData().currentFuel, 20, 80);
				font.draw(batch, player.getPlayerData().currentTool.getText(), 20, 60);
				if (player.momentums.size != 0) {
					font.draw(batch, "Saved Momentum: " + player.momentums.first(), 20, 40);
				}
				if (player.currentEvent != null) {
					font.draw(batch, player.currentEvent.getText(), 20, 20);
				}
			}
		}
		
		batch.end();
		
	}	
	
	private void cameraUpdate() {
		camera.zoom = 1;
		if (player != null) {
			if (player.body != null) {
				CameraStyles.lerpToTarget(camera, player.getPosition().scl(PPM));
			}
		}
	}
	
	@Override
	public void dispose() {
		b2dr.dispose();
		
		for (HadalEntity schmuck : schmucks) {
			schmuck.dispose();
		}
		
		world.dispose();
		tmr.dispose();
		map.dispose();
	}
	
	public Player getPlayer() {
		return player;
	}

	public void incrementScore(int i) {
		score += i;
	}
	
}
