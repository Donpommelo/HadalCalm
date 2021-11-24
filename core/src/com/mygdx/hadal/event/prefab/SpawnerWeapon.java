package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The WeaponPickup is a prefab that (atm) simply contains a weapon pickup with a spawner sprite and does the alignments.
 * Might add more to this later.
 * @author Blabiscus Blagherty
 */
public class SpawnerWeapon extends Prefabrication {

	private final String triggeredId, triggeringId;
	private final String pool;
	
	private String baseId, pickupId;
	
	public SpawnerWeapon(PlayState state, int width, int height, int x, int y, String triggeredId, String triggeringId, String pool) {
		super(state, width, height, x , y);
		this.triggeredId = triggeredId;
		this.triggeringId = triggeringId;
		this.pool = pool;
	}
	
	@Override
	public void generateParts() {
		
		pickupId = TiledObjectUtil.getPrefabTriggerId();
		baseId = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject base = new RectangleMapObject();
		base.getRectangle().set(x, y, width, height);
		base.setName("Dummy");
		base.getProperties().put("triggeredId", baseId);
		base.getProperties().put("sync", "ALL");
		base.getProperties().put("sprite", "BASE");
		base.getProperties().put("align", "CENTER_BOTTOM");
		
		RectangleMapObject spawner = new RectangleMapObject();
		spawner.setName("Alttrigger");
		spawner.getProperties().put("message", "roll");
		spawner.getProperties().put("triggeredId", triggeredId);
		spawner.getProperties().put("triggeringId", pickupId);
		
		RectangleMapObject weapon = new RectangleMapObject();
		weapon.getRectangle().set(x, y + (Event.defaultPickupEventSize - height) / 2.0f, width, height);
		weapon.setName("Equip");
		weapon.getProperties().put("synced", true);
		weapon.getProperties().put("particle_std", "EVENT_HOLO");
		weapon.getProperties().put("triggeredId", pickupId);
		weapon.getProperties().put("triggeringId", triggeringId);
		weapon.getProperties().put("pool", pool);
		
		TiledObjectUtil.parseTiledEvent(state, base);
		TiledObjectUtil.parseTiledEvent(state, spawner);
		TiledObjectUtil.parseTiledEvent(state, weapon);
	}
	
	@Override
	public Array<String> getConnectedEvents() {
		Array<String> events = new Array<>();
		events.add(pickupId);
		events.add(baseId);
		return events;
	}
}
