package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.FlagHoldable;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 */
public class SpawnerObjective extends Event {

	//How frequently will this event trigger its connected event?
	private static final float interval = 2.0f;

	//These keep track of how long until this triggers its connected event and how many times it can trigger again.
	private float timeCount = 0;

	public SpawnerObjective(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);

		this.body = BodyBuilder.createBox(world, startPos, size, 0, 1, 0, true, true, Constants.BIT_SENSOR, (short) (0), (short) 0, true, eventData);
		body.setType(BodyType.KinematicBody);

		if (state.getMode().equals(GameMode.EGGPLANTS)) {
			state.getUiObjective().addObjective(this, Sprite.CLEAR_CIRCLE_EGGPLANT, true, true);
			if (state.isServer()) {
				HadalGame.server.sendToAllTCP(new Packets.SyncObjectiveMarker(getEntityID().toString(),
						new Vector3(), true, false, Sprite.CLEAR_CIRCLE_EGGPLANT));
			}
		}
	}

	private Hitbox flag;
	private float spawnCountdown;
	private static final float spawnDelay = 2.0f;
	@Override
	public void controller(float delta) {
		if (state.getMode().equals(GameMode.EGGPLANTS)) {
			timeCount += delta;
			if (timeCount >= interval) {
				timeCount = 0;
				WeaponUtils.spawnScrap(state, 1, getPixelPosition(), false);
			}
		}

		if (state.getMode().equals(GameMode.KINGMAKER)) {
			if (spawnCountdown > 0.0f) {
				spawnCountdown -= delta;
				if (spawnCountdown <= 0.0f) {
					spawnFlag();
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

					if (getStandardParticle() != null) {
						getStandardParticle().onForBurst(spawnDelay);
					}
				}
			}
		}
	}

	private static final Vector2 flagSize = new Vector2(80, 80);
	private static final float flagLifespan = 240.0f;
	private void spawnFlag() {
		flag = new RangedHitbox(state, new Vector2(getPixelPosition()), flagSize, flagLifespan, new Vector2(),
				(short) 0, false, false, state.getWorldDummy(), Sprite.DIATOM_D);

		flag.addStrategy(new ControllerDefault(state, flag, state.getWorldDummy().getBodyData()));
		flag.addStrategy(new FlagHoldable(state, flag, state.getWorldDummy().getBodyData()));
		flag.addStrategy(new CreateParticles(state, flag, state.getWorldDummy().getBodyData(), Particle.BRIGHT_TRAIL, 0.0f, 1.0f));

		state.getUiObjective().addObjective(flag, Sprite.CLEAR_CIRCLE_ALERT, true, false);

		if (state.isServer()) {
			HadalGame.server.sendToAllTCP(new Packets.SyncObjectiveMarker(flag.getEntityID().toString(),
					new Vector3(), true, false, Sprite.CLEAR_CIRCLE_ALERT));
		}
	}
}
