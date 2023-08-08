package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Slodged;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox inflicts a slow on users that it makes contact with
 * @author Juduardo Jiffonso
 */
public class ContactUnitSlow extends HitboxStrategy {
	
	//the percentage and duration of the slow
	private final float duration, slow;

	//particle that will be rendered on the unit to indicate slow
	private final Particle particle;
	
	public ContactUnitSlow(PlayState state, Hitbox proj, BodyData user, float duration, float slow, Particle particle) {
		super(state, proj, user);
		this.duration = duration;
		this.slow = slow;
		this.particle = particle;
	}
	
	@Override
	public void onHit(HadalData fixB, Body body) {
		if (fixB instanceof BodyData bodyData) {
			bodyData.addStatus(new Slodged(state, duration, slow, creator, bodyData, particle));
		}
	}
}
