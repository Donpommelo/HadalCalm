package com.mygdx.hadal.event.prefab;

import java.util.ArrayList;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The WeaponPickup is a prefab that (atm) simply contains a weapon pickup with a spawner sprite and does the alignments.
 * Might add more to this later.
 * @author Zachary Tu
 *
 */
public class SpawnerWeapon extends Prefabrication {

	private String triggeredId, triggeringId;
	private int mods;
	private String pool;
	
	private String baseId, pickupId;
	
	public SpawnerWeapon(PlayState state, int width, int height, int x, int y, String triggeredId, String triggeringId, int mods, String pool) {
		super(state, width, height, x , y);
		this.triggeredId = triggeredId;
		this.triggeringId = triggeringId;
		this.mods = mods;
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
		weapon.getRectangle().set(x, y, width, height);
		weapon.setName("Equip");
		weapon.getProperties().put("synced", true);
		weapon.getProperties().put("particle_std", "EVENT_HOLO");
		weapon.getProperties().put("triggeredId", pickupId);
		weapon.getProperties().put("triggeringId", triggeringId);
		weapon.getProperties().put("mods", mods);
		weapon.getProperties().put("pool", pool);
		
		TiledObjectUtil.parseTiledEvent(state, base);
		TiledObjectUtil.parseTiledEvent(state, spawner);
		TiledObjectUtil.parseTiledEvent(state, weapon);
	}
	
	@Override
	public ArrayList<String> getConnectedEvents() {
		ArrayList<String> events = new ArrayList<String>();
		events.add(pickupId);
		events.add(baseId);
		return events;
	}
}
