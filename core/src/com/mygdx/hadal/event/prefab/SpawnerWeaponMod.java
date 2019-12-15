package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The ArtifactPickup is a prefab that (atm) simply contains an artifact pickup with a spawner sprite and does the alignments.
 * Might add more to this later.
 * @author Zachary Tu
 *
 */
public class SpawnerWeaponMod extends Prefabrication {

	private String triggeredId, triggeringId;
	private String pool;
	
	public SpawnerWeaponMod(PlayState state, int width, int height, int x, int y, String triggeredId, String triggeringId, String pool) {
		super(state, width, height, x , y);
		this.triggeredId = triggeredId;
		this.triggeringId = triggeringId;
		this.pool = pool;
	}
	
	@Override
	public void generateParts() {
		String pickupId = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject base = new RectangleMapObject();
		base.getRectangle().set(x, y, width, height);
		base.setName("Dummy");
		base.getProperties().put("sync", 2);
		base.getProperties().put("sprite", "BASE");
		
		RectangleMapObject spawner = new RectangleMapObject();
		spawner.setName("Alttrigger");
		spawner.getProperties().put("message", "roll");
		spawner.getProperties().put("triggeredId", triggeredId);
		spawner.getProperties().put("triggeringId", pickupId);
		
		RectangleMapObject weapon = new RectangleMapObject();
		weapon.getRectangle().set(x, y, width, height);
		weapon.setName("WeaponMod");
		weapon.getProperties().put("synced", true);
		weapon.getProperties().put("particle_std", "EVENT_HOLO");
		weapon.getProperties().put("triggeredId", pickupId);
		weapon.getProperties().put("triggeringId", triggeringId);
		weapon.getProperties().put("pool", pool);
		
		TiledObjectUtil.parseTiledEvent(state, base);
		TiledObjectUtil.parseTiledEvent(state, spawner);
		TiledObjectUtil.parseTiledEvent(state, weapon);
	}
}
