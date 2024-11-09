package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.input.CommonController;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.TiledObjectUtil;

import static com.mygdx.hadal.constants.Constants.PHYSICS_TIME;

/**
 * This is a version of the playstate that is provided for Clients.
 * A lot of effects are not processed in this state like statuses and damage. Instead, everything is done based on instructions by the server.
 * @author Frornswoggle Fraginald
 */
public class ClientState extends PlayState {
	
	//This is a set of all non-hitbox entities in the world mapped from their entityID
	private final OrderedMap<Integer, HadalEntity> entities = new OrderedMap<>();
	
	//This is a set of all hitboxes mapped from their unique entityID
	private final OrderedMap<Integer, HadalEntity> hitboxes = new OrderedMap<>();

	//This is a set of all particle effects mapped from their unique entityID
	private final OrderedMap<Integer, HadalEntity> effects = new OrderedMap<>();

	//this is a list containing all the aforementioned entity lists
	private final Array<OrderedMap<Integer, HadalEntity>> entityLists = new Array<>();

	//This is a list of sync instructions. It contains [entityID, object to be synced]
	private final Array<SyncPacket> sync = new Array<>();

	//This contains the position of the client's mouse, to be sent to the server
	private final Vector3 mousePosition = new Vector3();

	//This is the time since the last missed create packet we send the server. Kept track of to avoid sending too many at once.
	private final ObjectMap<Integer, Float> timeSinceLastMissedCreate = new ObjectMap<>();
	private final Array<Integer> missedCreatesToRemove = new Array<>();

	public ClientState(HadalGame app, UnlockLevel level, GameMode mode) {
		super(app, level, mode, false, true, "");
		entityLists.add(hitboxes);
		entityLists.add(entities);
		entityLists.add(effects);

		mode.processSettings(this);

		//client processes collisions and certain events
		TiledObjectUtil.parseTiledObjectLayer(this, map.getLayers().get("collision-layer").getObjects());
		TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get("event-layer").getObjects());

