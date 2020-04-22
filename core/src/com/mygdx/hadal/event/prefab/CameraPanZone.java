package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The CameraPanZone is an event that is drawn between two areas with different camera properties.
 * Think of is at a horizontal or vertical wall that, when passed, makes the camera (un)lock and/or pan. 
 * @author Zachary Tu
 *
 */
public class CameraPanZone extends Prefabrication {

	//Zoom percent of the left/lower, right/upper sections
	private float zoom1, zoom2;
	
	//0 = Vertical  wall. 1 = Horizontal wall
	private int align;
	
	//These are the eventIDs of the camera lock points for the left/lower, right/upper sections
	private String point1, point2;
	
	public CameraPanZone(PlayState state, int width, int height, int x, int y, float zoom1, float zoom2, int align, String point1, String point2) {
		super(state, width, height, x , y);
		this.zoom1 = zoom1;
		this.zoom2 = zoom2;
		this.align = align;
		this.point1 = point1;
		this.point2 = point2;
	}
	
	@Override
	public void generateParts() {
		
		String sensor1Id = TiledObjectUtil.getPrefabTriggerId();
		String sensor2Id = TiledObjectUtil.getPrefabTriggerId();
		String set1Id = TiledObjectUtil.getPrefabTriggerId();
		String set2Id = TiledObjectUtil.getPrefabTriggerId();
		String camera1Id = TiledObjectUtil.getPrefabTriggerId();
		String camera2Id = TiledObjectUtil.getPrefabTriggerId();
		String zoom1Id = TiledObjectUtil.getPrefabTriggerId();
		String zoom2Id = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject sensor1 = new RectangleMapObject();
		sensor1.setName("Sensor");
		sensor1.getProperties().put("triggeringId", sensor1Id);
		
		RectangleMapObject sensor2 = new RectangleMapObject();
		sensor2.setName("Sensor");
		sensor2.getProperties().put("triggeringId", sensor2Id);
		
		RectangleMapObject cond1 = new RectangleMapObject();
		cond1.setName("Condtrigger");
		cond1.getProperties().put("start", set1Id);
		cond1.getProperties().put("triggeredId", sensor1Id);
		cond1.getProperties().put("triggeringId", set1Id + "," + camera1Id);
		
		RectangleMapObject cond2 = new RectangleMapObject();
		cond2.setName("Condtrigger");
		cond2.getProperties().put("start", set2Id);
		cond2.getProperties().put("triggeredId", sensor2Id);
		cond2.getProperties().put("triggeringId", set2Id + "," + camera2Id);
		
		RectangleMapObject alt1 = new RectangleMapObject();
		alt1.setName("Alttrigger");
		alt1.getProperties().put("message", camera2Id);
		alt1.getProperties().put("triggeredId", set1Id);
		alt1.getProperties().put("triggeringId", sensor2Id);
		
		RectangleMapObject alt2 = new RectangleMapObject();
		alt2.setName("Alttrigger");
		alt2.getProperties().put("message", camera1Id);
		alt2.getProperties().put("triggeredId", set2Id);
		alt2.getProperties().put("triggeringId", sensor1Id);
		
		RectangleMapObject mult1 = new RectangleMapObject();
		mult1.setName("Multitrigger");
		mult1.getProperties().put("triggeredId", camera1Id);
		mult1.getProperties().put("triggeringId", zoom1Id + "," + set1Id);
		
		RectangleMapObject mult2 = new RectangleMapObject();
		mult2.setName("Multitrigger");
		mult2.getProperties().put("triggeredId", camera2Id);
		mult2.getProperties().put("triggeringId", zoom2Id + "," + set2Id);
		
		RectangleMapObject camera1 = new RectangleMapObject();
		camera1.setName("Camera");
		camera1.getProperties().put("zoom", zoom1);
		camera1.getProperties().put("triggeredId", zoom1Id);
		
		RectangleMapObject camera2 = new RectangleMapObject();
		camera2.setName("Camera");
		camera2.getProperties().put("zoom", zoom2);
		camera2.getProperties().put("triggeredId", zoom2Id);

		switch(align) {
		case 0:
			sensor1.getRectangle().set(x, y, 8, height);
			sensor2.getRectangle().set(x + width - 8, y, 8, height);
			break;
		case 1:
			sensor1.getRectangle().set(x, y, width, 8);
			sensor2.getRectangle().set(x, y + height - 8, width, 8);
			break;
		}
		
		if (!point1.equals("")) {
			camera1.getProperties().put("triggeringId", point1);
		}
		
		if (!point2.equals("")) {
			camera2.getProperties().put("triggeringId", point2);
		}
		
		TiledObjectUtil.parseTiledEvent(state, sensor1);
		TiledObjectUtil.parseTiledEvent(state, sensor2);
		TiledObjectUtil.parseTiledEvent(state, cond1);
		TiledObjectUtil.parseTiledEvent(state, cond2);
		TiledObjectUtil.parseTiledEvent(state, alt1);
		TiledObjectUtil.parseTiledEvent(state, alt2);
		TiledObjectUtil.parseTiledEvent(state, mult1);
		TiledObjectUtil.parseTiledEvent(state, mult2);
		TiledObjectUtil.parseTiledEvent(state, camera1);
		TiledObjectUtil.parseTiledEvent(state, camera2);
	}
}
