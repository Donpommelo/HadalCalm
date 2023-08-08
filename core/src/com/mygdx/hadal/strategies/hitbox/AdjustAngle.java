package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes hboxes adjust their angle according to their velocity.
 * Usually used by long hitboxes
 * @author Frurgnerd Frasky
 */
public class AdjustAngle extends HitboxStrategy {
	
	public AdjustAngle(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
		
		//setting the adjust angle to true upon creating lets the hbox start off at the right angle immediately
		hbox.setAdjustAngle(true);
	}

	private final Vector2 currentVelo = new Vector2();
	@Override
	public void controller(float delta) {
		currentVelo.set(hbox.getLinearVelocity());
		if (hbox.getAngle() != currentVelo.angleDeg()) {
			hbox.setTransform(hbox.getPosition(), MathUtils.atan2(currentVelo.y , currentVelo.x));
		}
	}
}
