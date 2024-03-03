package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The Spawner Unlock Prefab creates a designated unlock when triggered (if the item has not already been unlocked)
 * Interacting with this item unlocks it.
 * @author Sculario Starnitas
 */
public class SpawnerUnlockable extends Prefabrication {

	private static final String PICKUP_ID = "SPAWNER_UNLOCK_PICKUP";
	private static final String SPAWNER_ID = "SPAWNER_UNLOCK_SPAWNER";
	private static final String EFFECT_ID = "SPAWNER_UNLOCK_EFFECT";
	private static final String BACK_ID = "SPAWNER_UNLOCK_BACK";
	private static final String UNLOCK_ID = "SPAWNER_UNLOCK_UNLOCK";

	private final String triggeredId, triggeringId;

	//the type of unlock (equip, artifact, etc) and the name of the unlock
	private final String type, name;
	
	private String pickupId, spawnerId;
	
	public SpawnerUnlockable(PlayState state, float width, float height, float x, float y, String triggeredId,
							 String triggeringId, String type, String name) {
		super(state, width, height, x , y);
		this.triggeredId = triggeredId;
		this.triggeringId = triggeringId;
		this.type = type;
		this.name = name;
	}
	
	@Override
	public void generateParts() {
		pickupId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, PICKUP_ID, x, y);
		spawnerId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, SPAWNER_ID, x, y);
		String onPickupId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, EFFECT_ID, x, y);
		String pickupBackId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, BACK_ID, x, y);
		String unlockSetId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, UNLOCK_ID, x, y);
		
		RectangleMapObject unlockCheck = new RectangleMapObject();
		unlockCheck.setName("UnlockCheck");
		unlockCheck.getProperties().put("type", type);
		unlockCheck.getProperties().put("item", name);
		unlockCheck.getProperties().put("unlock", false);
		unlockCheck.getProperties().put("triggeredId", triggeredId);
		unlockCheck.getProperties().put("triggeringId", spawnerId);
		
		RectangleMapObject spawner = new RectangleMapObject();
		spawner.getRectangle().set(x, y, width, height);
		spawner.setName("EventMove");
		spawner.getProperties().put("syncServer", "ECHO_ACTIVATE");
		spawner.getProperties().put("syncClient", "ECHO");
		spawner.getProperties().put("particle_std", "EVENT_HOLO");
		spawner.getProperties().put("scale", 0.25f);
		spawner.getProperties().put("sprite", "BASE");
		spawner.getProperties().put("triggeredId", spawnerId);
		spawner.getProperties().put("triggeringId", pickupId);

		RectangleMapObject pickup = new RectangleMapObject();
		pickup.getRectangle().set(0, 0, width, height);
		pickup.setName("Switch");
		pickup.getProperties().put("particle_amb", "EVENT_HOLO");
		pickup.getProperties().put("sprite", "CUBE");
		pickup.getProperties().put("triggeredId", pickupId);
		pickup.getProperties().put("triggeringId", onPickupId);
		
		RectangleMapObject onPickup = new RectangleMapObject();
		onPickup.setName("Multitrigger");
		onPickup.getProperties().put("triggeredId", onPickupId);
		onPickup.getProperties().put("triggeringId", pickupBackId + "," + unlockSetId + "," + triggeringId);
		
		RectangleMapObject back = new RectangleMapObject();
		back.getRectangle().set(0, 0, width, height);
		back.setName("EventMove");
		back.getProperties().put("triggeredId", pickupBackId);
		back.getProperties().put("triggeringId", pickupId);
		
		RectangleMapObject unlockSet = new RectangleMapObject();
		unlockSet.setName("ItemUnlock");
		unlockSet.getProperties().put("type", type);
		unlockSet.getProperties().put("item", name);
		unlockSet.getProperties().put("unlock", true);
		unlockSet.getProperties().put("triggeredId", unlockSetId);
		
		TiledObjectUtil.parseAddTiledEvent(state, unlockCheck);
		TiledObjectUtil.parseAddTiledEvent(state, spawner);
		TiledObjectUtil.parseAddTiledEvent(state, pickup);
		TiledObjectUtil.parseAddTiledEvent(state, onPickup);
		TiledObjectUtil.parseAddTiledEvent(state, back);
		TiledObjectUtil.parseAddTiledEvent(state, unlockSet);
	}
	
	@Override
	public Array<String> getConnectedEvents() {
		Array<String> events = new Array<>();
		events.add(pickupId);
		events.add(spawnerId);
		return events;
	}
}
