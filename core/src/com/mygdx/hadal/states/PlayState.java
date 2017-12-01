package com.mygdx.hadal.states;

import static com.mygdx.hadal.utils.Constants.PPM;

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
import com.mygdx.hadal.utils.CameraStyles;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

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
		
		player = new Player(world, rays);
		
		BodyBuilder.createBox(world, 0, 0, 1000, 32, true, true);
		
		map = new TmxMapLoader().load("Maps/test_map_large.tmx");
		tmr = new OrthogonalTiledMapRenderer(map);
		TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision-layer").getObjects());
	}

	@Override
	public void update(float delta) {
		world.step(1 / 60f, 6, 2);
		
		player.controller(delta);
		
		cameraUpdate();
		tmr.setView(camera);
		batch.setProjectionMatrix(camera.combined);
//		rays.setCombinedMatrix(camera.combined.cpy().scl(PPM));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		tmr.render();				

		b2dr.render(world, camera.combined.scl(PPM));

		rays.updateAndRender();
		player.render(batch);
		
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		font.draw(batch, "Fuel: " + player.getPlayerData().currentFuel, 100, 150);
		batch.end();
		
	}	
	
	private void cameraUpdate() {
		camera.zoom = 1;
		CameraStyles.lerpToTarget(camera, player.getPosition().scl(PPM));
	}
	
	@Override
	public void dispose() {
		b2dr.dispose();
		world.dispose();
		player.dispose();
		tmr.dispose();
		map.dispose();
	}
	
}
