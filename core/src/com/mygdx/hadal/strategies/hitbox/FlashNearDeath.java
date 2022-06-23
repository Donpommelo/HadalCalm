package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 */
public class FlashNearDeath extends HitboxStrategy {

	//the hbox will start flashing when its hp falls below this
	private final float flashLifespan;

	//the duration of each flash
	private static final float flashDuration = 0.1f;

	public FlashNearDeath(PlayState state, Hitbox proj, BodyData user, float flashLifespan) {
		super(state, proj, user);
		this.flashLifespan = flashLifespan;
	}
	
	@Override
	public void controller(float delta) {
		if (hbox.getLifeSpan() <= flashLifespan) {
			hbox.setFlashCount(hbox.getFlashCount() - delta);
			if (hbox.getFlashCount() < -flashDuration) {
				hbox.setFlashCount(flashDuration);
			}
		}
	}
}
