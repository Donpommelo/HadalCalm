package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The LeverActivate is a lever that can be activated whenever. The base is always green and the lever animates when pulled.
 * @author Zachary Tu
 *
 */
public class LeverActivate extends Prefabrication {

	private String triggeringId;
	
	public LeverActivate(PlayState state, int width, int height, int x, int y, String triggeringId) {
		super(state, width, height, x , y);
		this.triggeringId = triggeringId;
	}
	
	@Override
	public void generateParts() {
		
		String leverId = TiledObjectUtil.getPrefabTriggerId();
		String multiId = TiledObjectUtil.getPrefabTriggerId();
		String spriteId1 = TiledObjectUtil.getPrefabTriggerId();
		String spriteId2 = TiledObjectUtil.getPrefabTriggerId();
		String timerId = TiledObjectUtil.getPrefabTriggerId();
		String resetId = TiledObjectUtil.getPrefabTriggerId();
		String alt1Id = TiledObjectUtil.getPrefabTriggerId();
		String alt2Id = TiledObjectUtil.getPrefabTriggerId();

		RectangleMapObject base = new RectangleMapObject();
		base.getRectangle().set(x, y, width, height);
		base.setName("Dummy");
		base.getProperties().put("sprite", "lever_base_green");
		
		RectangleMapObject lever = new RectangleMapObject();
		lever.getRectangle().set(x, y, width, height);
		lever.setName("Switch");
		lever.getProperties().put("sprite", "lever");
		lever.getProperties().put("still", true);
		lever.getProperties().put("frame", 0);
		lever.getProperties().put("particle_std", "MOMENTUM");
		lever.getProperties().put("triggeredId", leverId);
		lever.getProperties().put("triggeringId", multiId);
		
		RectangleMapObject use = new RectangleMapObject();
		use.setName("Multitrigger");
		use.getProperties().put("triggeredId", multiId);
		use.getProperties().put("triggeringId", triggeringId+ ","+ spriteId1 + "," + alt2Id);
		
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
		sprite2.getProperties().put("triggeredId", spriteId2);
		sprite2.getProperties().put("triggeringId", leverId);
		
		RectangleMapObject timer = new RectangleMapObject();
		timer.setName("Timer");
		timer.getProperties().put("interval", 0.8f);
		timer.getProperties().put("triggeredId", timerId);
		timer.getProperties().put("triggeringId", resetId);

		RectangleMapObject reset = new RectangleMapObject();
		reset.setName("Multitrigger");
		reset.getProperties().put("triggeredId", resetId);
		reset.getProperties().put("triggeringId", alt1Id+ ","+ spriteId2);
		
		RectangleMapObject alt1 = new RectangleMapObject();
		alt1.setName("Alttrigger");
		alt1.getProperties().put("message", "off");
		alt1.getProperties().put("triggeredId", alt1Id);
		alt1.getProperties().put("triggeringId", timerId);
		
		RectangleMapObject alt2 = new RectangleMapObject();
		alt2.setName("Alttrigger");
		alt2.getProperties().put("message", "on");
		alt2.getProperties().put("triggeredId", alt2Id);
		alt2.getProperties().put("triggeringId", timerId);
		
		TiledObjectUtil.parseTiledEvent(state, lever);
		TiledObjectUtil.parseTiledEvent(state, base);
		TiledObjectUtil.parseTiledEvent(state, use);
		TiledObjectUtil.parseTiledEvent(state, sprite1);
		TiledObjectUtil.parseTiledEvent(state, sprite2);
		TiledObjectUtil.parseTiledEvent(state, timer);
		TiledObjectUtil.parseTiledEvent(state, reset);
		TiledObjectUtil.parseTiledEvent(state, alt1);
		TiledObjectUtil.parseTiledEvent(state, alt2);

	}
}
