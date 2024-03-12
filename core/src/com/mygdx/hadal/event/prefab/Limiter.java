package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The Limiter is a prefab that is placed between triggerer and triggered to limit the number of times the trigger can activate
 * @author Gnathrop Gnurreau
 */
public class Limiter extends Prefabrication {

	private static final String MULTI_ID = "LIMITER_MULTI";
	private static final String COUNTER_ID = "LIMITER_COUNTER";
	private static final String ALT_ID = "LIMITER_ALT";

	//Ids of triggered and triggering events
	private final String triggeredId, triggeringId;

	//Number of times it can trigger
	private final int limit;
	
	public Limiter(PlayState state, String triggeredId, String triggeringId, int limit) {
		super(state);
		this.triggeredId = triggeredId;
		this.triggeringId = triggeringId;
		this.limit = limit;
	}
	
	@Override
	public void generateParts() {
		String multiId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, MULTI_ID, x, y);
		String counterId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, COUNTER_ID, x, y);
		String altId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, ALT_ID, x, y);
		
		RectangleMapObject conditional = new RectangleMapObject();
		conditional.setName("Condtrigger");
		conditional.getProperties().put("start", multiId);
		conditional.getProperties().put("triggeredId", triggeredId);
		conditional.getProperties().put("triggeringId", multiId);
		
		RectangleMapObject multi = new RectangleMapObject();
		multi.setName("Multitrigger");
		multi.getProperties().put("triggeredId", multiId);
		multi.getProperties().put("triggeringId", counterId + "," + triggeringId);

		RectangleMapObject counter = new RectangleMapObject();
		counter.setName("Counter");
		counter.getProperties().put("count", limit);
		counter.getProperties().put("triggeredId", counterId);
		counter.getProperties().put("triggeringId", altId);
		
		RectangleMapObject alt = new RectangleMapObject();
		alt.setName("Alttrigger");
		alt.getProperties().put("message", "");
		alt.getProperties().put("triggeredId", altId);
		alt.getProperties().put("triggeringId", triggeredId);
		
		TiledObjectUtil.parseAddTiledEvent(state, conditional);
		TiledObjectUtil.parseAddTiledEvent(state, multi);
		TiledObjectUtil.parseAddTiledEvent(state, counter);
		TiledObjectUtil.parseAddTiledEvent(state, alt);
	}
}
