package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The LeverActivate is a lever that can be activated once. The base is always green and the lever animates when pulled.
 * After pulling, the pase is red and the lever is still.
 * @author Zachary Tu
 *
 */
public class LeverActivateOnce extends Prefabrication {

	private String triggeringId;
	
	public LeverActivateOnce(PlayState state, int width, int height, int x, int y, String triggeringId) {
		super(state, width, height, x , y);
		this.triggeringId = triggeringId;
	}
	
	@Override
	public void generateParts() {
		
		String baseId = TiledObjectUtil.getPrefabTriggerId();
		String leverId = TiledObjectUtil.getPrefabTriggerId();
		String condId = TiledObjectUtil.getPrefabTriggerId();
		String altId = TiledObjectUtil.getPrefabTriggerId();
		String multiId = TiledObjectUtil.getPrefabTriggerId();
		String spriteId1 = TiledObjectUtil.getPrefabTriggerId();
		String spriteId2 = TiledObjectUtil.getPrefabTriggerId();
		String spriteId3 = TiledObjectUtil.getPrefabTriggerId();
		String timerId = TiledObjectUtil.getPrefabTriggerId();
		String resetId = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject base = new RectangleMapObject();
		base.getRectangle().set(x, y, width, height);
		base.setName("Dummy");
		base.getProperties().put("sprite", "lever_base_green");
		base.getProperties().put("particle_std", "MOMENTUM");
		base.getProperties().put("triggeredId", baseId);

		RectangleMapObject lever = new RectangleMapObject();
		lever.getRectangle().set(x, y, width, height);
		lever.setName("Switch");
		lever.getProperties().put("sprite", "lever");
		lever.getProperties().put("still", true);
		lever.getProperties().put("frame", 0);
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
		use.getProperties().put("triggeringId", triggeringId+ ","+ spriteId1 + "," + timerId + "," + altId + "," + baseId);
		
		RectangleMapObject sprite1 = new RectangleMapObject();
		sprite1.setName("SpriteChange");
		sprite1.getProperties().put("newSprite", "lever");
		sprite1.getProperties().put("still", false);
		sprite1.getProperties().put("triggeredId", spriteId1);
		sprite1.getProperties().put("triggeringId", leverId);
		
		RectangleMapObject sprite2 = new RectangleMapObject();
		sprite2.setName("SpriteChange");
		sprite2.getProperties().put("newSprite", "lever");
		sprite2.getProperties().put("still", true);
		sprite2.getProperties().put("frame", 8);
		sprite2.getProperties().put("triggeredId", spriteId2);
		sprite2.getProperties().put("triggeringId", leverId);
		
		RectangleMapObject sprite3 = new RectangleMapObject();
		sprite3.setName("SpriteChange");
		sprite3.getProperties().put("newSprite", "lever_base_red");
		sprite3.getProperties().put("triggeredId", spriteId3);
		sprite3.getProperties().put("triggeringId", baseId);
		
		RectangleMapObject timer = new RectangleMapObject();
		timer.setName("Timer");
		timer.getProperties().put("interval", 0.5f);
		timer.getProperties().put("startOn", false);
		timer.getProperties().put("triggeredId", timerId);
		timer.getProperties().put("triggeringId", resetId);

		RectangleMapObject reset = new RectangleMapObject();
		reset.setName("Multitrigger");
		reset.getProperties().put("triggeredId", resetId);
		reset.getProperties().put("triggeringId", spriteId3+ ","+ spriteId2);
		
		TiledObjectUtil.parseTiledEvent(state, base);
		TiledObjectUtil.parseTiledEvent(state, lever);
		TiledObjectUtil.parseTiledEvent(state, cond);
		TiledObjectUtil.parseTiledEvent(state, alt);
		TiledObjectUtil.parseTiledEvent(state, use);
		TiledObjectUtil.parseTiledEvent(state, sprite1);
		TiledObjectUtil.parseTiledEvent(state, sprite2);
		TiledObjectUtil.parseTiledEvent(state, sprite3);
		TiledObjectUtil.parseTiledEvent(state, timer);
		TiledObjectUtil.parseTiledEvent(state, reset);

	}
}
