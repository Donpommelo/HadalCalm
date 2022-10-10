package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.constants.Constants;

/**
 * this strategy makes a hitbox flash when its lifespan is below a specified threshold. It is usually used for explosive projectiles
 * @author Hugdanoff Hapodilla
 */
public class FlashShaderNearDeath extends HitboxStrategy {

	//the hbox will start flashing when its hp falls below this
	private final float flashLifespan;

	//Does the server notify the client of this flash?
	private final boolean synced;

	public FlashShaderNearDeath(PlayState state, Hitbox proj, BodyData user, float flashLifespan, boolean synced) {
		super(state, proj, user);
		this.flashLifespan = flashLifespan;
		this.synced = synced;
	}
	
	@Override
	public void controller(float delta) {
		if (hbox.getLifeSpan() <= flashLifespan) {
			if (hbox.getShaderCount() < -Constants.FLASH) {
				hbox.setShader(Shader.WHITE, Constants.FLASH, synced);
			}
		}
	}
}
