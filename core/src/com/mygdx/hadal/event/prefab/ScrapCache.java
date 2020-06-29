package com.mygdx.hadal.event.prefab;

import java.util.ArrayList;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The Spawner Unlock Prefab creates s designated unlock when triggered (if the item has not already been unlocked)
 * Interacting with this item unlocks it.
 * @author Zachary Tu
 *
 */
public class ScrapCache extends Prefabrication {

	private String triggeredId, triggeringId;
	private String cacheId;
	private int scrapAmount;
	
	private String pickupId, spawnerId;
	
	public ScrapCache(PlayState state, int width, int height, int x, int y, String triggeredId, String triggeringId, String cacheId, int scrapAmount) {
		super(state, width, height, x , y);
		this.triggeredId = triggeredId;
		this.triggeringId = triggeringId;
		this.cacheId = cacheId;
		this.scrapAmount = scrapAmount;
	}
	
	@Override
	public void generateParts() {
		pickupId = TiledObjectUtil.getPrefabTriggerId();
		spawnerId = TiledObjectUtil.getPrefabTriggerId();
		String onPickupId = TiledObjectUtil.getPrefabTriggerId();
		String pickupBackId = TiledObjectUtil.getPrefabTriggerId();
		String scrapSpawnId = TiledObjectUtil.getPrefabTriggerId();
		String cacheSetId = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject cacheCheck = new RectangleMapObject();
		cacheCheck.setName("QuestCheck");
		cacheCheck.getProperties().put("quest", cacheId);
		cacheCheck.getProperties().put("check", 0);
		cacheCheck.getProperties().put("triggeredId", triggeredId);
		cacheCheck.getProperties().put("triggeringId", spawnerId);
		
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
		onPickup.getProperties().put("triggeringId", pickupBackId + "," + scrapSpawnId + "," +  cacheSetId + "," + triggeringId);
		
		RectangleMapObject back = new RectangleMapObject();
		back.getRectangle().set(0, 0, width, height);
		back.setName("EventMove");
		back.getProperties().put("triggeredId", pickupBackId);
		back.getProperties().put("triggeringId", pickupId);
		
		RectangleMapObject scrapSpawn = new RectangleMapObject();
		scrapSpawn.getRectangle().set(x + 32, y + height / 2 + 48, 32, 32);
		scrapSpawn.setName("ScrapSpawn");
		scrapSpawn.getProperties().put("scrap", scrapAmount);
		scrapSpawn.getProperties().put("triggeredId", scrapSpawnId);
		
		RectangleMapObject cacheSet = new RectangleMapObject();
		cacheSet.setName("QuestChange");
		cacheSet.getProperties().put("quest", cacheId);
		cacheSet.getProperties().put("change", 1);
		cacheSet.getProperties().put("triggeredId", cacheSetId);
		
		TiledObjectUtil.parseTiledEvent(state, cacheCheck);
		TiledObjectUtil.parseTiledEvent(state, spawner);
		TiledObjectUtil.parseTiledEvent(state, pickup);
		TiledObjectUtil.parseTiledEvent(state, onPickup);
		TiledObjectUtil.parseTiledEvent(state, back);
		TiledObjectUtil.parseTiledEvent(state, scrapSpawn);
		TiledObjectUtil.parseTiledEvent(state, cacheSet);
	}
	
	@Override
	public ArrayList<String> getConnectedEvents() {
		ArrayList<String> events = new ArrayList<String>();
		events.add(pickupId);
		events.add(spawnerId);
		return events;
	}
}
