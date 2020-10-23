package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * Event Alternators function like camera pan zones, except can connect to any events, instead of just cameras.
 * This prefab sets up 2 parallel sensors that connect to events with the input eventIds.
 * Each sensor, when touched, set the other sensor to activate the respective event.
 * @author Brorbland Brirlov
 */
public class EventAlternatorZone extends Prefabrication {

	//0 = Vertical  wall. 1 = Horizontal wall
	private final int align;
	
	private final String event1, event2;
	
	public EventAlternatorZone(PlayState state, int width, int height, int x, int y, int align, String event1, String event2) {
		super(state, width, height, x , y);
		this.align = align;
		this.event1 = event1;
		this.event2 = event2;
	}
	
	@Override
	public void generateParts() {
		
		String sensor1Id = TiledObjectUtil.getPrefabTriggerId();
		String sensor2Id = TiledObjectUtil.getPrefabTriggerId();
		String set1Id = TiledObjectUtil.getPrefabTriggerId();
		String set2Id = TiledObjectUtil.getPrefabTriggerId();
		String event1Id = TiledObjectUtil.getPrefabTriggerId();
		String event2Id = TiledObjectUtil.getPrefabTriggerId();
		
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
		cond1.getProperties().put("triggeringId", set1Id + "," + event1Id);
		
		RectangleMapObject cond2 = new RectangleMapObject();
		cond2.setName("Condtrigger");
		cond2.getProperties().put("start", set2Id);
		cond2.getProperties().put("triggeredId", sensor2Id);
		cond2.getProperties().put("triggeringId", set2Id + "," + event2Id);
		
		RectangleMapObject alt1 = new RectangleMapObject();
		alt1.setName("Alttrigger");
		alt1.getProperties().put("message", event2Id);
		alt1.getProperties().put("triggeredId", set1Id);
		alt1.getProperties().put("triggeringId", sensor2Id);
		
		RectangleMapObject alt2 = new RectangleMapObject();
		alt2.setName("Alttrigger");
		alt2.getProperties().put("message", event1Id);
		alt2.getProperties().put("triggeredId", set2Id);
		alt2.getProperties().put("triggeringId", sensor1Id);
		
		RectangleMapObject mult1 = new RectangleMapObject();
		mult1.setName("Multitrigger");
		mult1.getProperties().put("triggeredId", event1Id);
		mult1.getProperties().put("triggeringId", event1 + "," + set1Id);
		
		RectangleMapObject mult2 = new RectangleMapObject();
		mult2.setName("Multitrigger");
		mult2.getProperties().put("triggeredId", event2Id);
		mult2.getProperties().put("triggeringId", event2 + "," + set2Id);
		


		switch(align) {
		case 0:
			sensor1.getRectangle().set(x, y, 16, height);
			sensor2.getRectangle().set(x + width - 16, y, 16, height);
			break;
		case 1:
			sensor1.getRectangle().set(x, y, width, 16);
			sensor2.getRectangle().set(x, y + height - 16, width, 16);
			break;
		}
		
		TiledObjectUtil.parseTiledEvent(state, sensor1);
		TiledObjectUtil.parseTiledEvent(state, sensor2);
		TiledObjectUtil.parseTiledEvent(state, cond1);
		TiledObjectUtil.parseTiledEvent(state, cond2);
		TiledObjectUtil.parseTiledEvent(state, alt1);
		TiledObjectUtil.parseTiledEvent(state, alt2);
		TiledObjectUtil.parseTiledEvent(state, mult1);
		TiledObjectUtil.parseTiledEvent(state, mult2);
	}
}
