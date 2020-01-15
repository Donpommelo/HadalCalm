package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This is the default strategy that most hboxes have
 * @author Zachary Tu
 *
 */
public class AdjustAngle extends HitboxStrategy{
	
	public AdjustAngle(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}

	@Override
	public void controller(float delta) {
		hbox.setTransform(hbox.getPosition(), (float)(Math.atan2(hbox.getLinearVelocity().y , hbox.getLinearVelocity().x)));
	}
}
