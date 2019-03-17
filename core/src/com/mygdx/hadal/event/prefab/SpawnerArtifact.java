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
public class SpawnerArtifact extends Prefabrication {

	private String triggeredToId, triggeredBackId, triggeringId;
	private String pool;
	
	public SpawnerArtifact(PlayState state, int width, int height, int x, int y, String triggeredToId, String triggeredBackId, 
			String triggeringId, String pool) {
		super(state, width, height, x , y);
		this.triggeredToId = triggeredToId;
		this.triggeredBackId = triggeredBackId;
		this.triggeringId = triggeringId;
		this.pool = pool;
	}
	
	@Override
	public void generateParts() {
		String pickupId = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject spawner = new RectangleMapObject();
		spawner.getRectangle().set(x, y, width, height);
		spawner.setName("EventMove");
		spawner.getProperties().put("sync", 2);
		spawner.getProperties().put("scale", 0.25f);
		spawner.getProperties().put("sprite", "BASE");
		spawner.getProperties().put("triggeredId", triggeredToId);
		spawner.getProperties().put("triggeringId", pickupId);
		
		RectangleMapObject back = new RectangleMapObject();
		back.getRectangle().set(0, 0, width, height);
		back.setName("EventMove");
		back.getProperties().put("triggeredId", triggeredBackId);
		back.getProperties().put("triggeringId", pickupId);
		
		RectangleMapObject artifact = new RectangleMapObject();
		artifact.getRectangle().set(x, y, width, height);
		artifact.setName("Artifact");
		artifact.getProperties().put("synced", true);
		artifact.getProperties().put("particle_amb", "EVENT_HOLO");
		artifact.getProperties().put("triggeredId", pickupId);
		artifact.getProperties().put("triggeringId", triggeringId);
		artifact.getProperties().put("pool", pool);
		
		TiledObjectUtil.parseTiledEvent(state, spawner);
		TiledObjectUtil.parseTiledEvent(state, back);
		TiledObjectUtil.parseTiledEvent(state, artifact);
	}
}
