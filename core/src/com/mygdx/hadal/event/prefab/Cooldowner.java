package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The Cooldowner is a prefab that is placed between triggerer and triggered to prevent the triggered from activating multiple times within a time limit
 * @author Folfworth Fromilius
 */
public class Cooldowner extends Prefabrication {

	//Ids of triggered and triggering events
	private final String triggeredId, triggeringId;

	//Number of times it can trigger
	private final float cooldown;
	
	public Cooldowner(PlayState state, String triggeredId, String triggeringId, float cooldown) {
		super(state);
		this.triggeredId = triggeredId;
		this.triggeringId = triggeringId;
		this.cooldown = cooldown;
	}
	
	@Override
	public void generateParts() {
		String multiId = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String altId1 = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String altId2 = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String timerId = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		
		RectangleMapObject conditional = new RectangleMapObject();
		conditional.setName("Condtrigger");
		conditional.getProperties().put("start", multiId);
		conditional.getProperties().put("triggeredId", triggeredId);
		conditional.getProperties().put("triggeringId", multiId);
		
		RectangleMapObject multi = new RectangleMapObject();
		multi.setName("Multitrigger");
		multi.getProperties().put("triggeredId", multiId);
		multi.getProperties().put("triggeringId", altId1 + "," + timerId + "," + triggeringId);
		
		RectangleMapObject alt1 = new RectangleMapObject();
		alt1.setName("Alttrigger");
		alt1.getProperties().put("message", "");
		alt1.getProperties().put("triggeredId", altId1);
		alt1.getProperties().put("triggeringId", triggeredId);
		
		RectangleMapObject alt2 = new RectangleMapObject();
		alt2.setName("Alttrigger");
		alt2.getProperties().put("message", multiId);
		alt2.getProperties().put("triggeredId", altId2);
		alt2.getProperties().put("triggeringId", triggeredId);
		
		RectangleMapObject timer = new RectangleMapObject();
		timer.setName("Timer");
		timer.getProperties().put("interval", cooldown);
		timer.getProperties().put("triggeredId", timerId);
		alt2.getProperties().put("triggeringId", altId2);

		TiledObjectUtil.parseAddTiledEvent(state, conditional);
		TiledObjectUtil.parseAddTiledEvent(state, multi);
		TiledObjectUtil.parseAddTiledEvent(state, alt1);
		TiledObjectUtil.parseAddTiledEvent(state, alt2);
		TiledObjectUtil.parseAddTiledEvent(state, timer);
	}
}
