package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.battle.PickupUtils;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A scrap spawner spawns scrap when activated.
 * 
 * Triggered Behavior: When triggered, spawn scrap
 * Triggering Behavior: N/A
 * 
 * Fields:
 * scrap: The amount of scrap to spawn. Default: 0
 * @author Punga Petherford
 */
public class SpawnerScrap extends Event {

	private final int scrap;
	
	public SpawnerScrap(PlayState state, Vector2 startPos, Vector2 size, int scrap) {
		super(state, startPos, size);
		this.scrap = scrap;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				PickupUtils.spawnScrap(state, state.getWorldDummy(), event.getPixelPosition(), new Vector2(0, 1),
						scrap, false, true);
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.addToWorld(world);
	}
}
