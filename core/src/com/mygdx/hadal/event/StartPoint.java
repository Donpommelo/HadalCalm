package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A StartPoint is a place that the player can start at when spawning into a map.
 * 
 * Triggered Behavior: N/A.
 * Triggering Behavior: This event will be triggered when the player spawns into the map.
 * 
 * Fields:
 * startId: String id of the start point. Many maps have multiple start points, so these are used to determine which one the player is entering from
 * 
 * @author Zachary Tu
 */
public class StartPoint extends Event {

	private String startId;
	
	//the timer manages the time until this spawn point will be used again
	private final static float SpawnTimer = 2.0f;
	private float spawnCd;
	
	public StartPoint(PlayState state, Vector2 startPos, Vector2 size, String startId) {
		super(state, startPos, size);
		this.startId = startId;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 1, 0, true, true, Constants.BIT_SENSOR, (short) (0), (short) 0, true, eventData);
		body.setType(BodyType.KinematicBody);
	}
	
	@Override
	public void controller(float delta) {
		if (spawnCd > 0) {
			spawnCd -= delta;
		}
	}
	
	/**
	 * This is run when the player is created to run connected events.
	 */
	public void playerStart(final Player p) {
		if (getConnectedEvent() != null) {
			getConnectedEvent().getEventData().preActivate(eventData, p);
		}
		spawnCd = SpawnTimer;
	}
	
	public boolean isReady() { return spawnCd <= 0.0f; }
	
	public String getStartId() { return startId; }
}
