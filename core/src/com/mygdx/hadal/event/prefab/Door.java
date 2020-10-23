package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The Door is a wall that, when triggered, will move to a specified point.
 * @author Flokchoy Froldwin
 */
public class Door extends Prefabrication {

	//This is the speed that the wall will open at.
	private final float speed;
	
	//This is the triggering Id of the event that causes the door to open.
	private final String eventId;
	
	//The wall will move this amount in the x and y directions.
	private final int xDisplace, yDisplace;
	
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
		String moverId = TiledObjectUtil.getPrefabTriggerId();

		RectangleMapObject redirector = new RectangleMapObject();
		redirector.setName("Redirecttrigger");
		redirector.getProperties().put("blameId", hingeId);
		redirector.getProperties().put("triggeredId", eventId);
		redirector.getProperties().put("triggeringId", moverId);
		
		RectangleMapObject hinge = new RectangleMapObject();
		hinge.getRectangle().set(x + width / 2.0f - 16 - xDisplace * 32, y + height / 2.0f - 16 - yDisplace * 32, 32, 32);
		hinge.setName("Dummy");
		hinge.getProperties().put("triggeredId", hingeId);
		
		RectangleMapObject door = new RectangleMapObject();
		door.getRectangle().set(x, y, width, height);
		door.setName("Platform");
		door.getProperties().put("triggeredId", doorId);
		door.getProperties().put("synced", true);
		
		RectangleMapObject mover = new RectangleMapObject();
		mover.getRectangle().set(x, y, width, height);
		mover.setName("MovePoint");
		mover.getProperties().put("speed", speed);
		mover.getProperties().put("connections", doorId);
		mover.getProperties().put("triggeredId", moverId);
		
		TiledObjectUtil.parseTiledEvent(state, redirector);
		TiledObjectUtil.parseTiledEvent(state, hinge);
		TiledObjectUtil.parseTiledEvent(state, door);
		TiledObjectUtil.parseTiledEvent(state, mover);
	}
}
