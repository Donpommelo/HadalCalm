package com.mygdx.hadal.states;


import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UIPlayClient;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.ClientController;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
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
		super(gsm, loadout, level, false, null, true, "");
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
	
	private Vector3 lastMouseLocation = new Vector3();
	@Override
	public void update(float delta) {
		
		//Send mouse position to the server.
		mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		HadalGame.viewportCamera.unproject(mousePosition);
		if (!lastMouseLocation.equals(mousePosition)) {
			HadalGame.client.client.sendUDP(new Packets.MouseMove(mousePosition.x, mousePosition.y));
		}
		lastMouseLocation.set(mousePosition);
		
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
		
		processCommonStateProperties(delta);
		
		//While most objects don't do any processing on client side, the clientController is run for the exceptions.
		for (HadalEntity entity : hitboxes.values()) {
			entity.clientController(delta);
			entity.decreaseShaderCount(delta);
			entity.increaseAnimationTime(delta);
		}
		for (HadalEntity entity : entities.values()) {
			entity.clientController(delta);
			entity.decreaseShaderCount(delta);
			entity.increaseAnimationTime(delta);
		}
	}
	
	@Override
	public void renderEntities(float delta) {
		for (HadalEntity schmuck : entities.values()) {
			renderEntity(schmuck, delta);
		}
		for (HadalEntity hitbox : hitboxes.values()) {
			renderEntity(hitbox, delta);
		}
	}
	
	@Override
	public void transitionState() {
		switch (nextState) {
		case RESPAWN:
			gsm.getApp().fadeIn();
			//Inform the server that we have finished transitioning to tell them to make us a new player.
			HadalGame.client.client.sendTCP(new Packets.ClientFinishRespawn());
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case RESULTS:
			
			//immediately transition to the results screen
			gsm.addResultsState(this, resultsText, ClientState.class);
			break;
		case SPECTATOR:
			//When ded but other players alive, spectate a player
			gsm.getApp().fadeIn();
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case NEWLEVEL:
		case NEXTSTAGE:
			//In these cases, we wait for the server to create a new playstate in which we connect again
			break;
		case TITLE:
			gsm.removeState(ClientState.class);
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
	 * @return: The found object (or null if nonexistent)
	 */
	public HadalEntity findEntity(String entityId) {
		HadalEntity entity = entities.get(entityId);
		if (entity != null ) {
			return entity;
		} else {
			return hitboxes.get(entityId);
		} 
	}
	
	@Override
	public void dispose() {
		
		//clean up all client entities. (some entities require running their dispose() to function properly (soundEntities turning off)
		for (HadalEntity schmuck : entities.values()) {
			schmuck.dispose();
		}
		for (HadalEntity hitbox : hitboxes.values()) {
			hitbox.dispose();
		}
		
		super.dispose();
	}
	
	/**
	 * The destroy and create methods do nothing for the client. 
	 * Objects cannot be created and destroyed in this way for the client, only by calling add and remove Entity.
	 */
	@Override
	public void destroy(HadalEntity entity) {}
	
	@Override
	public void create(HadalEntity entity) {}
	
	public UIPlayClient getUiPlay() { return (UIPlayClient) uiPlay; }
	
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
