package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This strategy simply makes a hbox static after it has been created.
 * It is used for non-moving hitboxes like explosions
 * @author Zachary Tu
 *
 */
public class HitboxStaticStrategy extends HitboxStrategy{
	
	public HitboxStaticStrategy(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void create() {
		hbox.getBody().setType(BodyType.StaticBody);
	}
}