		//parse map-specific event layers (used for different modes in the same map)
		for (String layer : mode.getExtraLayers()) {
			if (map.getLayers().get(layer) != null) {
				TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get(layer).getObjects());
			}
		}
		TiledObjectUtil.parseTiledTriggerLayer();
		TiledObjectUtil.parseDesignatedEvents(this);

		AlignmentFilter.resetTeams();

		//client still needs anchor points, world dummies
		addEntity(getAnchor().getEntityID(), getAnchor(), false, ObjectLayer.STANDARD);
		addEntity(getWorldDummy().getEntityID(), getWorldDummy(), false, ObjectLayer.STANDARD);
	}

	@Override
	public void resetController() {

		//we check if we are in a playstate (not paused or in setting menu) b/c we don't reset control in those states
		if (!StateManager.states.empty()) {
			if (StateManager.states.peek() instanceof PlayState) {
				if (null != HadalGame.usm.getOwnPlayer()) {
					//Whenever the controller is reset, the client gets a new client controller.
					controller = new PlayerController(HadalGame.usm.getOwnPlayer());

					InputMultiplexer inputMultiplexer = new InputMultiplexer();
					inputMultiplexer.addProcessor(stage);
					inputMultiplexer.addProcessor(controller);
					inputMultiplexer.addProcessor(new CommonController(this));
					Gdx.input.setInputProcessor(inputMultiplexer);
				}
			}
		}
	}
	
	//these control the frequency that we process world physics.
	private float physicsAccumulator;

	//these control the frequency that we send latency checking packets to the server.
	private static final float LATENCY_CHECK = 1 / 10f;
	private float latencyAccumulator;
	private float latency;

	//separate timer used to calculate latency
	private float clientPingTimer;

	@Override
	public void update(float delta) {

		//this makes the physics separate from the game framerate
		physicsAccumulator += delta;
		while (physicsAccumulator >= PHYSICS_TIME) {
			physicsAccumulator -= PHYSICS_TIME;

			//The box2d world takes a step. This handles collisions + physics stuff.
			world.step(PHYSICS_TIME, 8, 3);
		}

		//All entities that are set to be created are created and assigned their entityID
		for (CreatePacket packet : createListClient) {
			HadalEntity oldEntity;
			if (ObjectLayer.HBOX.equals(packet.layer)) {
				oldEntity = hitboxes.get(packet.entityID);
				hitboxes.put(packet.entityID, packet.entity);
			} else if (ObjectLayer.EFFECT.equals(packet.layer)) {
				oldEntity = effects.get(packet.entityID);
				effects.put(packet.entityID, packet.entity);
			} else {
				oldEntity = entities.get(packet.entityID);
				entities.put(packet.entityID, packet.entity);
			}

			//we replace old entity with same tag b/c we must never dispose of something that hasn't been created
			//also, we must create anything that has been initialized to avoid leaking memory
			if (oldEntity != null) {
				oldEntity.dispose();
			}
			packet.entity.create();

			if (packet.entityID != null) {
				packet.entity.setEntityID(packet.entityID);
			}
			packet.entity.setReceivingSyncs(packet.synced);
		}
		createListClient.clear();

		//All entities that are set to be removed are removed.
		for (Integer key : removeListClient) {
			for (ObjectMap<Integer, HadalEntity> m : entityLists) {
				HadalEntity entity = m.get(key);
				if (entity != null) {
					entity.dispose();
					m.remove(key);
				}
			}
//			UUIDUtil.releaseUnsyncedID(key);
		}
		removeListClient.clear();

		//process camera, ui, any received packets
		processCommonStateProperties(delta, false);
		clientPingTimer += delta;

		//this makes the latency checking separate from the game framerate
		latencyAccumulator += delta;
		if (latencyAccumulator >= LATENCY_CHECK) {
			latencyAccumulator = 0;
			PacketManager.clientUDP(new Packets.LatencySyn((int) (latency * 1000), clientPingTimer));
		}

		missedCreatesToRemove.clear();
		for (ObjectMap.Entry<Integer, Float> entry : timeSinceLastMissedCreate) {
			entry.value -= delta;
			if (entry.value <= 0.0f) {
				missedCreatesToRemove.add(entry.key);
			}
		}
		for (Integer id : missedCreatesToRemove) {
			timeSinceLastMissedCreate.remove(id);
		}
		
		//All sync instructions are carried out.
		while (!sync.isEmpty()) {
			SyncPacket p = sync.removeIndex(0);
		 	if (p != null) {
				HadalEntity entity = hitboxes.get(p.entityID);
		 		if (entity != null) {
		 			entity.onReceiveSync(p.packet, p.timestamp);
		 		} else {
					entity = effects.get(p.entityID);
					if (entity != null) {
						entity.onReceiveSync(p.packet, p.timestamp);
					} else {
						entity = entities.get(p.entityID);

						//if we have the entity, sync it
						if (entity != null) {
							entity.onReceiveSync(p.packet, p.timestamp);
						}
					}
				}
		 	}
		}

		//clientController is run for the objects that process on client side.
		for (ObjectMap<Integer, HadalEntity> m : entityLists) {
			for (HadalEntity entity : m.values()) {
				entity.clientController(delta);
				entity.getShaderHelper().decreaseShaderCount(delta);
				entity.increaseAnimationTime(delta);
			}
		}

		//periodically update score window if scores have been updated
		scoreSyncAccumulator += delta;
		if (scoreSyncAccumulator >= SCORE_SYNC_TIME) {
			scoreSyncAccumulator = 0;
			boolean changeMade = false;
			for (User user : HadalGame.usm.getUsers().values()) {
				if (user.isScoreUpdated()) {
					changeMade = true;
					user.setScoreUpdated(false);
				}
			}
			if (changeMade) {
				getUIManager().getScoreWindow().syncScoreTable();
			}
		}
	}

	/**
	 * This is called whenever the client is told to add an object to its world.
	 * @param entityID: The uuid of the entity
	 * @param entity: The entity to be added
	 * @param synced: should this object receive a regular sync packet from the server?
	 * @param layer: is this layer a hitbox (rendered underneath) or not?
	 */
	public void addEntity(Integer entityID, HadalEntity entity, boolean synced, ObjectLayer layer) {
		CreatePacket packet = new CreatePacket(entityID, entity, synced, layer);
		createListClient.add(packet);
	}

	/**
	 * This is called whenever the client is told to remove an object from the world.
	 * @param entityID: The unique id of the object to be removed.
	 */
	public void removeEntity(int entityID) {
		removeListClient.add(entityID);
	}

	/**
	 * This is called whenever the client is told to synchronize an object from the world.
	 * @param entityID: The entity unique id
	 * @param o: The SyncEntity Packet to use to synchronize the object
	 * @param timestamp: the time of the sync on the server.
	 */
	public void syncEntity(int entityID, Object o, float timestamp) {
		SyncPacket packet = new SyncPacket(entityID, o, timestamp);
		sync.add(packet);
	}

	/**
	 * This looks at the entities in the world and returns the one with the given id. 
	 * @param entityID: Unique id of he object to find
	 * @return The found object (or null if nonexistent)
	 */
	@Override
	public HadalEntity findEntity(int entityID) {
		for (ObjectMap<Integer, HadalEntity> m : entityLists) {
			HadalEntity entity = m.get(entityID);
			if (entity != null) {
				return entity;
			}
		}
		return null;
	}

	/**
	 * This is run when the server responds to our latency check packet. We calculate our ping and save it.
	 */
	public void syncLatency(float serverTime, float clientTimestamp) {

		//when transitioning to new state, we don't want old timestamp to give us a negative latency
		if (clientTimestamp <= clientPingTimer) {
			latency = clientPingTimer - clientTimestamp;
			setTimer(serverTime - 2 * PlayState.SYNC_TIME);
		}
	}
	
	@Override
	public void dispose() {
		
		//clean up all client entities. (some entities require running their dispose() to function properly (soundEntities turning off)
		for (ObjectMap<Integer, HadalEntity> m : entityLists) {
			for (HadalEntity entity : m.values()) {
				entity.dispose();
			}
		}
		super.dispose();
	}

	/**
	 * This record represents a packet telling the client to sync an object
	 */
	private record SyncPacket(Integer entityID, Object packet, float timestamp) {}

	/**
	 * This record represents a packet telling the client to create an object
	 */
	public record CreatePacket(Integer entityID, HadalEntity entity, boolean synced, ObjectLayer layer) {}

	/**
	 * The destroy and create methods do nothing for the client. 
	 * Objects cannot be created and destroyed in this way for the client, only by calling add and remove Entity.
	 */
	@Override
	public void destroy(HadalEntity entity) {}
	
	@Override
	public void create(HadalEntity entity) {}

	public Array<OrderedMap<Integer, HadalEntity>> getEntityListsClient() { return entityLists; }

	public float getLatency() { return latency; }

	public Vector3 getMousePosition() { return mousePosition; }
}
