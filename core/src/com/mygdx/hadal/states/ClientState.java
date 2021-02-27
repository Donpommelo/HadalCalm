package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UIPlayClient;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.ClientController;
import com.mygdx.hadal.input.CommonController;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.utils.TiledObjectUtil;

import java.util.*;

/**
 * This is a version of the playstate that is provided for Clients.
 * A lot of effects are not processed in this state like statuses and damage. Instead, everything is done based on instructions by the server.
 * @author Frornswoggle Fraginald
 */
public class ClientState extends PlayState {
	
	//these variables are used to deal with packet loss.
	// Windows to determine when a create/delete packet was dropped and when another packet can be requested
	public static final float missedCreateThreshold = 2.0f;
	public static final float missedDeleteThreshold = 2.0f;
	public static final float initialConnectThreshold = 5.0f;
	public static final float missedCreateCooldown = 4.0f;
	
	//This is a set of all non-hitbox entities in the world mapped from their entityId
	private final LinkedHashMap<String, HadalEntity> entities;
	
	//This is a set of all hitboxes mapped from their unique entityId
	private final LinkedHashMap<String, HadalEntity> hitboxes;

	//This is a set of all hitboxes mapped from their unique entityId
	private final LinkedHashMap<String, HadalEntity> effects;

	private final ArrayList<Map<String, HadalEntity>> entityLists = new ArrayList<>();

	//This is a list of sync instructions. It contains [entityId, object to be synced]
	private final ArrayList<SyncPacket> sync;

	//These sets are used by the Client for removing/adding entities.
	private final Set<String> removeListClient;
	private final Set<CreatePacket> createListClient;

	//This contains the position of the client's mouse, to be sent to the server
	private final Vector3 mousePosition = new Vector3();
	
	//This is the time since the last missed create packet we send the server. Kept track of to avoid sending too many at once.
	private float timeSinceLastMissedCreate;
	
	public ClientState(GameStateManager gsm, Loadout loadout, UnlockLevel level) {
		super(gsm, loadout, level, false, null, true, "");
		entities = new LinkedHashMap<>();
		hitboxes = new LinkedHashMap<>();
		effects = new LinkedHashMap<>();
		entityLists.add(hitboxes);
		entityLists.add(entities);
		entityLists.add(effects);
		removeListClient = new LinkedHashSet<>();
		createListClient = new LinkedHashSet<>();
		sync = new ArrayList<>();
		
		//client processes collisions
		TiledObjectUtil.parseTiledObjectLayerClient(this, map.getLayers().get("collision-layer").getObjects());
		TiledObjectUtil.parseTiledEventLayerClient(this, map.getLayers().get("event-layer").getObjects());

		//client still needs anchor points, world dummies and mouse tracker
		addEntity(getAnchor().getEntityID().toString(), getAnchor(), false, ObjectSyncLayers.STANDARD);
		addEntity(getWorldDummy().getEntityID().toString(), getWorldDummy(), false, ObjectSyncLayers.STANDARD);
		addEntity(getMouse().getEntityID().toString(), getMouse(), false, ObjectSyncLayers.STANDARD);
	}
	
	@Override
	public void resetController() {

		//we check if we are in a playstate (not paused or in setting menu) b/c we don't reset control in those states
		if (!gsm.getStates().empty()) {
			if (gsm.getStates().peek() instanceof PlayState) {

				//Whenever the controller is reset, the client gets a new client controller.
				controller = new ClientController(player, this);
				
				InputMultiplexer inputMultiplexer = new InputMultiplexer();
				inputMultiplexer.addProcessor(stage);
				inputMultiplexer.addProcessor(controller);
				inputMultiplexer.addProcessor(new CommonController(this));
				Gdx.input.setInputProcessor(inputMultiplexer);
			}
		}
	}
	
	//these control the frequency that we process world physics.
	private float physicsAccumulator;
	private static final float physicsTime = 1 / 200f;
	
	//these control the frequency that we send latency checking packets to the server.
	private float latencyAccumulator;
	private float lastLatencyCheck, latency;
	private static final float LatencyCheck = 1.0f;
	
