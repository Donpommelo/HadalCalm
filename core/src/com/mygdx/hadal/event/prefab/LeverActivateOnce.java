package com.mygdx.hadal.event.prefab;

import java.util.ArrayList;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The LeverActivate is a lever that can be activated once. The base is always green and the lever animates when pulled.
 * After pulling, the pase is red and the lever is still.
 * @author Zachary Tu
 */
public class LeverActivateOnce extends Prefabrication {

	private String triggeringId;
	private String baseId, leverId;
	
	public LeverActivateOnce(PlayState state, int width, int height, int x, int y, String triggeringId) {
		super(state, width, height, x , y);
		this.triggeringId = triggeringId;
	}
	
	@Override
	public void generateParts() {
		
		baseId = TiledObjectUtil.getPrefabTriggerId();
		leverId = TiledObjectUtil.getPrefabTriggerId();
		
		String condId = TiledObjectUtil.getPrefabTriggerId();
		String altId = TiledObjectUtil.getPrefabTriggerId();
		String multiId = TiledObjectUtil.getPrefabTriggerId();
		String spriteId1 = TiledObjectUtil.getPrefabTriggerId();
		String spriteId2 = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject base = new RectangleMapObject();
		base.getRectangle().set(x, y, width, height);
		base.setName("Dummy");
		base.getProperties().put("sprite", "BASE_GREEN");
		base.getProperties().put("align", "CENTER_BOTTOM");
		base.getProperties().put("sync", "ALL");
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
		sprite1.getProperties().put("sync", "ALL");
		sprite1.getProperties().put("triggeredId", spriteId1);
		sprite1.getProperties().put("triggeringId", leverId);
		
		RectangleMapObject sprite2 = new RectangleMapObject();
		sprite2.setName("SpriteChange");
		sprite2.getProperties().put("newSprite", "BASE_RED");
		sprite2.getProperties().put("align", "CENTER_BOTTOM");
		sprite2.getProperties().put("sync", "ALL");
		sprite2.getProperties().put("triggeredId", spriteId2);
		sprite2.getProperties().put("triggeringId", baseId);
		
		TiledObjectUtil.parseTiledEvent(state, lever);
		TiledObjectUtil.parseTiledEvent(state, base);
		TiledObjectUtil.parseTiledEvent(state, cond);
		TiledObjectUtil.parseTiledEvent(state, alt);
		TiledObjectUtil.parseTiledEvent(state, use);
		TiledObjectUtil.parseTiledEvent(state, sprite1);
		TiledObjectUtil.parseTiledEvent(state, sprite2);
	}
	
	@Override
	public ArrayList<String> getConnectedEvents() {
		ArrayList<String> events = new ArrayList<String>();
		events.add(baseId);
		events.add(leverId);
		return events;
	}
}
