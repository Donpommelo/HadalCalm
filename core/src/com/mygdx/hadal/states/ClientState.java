package com.mygdx.hadal.states;


import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.ClientController;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.server.Packets;

public class ClientState extends PlayState {
	
	//This is a set of all non-hitbox entities in the world
	private LinkedHashMap<String, HadalEntity> entities;
	//This is a set of all hitboxes. This is separate to draw hitboxes underneath other bodies
	private LinkedHashMap<String, HadalEntity> hitboxes;
	//This is a set of all non-hitbox entities in the world
	private LinkedHashMap<String, HadalEntity> removeList;
	//This is a set of all hitboxes. This is separate to draw hitboxes underneath other bodies
	private LinkedHashMap<String, HadalEntity> createList;

	private ArrayList<Object[]> sync;
	
	private ClientController controller;
	private Vector3 tmpVec3 = new Vector3();
	
	public ClientState(GameStateManager gsm, Loadout loadout, UnlockLevel level) {
		super(gsm, loadout, level, true, null);
		entities = new LinkedHashMap<String, HadalEntity>();
		hitboxes = new LinkedHashMap<String, HadalEntity>();
		removeList = new LinkedHashMap<String, HadalEntity>();
		createList = new LinkedHashMap<String, HadalEntity>();
		
		sync = new ArrayList<Object[]>();
		
        HadalGame.client.client.sendTCP(new Packets.ClientLoaded());
	}
	
	@Override
	public void resetController() {
		controller = new ClientController();
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		
		inputMultiplexer.addProcessor(stage);
		
		inputMultiplexer.addProcessor(controller);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	@Override
	public void update(float delta) {
		tmpVec3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		HadalGame.viewportCamera.unproject(tmpVec3);
		HadalGame.client.client.sendTCP(new Packets.MouseMove((int)tmpVec3.x, (int)tmpVec3.y));
		
		//All entities that are set to be removed are removed.
		for (String key : removeList.keySet()) {
			HadalEntity entity = entities.remove(key);
			if (entity != null) {
				entity.dispose();
			}
			entity = hitboxes.remove(key);
			if (entity != null) {
				entity.dispose();
			}
		}
		removeList.clear();
		
		//All entities that are set to be added are added.		
		for (String key : createList.keySet()) {
			HadalEntity entity = createList.get(key);
			if (entity instanceof Hitbox) {
				hitboxes.put(key, entity);
			} else {
				entities.put(key, entity);
			}
			entity.create();
		}
		createList.clear();
		
		while (!sync.isEmpty()) {
			Object[] p = (Object[]) sync.remove(0);
		 	if (p != null) {
		 		HadalEntity entity = hitboxes.get(p[0]);
		 		if (entity != null) {
		 			entity.onClientSync(p[1]);
		 		}
		 		entity = entities.get(p[0]);
		 		if (entity != null) {
		 			entity.onClientSync(p[1]);
		 		}
		 	}
		}
		
		for (HadalEntity entity : hitboxes.values()) {
			entity.clientController(delta);
		}
		for (HadalEntity entity : entities.values()) {
			entity.clientController(delta);
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
//					transitionState();
				}
			}
		} else {
			fadeInitialDelay -= delta;
		}
	}

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
		
		for (HadalEntity hitbox : hitboxes.values()) {
			hitbox.render(batch);
		}
		for (HadalEntity schmuck : entities.values()) {
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
	
	public void addEntity(String entityId, HadalEntity entity) {
		createList.put(entityId, entity);
	}
	
	public void removeEntity(String entityId, HadalEntity entity) {
		removeList.put(entityId, entity);
	}

	public void syncObject(String entityId, Object o) {
		Object[] packet = {entityId, o};
		sync.add(packet);
	}
	
	/**
	 * This method is called by entities to be added to the set of entities to be deleted next engine tick.
	 * @param entity: delet this
	 */
	@Override
	public void destroy(HadalEntity entity) {

	}
	
	/**
	 * This method is called by entities to be added to the set of entities to be created next engine tick.
	 * @param entity: entity to be created
	 */
	@Override
	public void create(HadalEntity entity) {

	}
	
	@Override
	public void dispose() {

	}
}
