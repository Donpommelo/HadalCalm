package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Semipermeable wall is a wall that some objects can pass through
 * 
 * Triggered Behavior: N/A.
 * Triggering Behavior: N/A.
 * 
 * Fields:
 * restitution: float value of platform bounciness. Default: 0.0f
 * 
 * wall: Boolean that describes whether walls can pass through this. Default: true
 * player: Boolean that describes whether players can pass through this. Default: true
 * hbox: Boolean that describes whether hitboxes can pass through this. Optional. Default: true
 * event: Boolean that describes whether this sensor touches events. Optional. Default: true
 * enemy: Boolean that describes whether this sensor touches enemies. Optional. Default: true
 * @author Brarpslinger Briberdash
 */
public class Platform extends Event {

	private final short filter;
	private final int teamIndex;
	private final float restitution;
	
	public Platform(PlayState state, Vector2 startPos, Vector2 size, float restitution,
			boolean wall, boolean player, boolean hbox, boolean event, boolean enemy, int teamIndex) {
		super(state, startPos ,size);
		this.filter = (short) ((wall ? Constants.BIT_WALL : 0) | (player ? Constants.BIT_PLAYER : 0) | (hbox ? Constants.BIT_PROJECTILE: 0) | (event ? Constants.BIT_SENSOR : 0) | (enemy ? Constants.BIT_ENEMY : 0));
		this.restitution = restitution;
		this.teamIndex = teamIndex;
	}
	
	@Override
	public void create() {
		short teamFilter;
		if (teamIndex != -1 && teamIndex < AlignmentFilter.currentTeams.length) {
			teamFilter = AlignmentFilter.currentTeams[teamIndex].getFilter();
		} else {
			teamFilter = 0;
		}

		this.eventData = new EventData(this, UserDataTypes.WALL);
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 1, restitution, false,
			true, Constants.BIT_WALL, filter, teamFilter, false, eventData);
		this.body.setType(BodyDef.BodyType.KinematicBody);
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_RELOAD_BAR);
		setScaleAlign("CENTER_STRETCH");
		setSyncType(eventSyncTypes.ALL);
	}
}
