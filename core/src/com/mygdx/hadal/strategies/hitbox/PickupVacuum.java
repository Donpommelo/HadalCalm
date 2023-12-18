package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 *
 */
public class PickupVacuum extends HitboxStrategy {

	private static final float VACUUM_SPEED_MIN = 15.0f;
	private static final float VACUUM_SPEED_MAX = 50.0f;
	private static final float VACUUM_SPEED_INTERVAL = 0.75f;
	private static final float VACUUM_DELAY = 0.3f;
	private static final float VACUUM_PUSH = 15.0f;
	private static final float VACUUM_PICKUP_RANGE = 5.0f;

	private boolean vacuumStarted, vacuuming;
	private float vacuumDelay, vacuumDuration;
	private HadalEntity vacuumTarget;

	public PickupVacuum(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}

	@Override
	public void create() {
		hbox.setPassability((short) (BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_SENSOR));
	}

	private final Vector2 entityLocation = new Vector2();
	private final Vector2 homeLocation = new Vector2();
	private final Vector2 homeVelocity = new Vector2();
	@Override
	public void controller(float delta) {
		if (vacuumStarted) {

			getVacuumed();

			vacuumDelay = VACUUM_DELAY;
			vacuumDuration = 0.0f;
			entityLocation.set(hbox.getPosition());
			homeLocation.set(vacuumTarget.getPosition());
			homeVelocity.set(entityLocation).sub(homeLocation).nor().scl(VACUUM_PUSH);
			hbox.applyLinearImpulse(homeVelocity);
		}
		if (vacuuming) {
			if (null != vacuumTarget) {
				if (0.0f <= vacuumDelay) {
					vacuumDelay -= delta;
				} else {
					if (vacuumDuration < VACUUM_SPEED_INTERVAL) {
						vacuumDuration += delta;
					}
					entityLocation.set(hbox.getPosition());
					homeLocation.set(vacuumTarget.getPosition());
					homeVelocity.set(homeLocation).sub(entityLocation).nor()
							.scl((vacuumDuration / VACUUM_SPEED_INTERVAL) * (VACUUM_SPEED_MAX - VACUUM_SPEED_MIN) + VACUUM_SPEED_MIN);
					hbox.setLinearVelocity(homeVelocity);

					if (entityLocation.dst2(homeLocation) < VACUUM_PICKUP_RANGE) {
						hbox.onPickup(vacuumTarget.getHadalData());
					}
				}

				if (!vacuumTarget.isAlive()) {
					getUnvacuumed();
				}
			}
		}
	}

	@Override
	public void onHit(HadalData fixB, Body body) {
		if (fixB != null) {
			if (!vacuuming && !vacuumStarted) {
				if (UserDataType.PICKUP_RADIUS.equals(fixB.getType())) {
					if (fixB.getEntity() instanceof Player player) {
						vacuumTarget = player;
						vacuumStarted = true;
					}
				}
			}
		}
	}

	public void startVacuum(HadalEntity vacuumTarget) {
		this.vacuumTarget = vacuumTarget;
		vacuumStarted = true;
	}

	private void getVacuumed() {
		vacuuming = true;
		vacuumStarted = false;
		hbox.setLifeSpan(hbox.getMaxLifespan());

		if (null != hbox.getWallCollider()) {
			hbox.getWallCollider().setSensor(true);
		}
		if (null != hbox.getDropthroughCollider()) {
			hbox.getDropthroughCollider().setSensor(true);
		}

		if (null != hbox.getBody()) {
			hbox.getBody().setLinearDamping(5.0f);
			hbox.getBody().setGravityScale(0.0f);
		}
	}

	private void getUnvacuumed() {
		vacuuming = false;
		vacuumStarted = false;
		vacuumTarget = null;
		hbox.setLifeSpan(hbox.getMaxLifespan());

		if (null != hbox.getWallCollider()) {
			hbox.getWallCollider().setSensor(false);
		}
		if (null != hbox.getDropthroughCollider()) {
			hbox.getDropthroughCollider().setSensor(false);
		}

		if (null != hbox.getBody()) {
			hbox.getBody().setLinearDamping(0.0f);
			hbox.getBody().setGravityScale(hbox.getGravity());
		}
	}
}
