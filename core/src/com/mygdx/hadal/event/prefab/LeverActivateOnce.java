package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The LeverActivate is a lever that can be activated once. The base is always green and the lever animates when pulled.
 * After pulling, the base is red and the lever is still.
 * @author Mubonnier Migmarole
 */
public class LeverActivateOnce extends Prefabrication {

	private static final String BASE_ID = "LEVER_ONCE_BASE";
	private static final String LEVER_ID = "LEVER_ONCE_LEVER";

	private final String triggeringId;
	private String baseId, leverId;
	
	public LeverActivateOnce(PlayState state, float width, float height, float x, float y, String triggeringId) {
		super(state, width, height, x , y);
		this.triggeringId = triggeringId;
	}
	
	@Override
	public void generateParts() {
		
		baseId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeringId, BASE_ID, x, y);
		leverId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeringId, LEVER_ID, x, y);
		
		String condId = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String altId = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String multiId = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String spriteId1 = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String spriteId2 = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		
		RectangleMapObject base = new RectangleMapObject();
		base.getRectangle().set(x, y, width, height);
		base.setName("Dummy");
		base.getProperties().put("sprite", "BASE_GREEN");
		base.getProperties().put("align", "CENTER_BOTTOM");
		base.getProperties().put("syncServer", "ECHO_ACTIVATE");
		base.getProperties().put("syncClient", "ECHO");
		base.getProperties().put("triggeredId", baseId);

		RectangleMapObject lever = new RectangleMapObject();
		lever.getRectangle().set(x, y, width, height);
		lever.setName("Switch");
		lever.getProperties().put("sprite", "LEVER");
		lever.getProperties().put("still", true);
		lever.getProperties().put("frame", 0);
		lever.getProperties().put("align", "CENTER_BOTTOM");
		lever.getProperties().put("syncServer", "ECHO_ACTIVATE");
		lever.getProperties().put("syncClient", "ECHO");
		lever.getProperties().put("particle_std", "MOMENTUM");
		lever.getProperties().put("triggeredId", leverId);
		lever.getProperties().put("triggeringId", condId);
		
		RectangleMapObject cond = new RectangleMapObject();
		cond.setName("Condtrigger");
		cond.getProperties().put("start", multiId);
		cond.getProperties().put("triggeredId", condId);
		cond.getProperties().put("triggeringId", multiId);
		
		RectangleMapObject alt = new RectangleMapObject();
		alt.setName("Alttrigger");
		alt.getProperties().put("message", "");
		alt.getProperties().put("triggeredId", altId);
		alt.getProperties().put("triggeringId", condId);
		
		RectangleMapObject use = new RectangleMapObject();
		use.setName("Multitrigger");
		use.getProperties().put("triggeredId", multiId);
		use.getProperties().put("triggeringId", triggeringId+ ","+ spriteId1 + "," + spriteId2 + "," + altId);
		
		RectangleMapObject sprite1 = new RectangleMapObject();
		sprite1.setName("SpriteChange");
		sprite1.getProperties().put("newSprite", "LEVER");
		sprite1.getProperties().put("still", false);
		sprite1.getProperties().put("mode", "NORMAL");
		sprite1.getProperties().put("speed", 0.02f);
		sprite1.getProperties().put("align", "CENTER_BOTTOM");
		sprite1.getProperties().put("triggeredId", spriteId1);
		sprite1.getProperties().put("triggeringId", leverId);
		
		RectangleMapObject sprite2 = new RectangleMapObject();
		sprite2.setName("SpriteChange");
		sprite2.getProperties().put("newSprite", "BASE_RED");
		sprite2.getProperties().put("align", "CENTER_BOTTOM");
		sprite2.getProperties().put("triggeredId", spriteId2);
		sprite2.getProperties().put("triggeringId", baseId);
		
		TiledObjectUtil.parseAddTiledEvent(state, lever);
		TiledObjectUtil.parseAddTiledEvent(state, base);
		TiledObjectUtil.parseAddTiledEvent(state, cond);
		TiledObjectUtil.parseAddTiledEvent(state, alt);
		TiledObjectUtil.parseAddTiledEvent(state, use);
		TiledObjectUtil.parseAddTiledEvent(state, sprite1);
		TiledObjectUtil.parseAddTiledEvent(state, sprite2);
	}
	
	@Override
	public Array<String> getConnectedEvents() {
		Array<String> events = new Array<>();
		events.add(baseId);
		events.add(leverId);
		return events;
	}
}
