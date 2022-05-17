package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The LeverActivate is a lever that can be activated whenever. The base is always green and the lever animates when pulled.
 * @author Bildwin Begory
 */
public class LeverActivate extends Prefabrication {

	private final String triggeringId;
	private String baseId, leverId;
	
	public LeverActivate(PlayState state, float width, float height, float x, float y, String triggeringId) {
		super(state, width, height, x, y);
		this.triggeringId = triggeringId;
	}
	
	@Override
	public void generateParts() {
		
		baseId = TiledObjectUtil.getPrefabTriggerId();
		leverId = TiledObjectUtil.getPrefabTriggerId();
		String multiId = TiledObjectUtil.getPrefabTriggerId();
		String spriteId1 = TiledObjectUtil.getPrefabTriggerId();

		RectangleMapObject base = new RectangleMapObject();
		base.getRectangle().set(x, y, width, height);
		base.setName("Dummy");
		base.getProperties().put("align", "CENTER_BOTTOM");
		base.getProperties().put("sync", "ALL");
		base.getProperties().put("sprite", "BASE_GREEN");
		base.getProperties().put("triggeredId", baseId);
		
		RectangleMapObject lever = new RectangleMapObject();
		lever.getRectangle().set(x, y, width, height);
		lever.setName("Switch");
		lever.getProperties().put("sprite", "LEVER");
		lever.getProperties().put("still", true);
		lever.getProperties().put("frame", 0);
		lever.getProperties().put("align", "CENTER_BOTTOM");
		lever.getProperties().put("sync", "ALL");
		lever.getProperties().put("particle_std", "MOMENTUM");
		lever.getProperties().put("triggeredId", leverId);
		lever.getProperties().put("triggeringId", multiId);
		
		RectangleMapObject use = new RectangleMapObject();
		use.setName("Multitrigger");
		use.getProperties().put("triggeredId", multiId);
		use.getProperties().put("triggeringId", triggeringId+ ","+ spriteId1);
		
		RectangleMapObject sprite1 = new RectangleMapObject();
		sprite1.setName("SpriteChange");
		sprite1.getProperties().put("newSprite", "LEVER");
		sprite1.getProperties().put("still", false);
		sprite1.getProperties().put("mode", "NORMAL");
		sprite1.getProperties().put("speed", 0.02f);
		sprite1.getProperties().put("align", "CENTER_BOTTOM");
		sprite1.getProperties().put("sync", "ALL");
		sprite1.getProperties().put("triggeredId", spriteId1);
		sprite1.getProperties().put("triggeringId", leverId);
		
		TiledObjectUtil.parseTiledEvent(state, lever);
		TiledObjectUtil.parseTiledEvent(state, base);
		TiledObjectUtil.parseTiledEvent(state, use);
		TiledObjectUtil.parseTiledEvent(state, sprite1);
	}
	
	@Override
	public Array<String> getConnectedEvents() {
		Array<String> events = new Array<>();
		events.add(baseId);
		events.add(leverId);
		return events;
	}
}
