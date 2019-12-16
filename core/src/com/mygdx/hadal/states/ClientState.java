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
import com.mygdx.hadal.server.PacketEffect;
import com.mygdx.hadal.server.Packets;

/**
 * This is a version of the playstate that is provided for Clients.
 * No processing of physics and stuff like that is done here. Instead, everything is done based on instructions by the server.
 * @author Zachary Tu
 *
 */
public class ClientState extends PlayState {
	
	//This is a set of all non-hitbox entities in the world mapped from their entityId
	private LinkedHashMap<String, HadalEntity> entities;
	
	//This is a set of all hitboxes mapped from their unique entityId
	private LinkedHashMap<String, HadalEntity> hitboxes;
	
	//This is a list of sync instructions. It contains [entityId, object to be synced]
	private ArrayList<Object[]> sync;
	
	//This contains the position of the client's mouse, to be sent to the server
	private Vector3 mousePosition = new Vector3();
	
	public ClientState(GameStateManager gsm, Loadout loadout, UnlockLevel level) {
		super(gsm, loadout, level, false, null);
		entities = new LinkedHashMap<String, HadalEntity>();
		hitboxes = new LinkedHashMap<String, HadalEntity>();
		sync = new ArrayList<Object[]>();
		
		//Add a world dummy to the client's world.
		addEntity(getWorldDummy().getEntityID().toString(), getWorldDummy(), ObjectSyncLayers.STANDARD);
	}
	
	@Override
	public void resetController() {
		
		//Whenever the controller is reset, the client gets a new client controller.
		controller = new ClientController(this);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		
		inputMultiplexer.addProcessor(stage);
		
		inputMultiplexer.addProcessor(controller);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	@Override
	public void update(float delta) {
		
		//Send mouse position to the server.
		mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		HadalGame.viewportCamera.unproject(mousePosition);
		HadalGame.client.client.sendUDP(new Packets.MouseMove((int)mousePosition.x, (int)mousePosition.y));
		
		//All entities that are set to be created are created and assigned their entityId
		for (Object[] pair: createListClient) {
			if (pair[2].equals(ObjectSyncLayers.HBOX)) {
				hitboxes.put((String)pair[0], (HadalEntity)pair[1]);
			} else {
				entities.put((String)pair[0], (HadalEntity)pair[1]);
			}
			((HadalEntity)pair[1]).create();
		}
		createListClient.clear();
		
		//All entities that are set to be removed are removed.
		for (String key: removeListClient) {
			HadalEntity entity = findEntity(key);
			if (entity != null) {
				entity.dispose();
			}
			entities.remove(key);
			hitboxes.remove(key);
		}
		removeListClient.clear();
		
		//All sync instructions are carried out.
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
		
		//While most objects don't do any processing on client side, the clientController is run for the exceptions.
		for (HadalEntity entity : hitboxes.values()) {
			entity.clientController(delta);
		}
		for (HadalEntity entity : entities.values()) {
			entity.clientController(delta);
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
			
			if (fadeLevel > 0f && fadeDelta < 0f) {
				
				//If we are fading in and not done yet, decrease fade.
				fadeLevel += fadeDelta;
				
				//If we just finished fading in, set fade to 0
				if (fadeLevel < 0f) {
					fadeLevel = 0f;
				}
			} else if (fadeLevel < 1f && fadeDelta > 0f) {
				
				//If we are fading out and not done yet, increase fade.
				fadeLevel += fadeDelta;
				
				//If we just finished fading out, set fade to 1 and do a transition
				if (fadeLevel >= 1f) {
					fadeLevel = 1f;
					transitionState();
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
		tmr.setView(camera);
		tmr.render();				

		//Render debug lines for box2d objects.
		b2dr.render(world, camera.combined.scl(PPM));
		
		//Iterate through entities in the world to render
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		//render all of the entities in the world
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
	
	@Override
	public void transitionState() {
		switch (nextState) {
		case RESPAWN:
			//Inform the server that we have finished transitioning to tell them to make us a new player.
			HadalGame.client.client.sendTCP(new Packets.ClientFinishTransition(new Loadout(gsm.getRecord()), nextState));
			
			//Make the screen fade back in
			fadeDelta = -0.015f;
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case RESULTS:
			getGsm().addResultsState(this, ClientState.class);
			break;
		case SPECTATOR:
			//When ded but other players alive, spectate a player
			
			//Make the screen fade back in
			fadeDelta = -0.015f;
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case NEWLEVEL:
			
			//Tell the server that we are ready to be sent to a new level with a given loadout (from records)
			HadalGame.client.client.sendTCP(new Packets.ClientFinishTransition(new Loadout(gsm.getRecord()), nextState));
			break;
		case NEXTSTAGE:
			
			//Tell the server that we are ready to be sent to a new level with our current loadout
			HadalGame.client.client.sendTCP(new Packets.ClientFinishTransition(player.getPlayerData().getLoadout(), nextState));
			break;
		case TITLE:
			getGsm().removeState(ClientState.class);
			break;
		default:
			break;
		}	
	}
	
	/**
	 * This is called whenever the client is told to add an object to its world.
	 * @param entityId: The unique id of the new entity
	 * @param entity: The entity to be added
	 * @param layer: is this layer a hitbox (rendered underneath) or not?
	 */
	public void addEntity(String entityId, HadalEntity entity, ObjectSyncLayers layer) {
		Object[] packet = {entityId, entity, layer};
		createListClient.add(packet);
	}
	
	/**
	 * This is called whenever the client is told to remove an object from the world.
	 * @param entityId: The unique id of the object to be removed.
	 */
	public void removeEntity(String entityId) {
		removeListClient.add(entityId);
	}

	/**
	 * This is called whenever the client is told to synchronize an object from the world.
	 * @param entityId: The unqie id of the object to be synchronized
	 * @param o: The SyncEntity Packet to use to sychronize the object
	 */
	public void syncEntity(String entityId, Object o) {
		Object[] packet = {entityId, o};
		sync.add(packet);
	}
	
	/**
	 * This looks at the entities in the world and returns the one with the given id. 
	 * @param entityId: Unique id of he object to find
	 * @return: THe found object (or null if nonexistent)
	 */
	public HadalEntity findEntity(String entityId) {
		HadalEntity entity = entities.get(entityId);
		if (entity != null ) {
			return entity;
		} else {
			return hitboxes.get(entityId);
		} 
	}
	
	/**
	 * The destroy and create methods do nothing for the client. 
	 * Objects cannot be created and destroyed in this way for the client, only by calling add and remove Entity.
	 */
	@Override
	public void destroy(HadalEntity entity) {}
	
	@Override
	public void create(HadalEntity entity) {}
	
	/**
	 * Z-Axis Layers that entities can be added to. ATM, there is just 1 for hitboxes beneath everything else.
	 * @author Zachary Tu
	 *
	 */
	public enum ObjectSyncLayers {
		STANDARD,
		HBOX
	}
}
