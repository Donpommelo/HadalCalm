package com.mygdx.hadal.schmucks.strategies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

public class HitboxBouncyStrategy extends HitboxStrategy{
	
	private boolean removed;
	
	public HitboxBouncyStrategy(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
		removed = false;
	}
	
	@Override
	public void controller(float delta) {
		if (!removed) {
			removed = true;
			if (hbox.isSensor()) {
				hbox.getBody().createFixture(FixtureBuilder.createFixtureDef(hbox.getWidth() / 2 - 2, hbox.getHeight() / 2 - 2, 
						new Vector2(1 / 4 / PPM,  1 / 4 / PPM), false, 0, 0, hbox.getRest(), hbox.getFriction(),
						Constants.BIT_SENSOR, Constants.BIT_WALL, hbox.getFilter()));
			}
		}
	}
}
