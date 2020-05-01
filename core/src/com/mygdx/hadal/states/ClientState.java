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
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * This is a version of the playstate that is provided for Clients.
 * No processing of physics and stuff like that is done here. Instead, everything is done based on instructions by the server.
 * @author Zachary Tu
 *
 */
public class ClientState extends PlayState {
	
	public final static float missedCreateThreshold = 1.0f;
	public final static float missedDeleteThreshold = 1.0f;
	public final static float initialConnectThreshold = 3.0f;
	public final static float missedDeleteCooldown = 2.0f;
	public final static float missedCreateCooldown = 2.0f;
	
	//This is a set of all non-hitbox entities in the world mapped from their entityId
	private LinkedHashMap<String, HadalEntity> entities;
	
	//This is a set of all hitboxes mapped from their unique entityId
	private LinkedHashMap<String, HadalEntity> hitboxes;
	
	//This is a list of sync instructions. It contains [entityId, object to be synced]
	private ArrayList<Object[]> sync;
	
	//This contains the position of the client's mouse, to be sent to the server
	private Vector3 mousePosition = new Vector3();
	
	//This is the time since the last missed create packet we send the server. Kept track of to avoid sending too many at once.
	private float timeSinceLastMissedCreate;
	
	public ClientState(GameStateManager gsm, Loadout loadout, UnlockLevel level) {
		super(gsm, loadout, level, false, null, true, "");
		entities = new LinkedHashMap<String, HadalEntity>();
		hitboxes = new LinkedHashMap<String, HadalEntity>();
		sync = new ArrayList<Object[]>();
		
		TiledObjectUtil.parseTiledObjectLayerClient(this, map.getLayers().get("collision-layer").getObjects());
		
		addEntity(getWorldDummy().getEntityID().toString(), getWorldDummy(), false, ObjectSyncLayers.STANDARD);
		addEntity(getMouse().getEntityID().toString(), getMouse(), false, ObjectSyncLayers.STANDARD);
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
	
	private float physicsAccumulator = 0.0f;
	private final static float physicsTime = 1 / 200f;
	private Vector3 lastMouseLocation = new Vector3();
	@Override
	public void update(float delta) {
		
		physicsAccumulator += delta;

		//this makes the physics separate from the game framerate
		while (physicsAccumulator >= physicsTime) {
			physicsAccumulator -= physicsTime;

			//The box2d world takes a step. This handles collisions + physics stuff.
			world.step(physicsTime, 8, 3);
		}
		
		//Send mouse position to the server.
		mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		HadalGame.viewportCamera.unproject(mousePosition);
		if (!lastMouseLocation.equals(mousePosition)) {
			HadalGame.client.sendUDP(new Packets.MouseMove(mousePosition.x, mousePosition.y));
		}
		lastMouseLocation.set(mousePosition);
		
		//All entities that are set to be created are created and assigned their entityId
		for (Object[] pair: createListClient) {
			if (pair[3].equals(ObjectSyncLayers.HBOX)) {
				hitboxes.putIfAbsent((String) pair[0], (HadalEntity) pair[1]);
			} else {
				entities.putIfAbsent((String) pair[0], (HadalEntity) pair[1]);
			}
			((HadalEntity) pair[1]).create();
			if (pair[0] != "") {
				((HadalEntity) pair[1]).setEntityID((String) pair[0]);
			}
			((HadalEntity) pair[1]).setReceivingSyncs((boolean) pair[2]);
			
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
		
		//process camera, ui, any received packets
		processCommonStateProperties(delta);
		
		timeSinceLastMissedCreate += delta;
		
		//All sync instructions are carried out.
		while (!sync.isEmpty()) {
			Object[] p = (Object[]) sync.remove(0);
		 	if (p != null) {
		 		HadalEntity entity = hitboxes.get(p[0]);
		 		if (entity != null) {
		 			entity.onClientSync(p[1]);
		 			entity.resetTimeSinceLastSync();
		 		} else {
		 			entity = entities.get(p[0]);
		 			
		 			//if we have the entity, synci t and reset the time since last sync
			 		if (entity != null) {
			 			entity.onClientSync(p[1]);
			 			entity.resetTimeSinceLastSync();
			 		} else {
			 			
			 			//if we don't recognize the entity and the entity is of a sufficient age and the client didn't just start up, we may have missed a create packet.
			 			if ((float) p[2] > missedCreateThreshold && getTimer() > initialConnectThreshold && timeSinceLastMissedCreate > missedCreateCooldown) {
			 				timeSinceLastMissedCreate = 0.0f;
			 				HadalGame.client.sendUDP(new Packets.MissedCreate((String) p[0]));
			 			}
			 		}
		 		}
		 	}
		}
		
		//While most objects don't do any processing on client side, the clientController is run for the exceptions.
		for (HadalEntity entity : hitboxes.values()) {
			entity.clientController(delta);
			entity.decreaseShaderCount(delta);
			entity.increaseAnimationTime(delta);
			entity.increaseTimeSinceLastSync(delta);
		}
		for (HadalEntity entity : entities.values()) {
			entity.clientController(delta);
			entity.decreaseShaderCount(delta);
			entity.increaseAnimationTime(delta);
			entity.increaseTimeSinceLastSync(delta);
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
			HadalGame.client.sendTCP(new Packets.ClientFinishRespawn());
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case RESULTS:
			
			//immediately transition to the results screen
			gsm.removeState(ClientState.class);
			gsm.addResultsState(this, resultsText, TitleState.class);
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
	 * @param: synced: should this object receive a regular sync packet from the server?
	 * @param layer: is this layer a hitbox (rendered underneath) or not?
	 */
	public void addEntity(String entityId, HadalEntity entity, boolean synced, ObjectSyncLayers layer) {
		Object[] packet = {entityId, entity, synced, layer};
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
	 * @param entityId: The unique id of the object to be synchronized
	 * @param o: The SyncEntity Packet to use to sychronize the object
	 * @param age: the age of the entity o nthe server. If we are told to sync an entity we don't have that's old enough, we missed a create packet.
	 */
	public void syncEntity(String entityId, Object o, float age) {
		Object[] packet = {entityId, o, age};
		sync.add(packet);
	}
	
	/**
	 * This looks at the entities in the world and returns the one with the given id. 
	 * @param entityId: Unique id of he object to find
	 * @return: The found object (or null if nonexistent)
	 */
	@Override
	public HadalEntity findEntity(String entityId) {
		HadalEntity entity = entities.get(entityId);
		if (entity != null) {
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