	private final Vector3 lastMouseLocation = new Vector3();
	@Override
	public void update(float delta) {
		
		//this makes the physics separate from the game framerate
		physicsAccumulator += delta;
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
		for (CreatePacket packet: createListClient) {
			if (packet.layer.equals(ObjectSyncLayers.HBOX)) {
				if (hitboxes.putIfAbsent(packet.entityId, packet.entity) == null) {
					packet.entity.create();
				}
			} else if (packet.layer.equals(ObjectSyncLayers.EFFECT)) {
				if (effects.putIfAbsent(packet.entityId, packet.entity) == null) {
					packet.entity.create();
				}
			} else {
				if (entities.putIfAbsent(packet.entityId, packet.entity) == null) {
					packet.entity.create();
				}
			}
			if (!packet.entityId.equals("")) {
				packet.entity.setEntityID(packet.entityId);
			}
			packet.entity.setReceivingSyncs(packet.synced);
		}
		createListClient.clear();
		
		//All entities that are set to be removed are removed.
		for (String key: removeListClient) {
			HadalEntity entity = findEntity(key);
			if (entity != null) {
				entity.dispose();
			}
			for (Map<String, HadalEntity> m: entityLists) {
				m.remove(key);
			}
		}
		removeListClient.clear();
		
		//process camera, ui, any received packets
		processCommonStateProperties(delta, false);
		
		//this makes the latency checking separate from the game framerate
		latencyAccumulator += delta;
		if (latencyAccumulator >= LatencyCheck) {
			latencyAccumulator = 0;
			lastLatencyCheck = getTimer();
			HadalGame.client.sendTCP(new Packets.LatencySyn((int) (latency * 1000)));
		}
				
		timeSinceLastMissedCreate += delta;
		
		//All sync instructions are carried out.
		while (!sync.isEmpty()) {
			SyncPacket p = sync.remove(0);
		 	if (p != null) {
		 		HadalEntity entity = hitboxes.get(p.entityId);
		 		if (entity != null) {
		 			entity.onReceiveSync(p.packet, p.timestamp);
		 			entity.resetTimeSinceLastSync();
		 		} else {
					entity = effects.get(p.entityId);
					if (entity != null) {
						entity.onReceiveSync(p.packet, p.timestamp);
						entity.resetTimeSinceLastSync();
					} else {
						entity = entities.get(p.entityId);

						//if we have the entity, sync it and reset the time since last sync
						if (entity != null) {
							entity.onReceiveSync(p.packet, p.timestamp);
							entity.resetTimeSinceLastSync();
						} else {

							//if we don't recognize the entity and the entity is of a sufficient age and the client didn't just start up, we may have missed a create packet.
							if (p.age > missedCreateThreshold && getTimer() > initialConnectThreshold && timeSinceLastMissedCreate > missedCreateCooldown) {
								timeSinceLastMissedCreate = 0.0f;
								HadalGame.client.sendUDP(new Packets.MissedCreate(p.entityId));
							}
						}
					}
				}
		 	}
		}
		
		//While most objects don't do any processing on client side, the clientController is run for the exceptions.
		for (Map<String, HadalEntity> m: entityLists) {
			for (HadalEntity entity : m.values()) {
				entity.clientController(delta);
				entity.decreaseShaderCount(delta);
				entity.increaseAnimationTime(delta);
				entity.increaseTimeSinceLastSync(delta);
			}
		}

		//periodically update score window if scores have been updated
		scoreSyncAccumulator += delta;
		if (scoreSyncAccumulator >= scoreSyncTime) {
			scoreSyncAccumulator = 0;
			boolean changeMade = false;
			for (User user : HadalGame.client.getUsers().values()) {
				if (user.isScoreUpdated()) {
					changeMade = true;
					user.setScoreUpdated(false);
				}
			}
			if (changeMade) {
				scoreWindow.syncScoreTable();
			}
		}
	}
	
	@Override
	public void renderEntities(float delta) {
		for (Map<String, HadalEntity> m: entityLists) {
			for (HadalEntity entity : m.values()) {
				renderEntity(entity);
			}
		}
	}
	
