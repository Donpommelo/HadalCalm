package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataType;
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
 * teamIndex: Int index of team that should be unable to pass through this platform. (if 1-, affects all teams) Default: -1
 * @author Brarpslinger Briberdash
 */
public class Platform extends Event {

	private final short filter;
	private final int teamIndex;
	private final float restitution;

	private boolean loaded;

	public Platform(PlayState state, Vector2 startPos, Vector2 size, float restitution,
			boolean wall, boolean player, boolean hbox, boolean event, boolean enemy, int teamIndex) {
		super(state, startPos ,size);
		this.filter = (short) ((wall ? Constants.BIT_WALL : 0) | (player ? Constants.BIT_PLAYER : 0) | (hbox ? Constants.BIT_PROJECTILE: 0) | (event ? Constants.BIT_SENSOR : 0) | (enemy ? Constants.BIT_ENEMY : 0));
		this.restitution = restitution;
		this.teamIndex = teamIndex;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataType.WALL);
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 1, restitution, false,
			true, Constants.BIT_WALL, filter, (short) 0, false, eventData);
		this.body.setType(BodyDef.BodyType.KinematicBody);
	}

	@Override
	public void controller(float delta) {
		super.controller(delta);

		//we need to set team alignments here, because the client doesn't know team alignments upon loading
		if (!loaded && teamIndex != -1.0f) {
			short teamFilter;
			if (teamIndex < AlignmentFilter.currentTeams.length) {
				loaded = true;
				teamFilter = AlignmentFilter.currentTeams[teamIndex].getFilter();

				Filter filter = getMainFixture().getFilterData();
				filter.groupIndex = teamFilter;
				getMainFixture().setFilterData(filter);
			}
		}
	}

	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		controller(delta);
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_RELOAD_BAR);
		setScaleAlign("CENTER_STRETCH");
		setSyncType(eventSyncTypes.ALL);
	}
}
