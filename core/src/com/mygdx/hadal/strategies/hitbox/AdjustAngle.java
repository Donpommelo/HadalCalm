package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.MathUtils;
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
	
	@Override
	public void controller(float delta) {
		hbox.setTransform(hbox.getPosition(), MathUtils.atan2(hbox.getLinearVelocity().y , hbox.getLinearVelocity().x));
	}
}