	@Override
	public void transitionState() {
		switch (nextState) {
		case RESPAWN:
			gsm.getApp().fadeIn();
			spectatorMode = false;
			
			//Inform the server that we have finished transitioning to tell them to make us a new player.
			HadalGame.client.sendTCP(new Packets.ClientFinishRespawn(new Loadout(gsm.getLoadout())));
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case RESULTS:
			
			//immediately transition to the results screen
			gsm.removeState(SettingState.class, false);
			gsm.removeState(AboutState.class, false);
			gsm.removeState(PauseState.class, false);
			gsm.removeState(ClientState.class, false);
			gsm.addResultsState(this, resultsText, LobbyState.class);
			gsm.addResultsState(this, resultsText, TitleState.class);
			break;
		case SPECTATOR:
			//When ded but other players alive, spectate a player
			gsm.getApp().fadeIn();

			setSpectatorMode();

			//sometimes, the client can miss the server's delete packet. if so, delete own player automatically
			if (player != null) {
				if (player.isAlive()) {
					removeEntity(player.getEntityID().toString());
				}
			}
			
			//Make nextState null so we can transition again
			nextState = null;
			break;
		case NEWLEVEL:
		case NEXTSTAGE:
			//In these cases, we wait for the server to create a new playstate in which we connect again
			gsm.removeState(SettingState.class, false);
			gsm.removeState(AboutState.class, false);
			gsm.removeState(PauseState.class, false);
			break;
		case TITLE:
			gsm.removeState(SettingState.class, false);
			gsm.removeState(AboutState.class, false);
			gsm.removeState(PauseState.class, false);
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
	 * @param synced: should this object receive a regular sync packet from the server?
	 * @param layer: is this layer a hitbox (rendered underneath) or not?
	 */
	public void addEntity(String entityId, HadalEntity entity, boolean synced, ObjectSyncLayers layer) {
		CreatePacket packet = new CreatePacket(entityId, entity, synced, layer);
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
	 * @param o: The SyncEntity Packet to use to synchronize the object
	 * @param age: the age of the entity on the server. If we are told to sync an entity we don't have that's old enough, we missed a create packet.
	 * @param timestamp: the time of the sync on the server.
	 */
	public void syncEntity(String entityId, Object o, float age, float timestamp) {
		SyncPacket packet = new SyncPacket(entityId, o, age, timestamp);
		sync.add(packet);
		
		//if our timer is ahead, we set it to be less than the server so we can linear interpolate to predicted position
		if (getTimer() > timestamp) {
			setTimer(timestamp - 2 * PlayState.syncTime);
		}
		
		//if our timer is lagging too far behind, we make it catch up
		if (getTimer() < timestamp - 2 * PlayState.syncTime) {
			setTimer(timestamp - 2 * PlayState.syncTime);
		}
	}

	/**
	 * This looks at the entities in the world and returns the one with the given id. 
	 * @param entityId: Unique id of he object to find
	 * @return The found object (or null if nonexistent)
	 */
	@Override
	public HadalEntity findEntity(String entityId) {
		HadalEntity entity = entities.get(entityId);
		if (entity != null) {
			return entity;
		} else {
			entity = effects.get(entityId);
			if (entity != null) {
				return entity;
			} else {
				return hitboxes.get(entityId);
			}
		}
	}
	
	/**
	 * This is run when the server responds to our latency check packet. We calculate our ping and save i.
	 */
	public void syncLatency() {
		latency = getTimer() - lastLatencyCheck;
	}
	
	@Override
	public void dispose() {
		
		//clean up all client entities. (some entities require running their dispose() to function properly (soundEntities turning off)
		for (Map<String, HadalEntity> m: entityLists) {
			for (HadalEntity entity : m.values()) {
				entity.dispose();
			}
		}

		super.dispose();
	}

	/**
	 * This class represents a packet telling the client to sync an object
	 */
	private static class SyncPacket {
		String entityId;
		Object packet;
		float age;
		float timestamp;

		public SyncPacket(String entityId, Object packet, float age, float timestamp) {
			this.entityId = entityId;
			this.packet = packet;
			this.age = age;
			this.timestamp = timestamp;
		}
	}

	/**
	 * This class represents a packet telling the client to sync an object
	 */
	public static class CreatePacket {
		String entityId;
		HadalEntity entity;
		boolean synced;
		ObjectSyncLayers layer;

		public CreatePacket(String entityId, HadalEntity entity, boolean synced, ObjectSyncLayers layer) {
			this.entityId = entityId;
			this.entity = entity;
			this.synced = synced;
			this.layer = layer;
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
	
	public UIPlayClient getUiPlay() { return (UIPlayClient) uiPlay; }

	public float getLatency() { return latency; }
	
	public Vector3 getMousePosition() { return mousePosition; }

	/**
	 * Z-Axis Layers that entities can be added to. ATM, there is just 1 for hitboxes beneath everything else.
	 */
	public enum ObjectSyncLayers {
		STANDARD,
		HBOX,
		EFFECT
	}
}
