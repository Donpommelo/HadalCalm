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
import com.mygdx.hadal.event.AirBubble;
import com.mygdx.hadal.event.Currents;
import com.mygdx.hadal.event.Spring;
import com.mygdx.hadal.handlers.WorldContactListener;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Enemy;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.utils.CameraStyles;
import com.mygdx.hadal.utils.TiledObjectUtil;

import box2dLight.RayHandler;

public class PlayState extends GameState{
	
	private Player player;
	
	private TiledMap map;
	OrthogonalTiledMapRenderer tmr;
	
    private BitmapFont font;
    private OrthographicCamera hud;
    
	private RayHandler rays;
	private Box2DDebugRenderer b2dr;
	private World world;
	
	private Set<HadalEntity> removeList;
	private Set<HadalEntity> createList;
		
	private Set<HadalEntity> schmucks;
	
	public PlayState(GameStateManager gsm) {
		super(gsm);
		
        font = new BitmapFont();
        hud = new OrthographicCamera();
        hud.setToOrtho(false, 720, 480);
        
		world = new World(new Vector2(0, -9.81f), false);
		world.setContactListener(new WorldContactListener());
		rays = new RayHandler(world);
        rays.setAmbientLight(.4f);
        
		b2dr = new Box2DDebugRenderer();
		
		removeList = new HashSet<HadalEntity>();
		createList = new HashSet<HadalEntity>();
		schmucks = new HashSet<HadalEntity>();
		
		player = new Player(this, world, camera, rays, 300, 300);
		
		new Enemy(this, world, camera, rays, 16, 32, 500, 300);
		
		new AirBubble(this, world, camera, rays, 500, 300);
		new Spring(this, world, camera, rays,  64, 16, 540, 125, new Vector2(0, 500));
		new Currents(this, world, camera, rays,  200, 400, 700, 200, new Vector2(30, 10));
		
		map = new TmxMapLoader().load("Maps/test_map_large.tmx");
		tmr = new OrthogonalTiledMapRenderer(map);
		TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision-layer").getObjects());
	}

	@Override
	public void update(float delta) {
		world.step(1 / 60f, 6, 2);
		
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
		for (HadalEntity schmuck : schmucks) {
			schmuck.render(batch);
		}
		
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		
		if (player.getPlayerData() != null) {
			font.draw(batch, "Fuel: " + player.getPlayerData().currentFuel, 100, 150);
			font.draw(batch, player.getPlayerData().currentTool.getText(), 100, 120);
		}
		
		batch.end();
		
	}	
	
	private void cameraUpdate() {
		camera.zoom = 1;
		CameraStyles.lerpToTarget(camera, player.getPosition().scl(PPM));
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
	
}
