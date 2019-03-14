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

		RectangleMapObject base = new RectangleMapObject();
		base.getRectangle().set(x, y, width, height);
		base.setName("Dummy");
		base.getProperties().put("align", 2);
		base.getProperties().put("sync", 2);
		base.getProperties().put("sprite", "BASE_GREEN");
		
		RectangleMapObject lever = new RectangleMapObject();
		lever.getRectangle().set(x, y, width, height);
		lever.setName("Switch");
		lever.getProperties().put("sprite", "LEVER");
		lever.getProperties().put("still", true);
		lever.getProperties().put("frame", 0);
		lever.getProperties().put("align", 2);
		lever.getProperties().put("sync", 2);
		lever.getProperties().put("particle_std", "MOMENTUM");
		lever.getProperties().put("triggeredId", leverId);
		lever.getProperties().put("triggeringId", multiId);
		
		RectangleMapObject use = new RectangleMapObject();
		use.setName("Multitrigger");
		use.getProperties().put("sync", 2);
		use.getProperties().put("triggeredId", multiId);
		use.getProperties().put("triggeringId", triggeringId+ ","+ spriteId1);
		
		RectangleMapObject sprite1 = new RectangleMapObject();
		sprite1.setName("SpriteChange");
		sprite1.getProperties().put("newSprite", "LEVER");
		sprite1.getProperties().put("still", false);
		sprite1.getProperties().put("mode", "NORMAL");
		sprite1.getProperties().put("speed", 0.02f);
		sprite1.getProperties().put("align", 2);
		sprite1.getProperties().put("sync", 2);
		sprite1.getProperties().put("triggeredId", spriteId1);
		sprite1.getProperties().put("triggeringId", leverId);
		
		TiledObjectUtil.parseTiledEvent(state, lever);
		TiledObjectUtil.parseTiledEvent(state, base);
		TiledObjectUtil.parseTiledEvent(state, use);
		TiledObjectUtil.parseTiledEvent(state, sprite1);
	}
}
