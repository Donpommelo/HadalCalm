package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.battle.PickupUtils;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * This is a multi-use event that performs a variety of functions for special modes that require a central objective.
 * In these modes (eggplant hunt, kingmaker), this event serves as the objective.
 * Due to there only being a few modes, this event simply contains the logic for each mode.
 *
 *  @author Kiwin Krinoceros
 */
public class SpawnerObjective extends Event {

	//How frequently will this event spawn eggplants/candy in eggplant hunt/trick or treat?
	private static final float EGGPLANT_INTERVAL = 4.0f;
	private static final int EGGPLANT_AMOUNT = 3;

	private static final float CANDY_INTERVAL = 10.0f;

	private final static float PARTICLE_DURATION = 5.0f;

	//These keep track of how long until this triggers its connected event and how many times it can trigger again.
	private float timeCount;

	public SpawnerObjective(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
	}

	@Override
	public Object onServerCreate(boolean catchup) {
		return super.onServerCreate(catchup);
	}

	@Override
	public void create() {
		this.eventData = new EventData(this);

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.addToWorld(world);

		//in eggplant mode, this event should be visible in the objective ui
		if (GameMode.EGGPLANTS.equals(state.getMode())) {
			state.getUIManager().getUiObjective().addObjective(this, Sprite.NASU,true, true, true);
		}

		if (GameMode.TRICK_OR_TREAT.equals(state.getMode())) {
			state.getUIManager().getUiObjective().addObjective(this, Sprite.NOTIFICATIONS_GHOST,true, false, true);
			setScaleAlign(ClientIllusion.alignType.CENTER);
			setEventSprite(Sprite.CANDY_GHOST);
		}
	}

	private static final float SPAWN_DELAY = 2.0f;
	private CrownHoldable flag;
	private float spawnCountdown;
	@Override
	public void controller(float delta) {

		//in eggplant mode, spawn scrap periodically
		if (GameMode.EGGPLANTS.equals(state.getMode())) {
			timeCount += delta;
			if (timeCount >= EGGPLANT_INTERVAL) {
				timeCount = 0;

				PickupUtils.spawnScrap(state, state.getWorldDummy(), getPixelPosition(), new Vector2(0, 1),
						EGGPLANT_AMOUNT, false, true);
			}
		}

		if (GameMode.TRICK_OR_TREAT.equals(state.getMode())) {
			timeCount += delta;
			if (timeCount >= CANDY_INTERVAL) {
				timeCount = 0;

				PickupUtils.spawnCandy(state, state.getWorldDummy(), getPixelPosition(), new Vector2(0, 1),1);
			}
		}

		//in kingmaker, spawn a flag after some time if there is not one active or after a delay after it has been dropped.
		if (GameMode.KINGMAKER.equals(state.getMode())) {
			if (spawnCountdown > 0.0f) {
				spawnCountdown -= delta;
				if (spawnCountdown <= 0.0f) {
					flag = new CrownHoldable(state, new Vector2(getPixelPosition()));
					EffectEntityManager.getParticle(state, new ParticleCreate(Particle.DIATOM_IMPACT_LARGE, this)
							.setLifespan(PARTICLE_DURATION)
							.setSyncType(SyncType.CREATESYNC)
							.setColor(HadalColor.GOLDEN_YELLOW));
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
					spawnCountdown = SPAWN_DELAY;
				}
			}
		}
	}

	@Override
	public void loadDefaultProperties() {
		setServerSyncType(eventSyncTypes.USER);
		setClientSyncType(eventSyncTypes.USER);
	}
}
