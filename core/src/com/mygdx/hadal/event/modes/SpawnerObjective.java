package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.EventUtils;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This is a multi-use event that performs a variety of functions for special modes that require a central objective.
 * In these modes (eggplant hunt, kingmaker), this event serves as the objective.
 * Due to there only being a few modes, this event simply contains the logic for each mode.
 *
 *  @author Kiwin Krinoceros
 */
public class SpawnerObjective extends Event {

	//How frequently will this event spawn eggplants in eggplant hunt?
	private static final float interval = 2.0f;

	private final static float particleDuration = 5.0f;

	//These keep track of how long until this triggers its connected event and how many times it can trigger again.
	private float timeCount;

	public SpawnerObjective(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);

		this.body = BodyBuilder.createBox(world, startPos, size, 0, 1, 0, true, true,
				Constants.BIT_SENSOR, (short) (0), (short) 0, true, eventData);
		body.setType(BodyType.KinematicBody);

		//in eggplant mode, this event should be visible in the objective ui
		if (GameMode.EGGPLANTS.equals(state.getMode())) {
			EventUtils.setObjectiveMarker(state, this, Sprite.CLEAR_CIRCLE_EGGPLANT, HadalColor.NOTHING,
					true, true);
		}
	}

	private CrownHoldable flag;
	private float spawnCountdown;
	private static final float spawnDelay = 2.0f;
	@Override
	public void controller(float delta) {

		//in eggplant mode, spawn scrap periodically
		if (GameMode.EGGPLANTS.equals(state.getMode())) {
			timeCount += delta;
			if (timeCount >= interval) {
				timeCount = 0;
				WeaponUtils.spawnScrap(state, 1, getPixelPosition(), false, true);
			}
		}

		//in kingmaker, spawn a flag after some time if there is not one active or after a delay after it has been dropped.
		if (GameMode.KINGMAKER.equals(state.getMode())) {
			if (spawnCountdown > 0.0f) {
				spawnCountdown -= delta;
				if (spawnCountdown <= 0.0f) {
					flag = new CrownHoldable(state, new Vector2(getPixelPosition()));
					new ParticleEntity(state, this, Particle.DIATOM_IMPACT_LARGE, 0, particleDuration,
							true, SyncType.CREATESYNC).setColor(HadalColor.GOLDEN_YELLOW);
				}
			} else {

				//spawn a flag if it is dead or nonexistent
				boolean flagded = false;
				if (flag == null) {
					flagded = true;
				} else if (!flag.isAlive()) {
					flagded = true;
				}

				if (flagded) {
					spawnCountdown = spawnDelay;
				}
			}
		}
	}
}
