package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

public class Door extends Prefabrication {

	private float speed;
	private String eventId;
	private int xDisplace, yDisplace;
	
	public Door(PlayState state, int width, int height, int x, int y, String eventId, float speed, int xDisplace, int yDisplace) {
		super(state, width, height, x , y);
		this.speed = speed;
		this.eventId = eventId;
		this.xDisplace = xDisplace;
		this.yDisplace = yDisplace;
	}
	
	@Override
	public void generateParts() {
		
		String doorId = TiledObjectUtil.getPrefabTriggerId();
		String hingeId = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject redirector = new RectangleMapObject();
		redirector.setName("Redirecttrigger");
		redirector.getProperties().put("blameId", hingeId);
		redirector.getProperties().put("triggeredId", eventId);
		redirector.getProperties().put("triggeringId", doorId);
		
		RectangleMapObject hinge = new RectangleMapObject();
		hinge.getRectangle().set(x + width / 2 - 16 - xDisplace * 32, y + height / 2 - 16 - yDisplace * 32, 32, 32);
		hinge.setName("Dummy");
		hinge.getProperties().put("triggeredId", hingeId);
		
		RectangleMapObject door = new RectangleMapObject();
		door.getRectangle().set(x, y, width, height);
		door.setName("Platform");
		door.getProperties().put("speed", speed);
		door.getProperties().put("triggeredId", doorId);
		
		TiledObjectUtil.parseTiledEvent(state, redirector);
		TiledObjectUtil.parseTiledEvent(state, hinge);
		TiledObjectUtil.parseTiledEvent(state, door);
	}
}
