package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes hboxes rotate by setting angular velocity upon creating.
 */
public class RotationConstant extends HitboxStrategy {

	private final float rotationSpeed;

	public RotationConstant(PlayState state, Hitbox proj, BodyData user, float rotationSpeed) {
		super(state, proj, user);
		this.rotationSpeed = rotationSpeed;
	}

	@Override
	public void create() {

		//flip rotation so the top of the hbox is moving forwards
		if (hbox.getStartVelo().x < 0.0f) {
			hbox.setAngularVelocity(rotationSpeed);
		} else {
			hbox.setAngularVelocity(-rotationSpeed);
		}
	}
}
