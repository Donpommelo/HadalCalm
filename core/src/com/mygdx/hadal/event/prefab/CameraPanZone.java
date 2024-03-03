package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The CameraPanZone is an event that is drawn between two areas with different camera properties.
 * Think of is at a horizontal or vertical wall that, when passed, makes the camera (un)lock and/or pan. 
 * @author Bengermine Barsnip
 */
public class CameraPanZone extends Prefabrication {

	//Zoom percent of the left/lower, right/upper sections
	private final float zoom1, zoom2;
	
	//0 = Vertical  wall. 1 = Horizontal wall
	private final int align;
	
	//These are the eventIDs of the camera lock points for the left/lower, right/upper sections
	private final String point1, point2;
	
	public CameraPanZone(PlayState state, float width, float height, float x, float y, float zoom1, float zoom2,
						 int align, String point1, String point2) {
		super(state, width, height, x , y);
		this.zoom1 = zoom1;
		this.zoom2 = zoom2;
		this.align = align;
		this.point1 = point1;
		this.point2 = point2;
	}
	
	@Override
	public void generateParts() {
		String sensor1Id = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String sensor2Id = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String set1Id = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String set2Id = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String camera1Id = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String camera2Id = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String zoom1Id = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		String zoom2Id = TiledObjectUtil.getPrefabTriggerIdUnsynced();
		
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

		switch (align) {
			case 0 -> {
				sensor1.getRectangle().set(x, y, 8, height);
				sensor2.getRectangle().set(x + width - 8, y, 8, height);
			}
			case 1 -> {
				sensor1.getRectangle().set(x, y, width, 8);
				sensor2.getRectangle().set(x, y + height - 8, width, 8);
			}
		}
		
		if (!"".equals(point1)) {
			camera1.getProperties().put("triggeringId", point1);
		}
		if (!"".equals(point2)) {
			camera2.getProperties().put("triggeringId", point2);
		}
		
		TiledObjectUtil.parseAddTiledEvent(state, sensor1);
		TiledObjectUtil.parseAddTiledEvent(state, sensor2);
		TiledObjectUtil.parseAddTiledEvent(state, cond1);
		TiledObjectUtil.parseAddTiledEvent(state, cond2);
		TiledObjectUtil.parseAddTiledEvent(state, alt1);
		TiledObjectUtil.parseAddTiledEvent(state, alt2);
		TiledObjectUtil.parseAddTiledEvent(state, mult1);
		TiledObjectUtil.parseAddTiledEvent(state, mult2);
		TiledObjectUtil.parseAddTiledEvent(state, camera1);
		TiledObjectUtil.parseAddTiledEvent(state, camera2);
	}
}
