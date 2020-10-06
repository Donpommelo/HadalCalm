package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

import java.util.ArrayList;

/**
 * The Spawner Unlock Prefab creates a designated unlock when triggered (if the item has not already been unlocked)
 * Interacting with this item unlocks it.
 * @author Zachary Tu
 */
public class SpawnerUnlockable extends Prefabrication {

	private final String triggeredId, triggeringId;
	private final String type, name;
	
	private String pickupId, spawnerId;
	
	public SpawnerUnlockable(PlayState state, int width, int height, int x, int y, String triggeredId, String triggeringId, String type, String name) {
		super(state, width, height, x , y);
		this.triggeredId = triggeredId;
		this.triggeringId = triggeringId;
		this.type = type;
		this.name = name;
	}
	
	@Override
	public void generateParts() {
		pickupId = TiledObjectUtil.getPrefabTriggerId();
		spawnerId = TiledObjectUtil.getPrefabTriggerId();
		String onPickupId = TiledObjectUtil.getPrefabTriggerId();
		String pickupBackId = TiledObjectUtil.getPrefabTriggerId();
		String unlockSetId = TiledObjectUtil.getPrefabTriggerId();
		
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
		spawner.getProperties().put("sync", "ALL");
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
		
		TiledObjectUtil.parseTiledEvent(state, unlockCheck);
		TiledObjectUtil.parseTiledEvent(state, spawner);
		TiledObjectUtil.parseTiledEvent(state, pickup);
		TiledObjectUtil.parseTiledEvent(state, onPickup);
		TiledObjectUtil.parseTiledEvent(state, back);
		TiledObjectUtil.parseTiledEvent(state, unlockSet);
	}
	
	@Override
	public ArrayList<String> getConnectedEvents() {
		ArrayList<String> events = new ArrayList<>();
		events.add(pickupId);
		events.add(spawnerId);
		return events;
	}
}
