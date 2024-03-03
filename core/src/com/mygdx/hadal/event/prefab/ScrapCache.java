package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The Scrap Cache creates some scrap (if the cache flag has not already been raised)
 * Interacting with this item sets the cache to already taken.
 * @author Neugene Nifferty
 */
public class ScrapCache extends Prefabrication {

	private static final String PICKUP_ID = "SCRAP_PICKUP";
	private static final String SPAWNER_ID = "SCRAP_SPAWNER";
	private static final String EFFECT_ID = "SCRAP_EFFECT";
	private static final String BACK_ID = "SCRAP_BACK";
	private static final String SCRAP_ID = "SCRAP_SCRAP";
	private static final String CACHE_ID = "SCRAP_CACHE";

	private final String triggeredId, triggeringId;
	private final String cacheId;

	//the amount of scrap this prefab will give
	private final int scrapAmount;
	
	private String pickupId, spawnerId;
	
	public ScrapCache(PlayState state, float width, float height, float x, float y, String triggeredId,
					  String triggeringId, String cacheId, int scrapAmount) {
		super(state, width, height, x , y);
		this.triggeredId = triggeredId;
		this.triggeringId = triggeringId;
		this.cacheId = cacheId;
		this.scrapAmount = scrapAmount;
	}
	
	@Override
	public void generateParts() {
		pickupId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, PICKUP_ID, x, y);
		spawnerId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, SPAWNER_ID, x, y);
		String onPickupId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, EFFECT_ID, x, y);
		String pickupBackId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, BACK_ID, x, y);
		String scrapSpawnId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, SCRAP_ID, x, y);
		String cacheSetId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, CACHE_ID, x, y);
		
		RectangleMapObject cacheCheck = new RectangleMapObject();
		cacheCheck.setName("QuestCheck");
		cacheCheck.getProperties().put("quest", cacheId);
		cacheCheck.getProperties().put("check", 0);
		cacheCheck.getProperties().put("triggeredId", triggeredId);
		cacheCheck.getProperties().put("triggeringId", spawnerId);
		
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
		onPickup.getProperties().put("triggeringId", pickupBackId + "," + scrapSpawnId + "," +  cacheSetId + "," + triggeringId);
		
		RectangleMapObject back = new RectangleMapObject();
		back.getRectangle().set(0, 0, width, height);
		back.setName("EventMove");
		back.getProperties().put("triggeredId", pickupBackId);
		back.getProperties().put("triggeringId", pickupId);
		
		RectangleMapObject scrapSpawn = new RectangleMapObject();
		scrapSpawn.getRectangle().set(x + 32, y + height / 2.0f + 48, 32, 32);
		scrapSpawn.setName("ScrapSpawn");
		scrapSpawn.getProperties().put("scrap", scrapAmount);
		scrapSpawn.getProperties().put("triggeredId", scrapSpawnId);
		
		RectangleMapObject cacheSet = new RectangleMapObject();
		cacheSet.setName("QuestChange");
		cacheSet.getProperties().put("quest", cacheId);
		cacheSet.getProperties().put("change", 1);
		cacheSet.getProperties().put("triggeredId", cacheSetId);
		
		TiledObjectUtil.parseAddTiledEvent(state, cacheCheck);
		TiledObjectUtil.parseAddTiledEvent(state, spawner);
		TiledObjectUtil.parseAddTiledEvent(state, pickup);
		TiledObjectUtil.parseAddTiledEvent(state, onPickup);
		TiledObjectUtil.parseAddTiledEvent(state, back);
		TiledObjectUtil.parseAddTiledEvent(state, scrapSpawn);
		TiledObjectUtil.parseAddTiledEvent(state, cacheSet);
	}
	
	@Override
	public Array<String> getConnectedEvents() {
		Array<String> events = new Array<>();
		events.add(pickupId);
		events.add(spawnerId);
		return events;
	}
}
