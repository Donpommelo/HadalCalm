package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates a ragdoll when its hbox dies.
 * the ragdoll will have the same sprite as the hbox itself
 * @author Ferroway Frasteban
 *
 */
public class DieRagdoll extends HitboxStrategy {
	
	public DieRagdoll(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void die() {
		new Ragdoll(state, this.hbox.getPixelPosition(), hbox.getSize(), hbox.getSprite(), new Vector2(), 0.75f, 1.0f, false, false, true);
	}
}
