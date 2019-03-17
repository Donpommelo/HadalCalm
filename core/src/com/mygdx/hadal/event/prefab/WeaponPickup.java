package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The WeaponPickup is a prefab that (atm) simply contains a weapon pickup with a spawner sprite and does the alignments.
 * Might add more to this later.
 * @author Zachary Tu
 *
 */
public class WeaponPickup extends Prefabrication {

	private String triggeredId, triggeringId;
	private int mods;
	private String pool;
	
	public WeaponPickup(PlayState state, int width, int height, int x, int y, String triggeredId, String triggeringId, int mods, String pool) {
		super(state, width, height, x , y);
		this.triggeredId = triggeredId;
		this.triggeringId = triggeringId;
		this.mods = mods;
		this.pool = pool;
	}
	
	@Override
	public void generateParts() {
		RectangleMapObject weapon = new RectangleMapObject();
		weapon.getRectangle().set(x, y, width, height);
		weapon.setName("Equip");
		weapon.getProperties().put("align", 2);
		weapon.getProperties().put("triggeredId", triggeredId);
		weapon.getProperties().put("triggeringId", triggeringId);
		weapon.getProperties().put("mods", mods);
		weapon.getProperties().put("pool", pool);
		
		RectangleMapObject spawner = new RectangleMapObject();
		spawner.getRectangle().set(x, y, width, height);
		spawner.setName("Dummy");
		spawner.getProperties().put("align", 2);
		spawner.getProperties().put("sync", 1);
		spawner.getProperties().put("particle_amb", "EVENT_HOLO");
		spawner.getProperties().put("scale", 0.25f);
		spawner.getProperties().put("sprite", "BASE");
		
		TiledObjectUtil.parseTiledEvent(state, spawner);
		TiledObjectUtil.parseTiledEvent(state, weapon);
	}
}
