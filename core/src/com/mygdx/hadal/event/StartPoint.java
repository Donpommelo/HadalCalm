package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A StartPoint is a place that the player can start at when spawning into a map.
 * <p>
 * Triggered Behavior: N/A.
 * Triggering Behavior: This event will be triggered when the player spawns into the map.
 * <p>
 * Fields:
 * startId: String id of the start point. Many maps have multiple start points, so these are used to determine which one the player is entering from
 * 
 * @author Xardamom Xeryl
 */
public class StartPoint extends Event {

	//the timer manages the time until this spawn point will be used again
	private static final float SPAWN_TIMER = 2.0f;

	private final String startId;
	private final int teamIndex;
	private float spawnCd;
	
	public StartPoint(PlayState state, Vector2 startPos, Vector2 size, String startId, int teamIndex) {
		super(state, startPos, size);
		this.startId = startId;
		this.teamIndex = teamIndex;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.addToWorld(world);
	}
	
	@Override
	public void controller(float delta) {
		if (spawnCd > 0) {
			spawnCd -= delta;
		}
	}

	/**
	 * This is run when a start point is selected which occurs slightly before spawning
	 */
	public void startPointSelected() {
		spawnCd = SPAWN_TIMER;
	}
	
	public boolean isReady() { return spawnCd <= 0.0f; }
	
	public String getStartId() { return startId; }

	public int getTeamIndex() { return teamIndex; }
}
