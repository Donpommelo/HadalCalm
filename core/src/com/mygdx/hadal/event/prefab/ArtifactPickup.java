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
public class ArtifactPickup extends Prefabrication {

	private String triggeredId, triggeringId;
	private String pool;
	
	public ArtifactPickup(PlayState state, int width, int height, int x, int y, String triggeredId, String triggeringId, String pool) {
		super(state, width, height, x , y);
		this.triggeredId = triggeredId;
		this.triggeringId = triggeringId;
		this.pool = pool;
	}
	
	@Override
	public void generateParts() {
		RectangleMapObject artifact = new RectangleMapObject();
		artifact.getRectangle().set(x, y, width, height);
		artifact.setName("Artifact");
		artifact.getProperties().put("align", 2);
		artifact.getProperties().put("sprite", "event_cube");
		artifact.getProperties().put("particle_amb", "EVENT_HOLO");
		artifact.getProperties().put("triggeredId", triggeredId);
		artifact.getProperties().put("triggeringId", triggeringId);
		artifact.getProperties().put("pool", pool);
		
		RectangleMapObject spawner = new RectangleMapObject();
		spawner.getRectangle().set(x, y, width, height);
		spawner.setName("Dummy");
		spawner.getProperties().put("align", 2);
		spawner.getProperties().put("scale", 0.25f);
		spawner.getProperties().put("sprite", "event_base");
		
		TiledObjectUtil.parseTiledEvent(state, spawner);
		TiledObjectUtil.parseTiledEvent(state, artifact);
	}
}
