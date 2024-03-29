package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox die when it touches a unit
 * @author Rashmere Rufferty
 */
public class ContactUnitDie extends HitboxStrategy {
	
	//delay after hbox creation before this strategy will activate. can be set using factory method
	private float delay;
	
	public ContactUnitDie(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void controller(float delta) {
		if (delay >= 0) {
			delay -= delta;
		}
	}
	
	@Override
	public void onHit(HadalData fixB, Body body) {
		if (fixB != null && delay <= 0) {
			if (UserDataType.BODY.equals(fixB.getType())) {
				hbox.die();
			}
		}
	}
	
	public ContactUnitDie setDelay(float delay) {
		this.delay = delay;
		return this;
	}
}
